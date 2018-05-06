package pub.tbc.rpc.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import pub.tbc.rpc.common.ProviderService;
import pub.tbc.rpc.common.helper.RpcConfigHelper;
import pub.tbc.rpc.common.model.RpcResponse;
import pub.tbc.rpc.remoting.netty.handler.NettyClientInvokerHandler;
import pub.tbc.rpc.remoting.netty.handler.codec.NettyDecoderHandler;
import pub.tbc.rpc.remoting.netty.handler.codec.NettyEncoderHandler;
import pub.tbc.rpc.framework.serializer.SerializerType;
import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.collect.Lists;
import pub.tbc.toolkit.core.collect.Maps;
import pub.tbc.toolkit.core.thread.ExecutorFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

import static java.util.stream.Collectors.toSet;

/**
 * Created by tbc on 2018/4/20.
 */
@Slf4j
public class NettyChannelPoolFactory {
    private static final NettyChannelPoolFactory channelPoolFactory = new NettyChannelPoolFactory();

    //Key为服务提供者地址,value为Netty Channel阻塞队列
    private static final Map<InetSocketAddress, ArrayBlockingQueue<Channel>> channelPoolMap = Maps.newConcurrentHashMap();

    //初始化Netty Channel阻塞队列的长度,该值为可配置信息
    private static final int channelConnectSize = RpcConfigHelper.getChannelConnectSize();

    //初始化序列化协议类型,该值为可配置信息
    private static final SerializerType serializeType = RpcConfigHelper.getSerializeType();

    //服务提供者列表
    private List<ProviderService> serviceMetaDataList = Lists.newArrayList();

    public static NettyChannelPoolFactory instance() {
        log.debug("获取NettyChannelPoolFactory实例: NettyChannelPoolFactory:{}", Thread.currentThread().getStackTrace()[1].getMethodName());
        return channelPoolFactory;
    }

    {
        log.debug("init {}", getClass());
    }

    /**
     * 处理器列表
     */
    private ChannelInitializer<SocketChannel> channelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                        // netty encoder
                        .addLast("EncoderHandler", new NettyEncoderHandler(serializeType))
                        // netty decoder
                        .addLast("DecoderHandler", new NettyDecoderHandler(RpcResponse.class, serializeType))
                        // client handler
                        .addLast("ClientInvokerHandler", new NettyClientInvokerHandler());
            }
        };
    }

    private InetSocketAddress newInetSocketAddress(ProviderService providerService) {
        return new InetSocketAddress(providerService.getServerIp(), providerService.getServerPort());
    }

    /**
     * 初始化Netty Channel连接队列 Map
     */
    public void initChannelPoolFactory(Map<String, List<ProviderService>> providerMap) {
        // 将服务提供者信息存入 serviceMetaDataList
        providerMap.values().stream().filter(EmptyUtil::nonEmpty).forEach(serviceMetaDataList::addAll);

        // 获取服务提供者地址列表
        Set<InetSocketAddress> socketAddresses = serviceMetaDataList.stream().map(this::newInetSocketAddress).collect(toSet());

        // 根据服务提供者地址列表初始化Channel阻塞队列， 并以地址为Key，地址对应的Channel阻塞队列为value， 存入channelPoolMap
        socketAddresses.forEach(socketAddress -> {
            try {
                int realChannelConnectSize = 0;
                while (realChannelConnectSize < channelConnectSize) {
                    Channel channel = null;
                    while (EmptyUtil.isEmpty(channel)) {
                        channel = registerChannel(socketAddress);
                    }
                    // 计数器，初始化的时候存入阻塞队列的Channel个数不超过channelConnectSize
                    realChannelConnectSize++;

                    // 将新注册的Channel存入阻塞队列，并将阻塞队列作为value存入channelPoolMap
                    ArrayBlockingQueue<Channel> channelArrayBlockingQueue = channelPoolMap.get(socketAddress);
                    if (EmptyUtil.isNull(channelArrayBlockingQueue)) {
                        channelArrayBlockingQueue = new ArrayBlockingQueue<>(channelConnectSize);
                        channelPoolMap.put(socketAddress, channelArrayBlockingQueue);
                    }
                    channelArrayBlockingQueue.offer(channel);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });


    }

    /**
     * 根据服务提供者地址获取对应的Netty Channel阻塞队列
     */
    public ArrayBlockingQueue<Channel> acquire(InetSocketAddress socketAddress) {
        return channelPoolMap.get(socketAddress);
    }

    /**
     * channel使用完，回收到阻塞队列中
     */
    public void release(ArrayBlockingQueue<Channel> arrayBlockingQueue, Channel channel, InetSocketAddress socketAddress) {
        if (EmptyUtil.isNull(arrayBlockingQueue)) {
            return;
        }
        //回收之前先检查channel是否可用(非空、活跃、打开、可写),不可用的话,重新注册一个,放入阻塞队列
        if (EmptyUtil.isNull(channel) || !channel.isActive() || !channel.isOpen() || !channel.isWritable()) {
            if (EmptyUtil.nonNull(channel)) {
                channel.deregister().syncUninterruptibly().awaitUninterruptibly();
                channel.closeFuture().syncUninterruptibly().awaitUninterruptibly();
            }
            Channel newChannel = null;
            while (EmptyUtil.isNull(newChannel)) {
                log.debug("---------register new Channel-------------");
                newChannel = registerChannel(socketAddress);
            }
            arrayBlockingQueue.offer(newChannel);
            return;
        }
        arrayBlockingQueue.offer(channel);
    }

    public Channel registerChannel(InetSocketAddress socketAddress) {
        try {
            EventLoopGroup group = new NioEventLoopGroup(10);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.remoteAddress(socketAddress);

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(channelInitializer());

            ChannelFuture channelFuture = bootstrap.connect().sync();
            Channel newChannel = channelFuture.channel();

            CountDownLatch connectedLatch = new CountDownLatch(1);
            List<Boolean> isSuccessHolder = Lists.newArrayListWithCapacity(1);
            // 监听channel是否建立成功
            channelFuture.addListener(future -> {
                // 若Channel建立成功，保存建立成功的标记
                if (future.isSuccess()) {
                    isSuccessHolder.add(true);
                } else {
                    // 否则，保存建立失败的标记
                    isSuccessHolder.add(false);
                }
                connectedLatch.countDown();
            });

            connectedLatch.await();
            // 若channel建立成功 ，返回新建的Channel
            if (isSuccessHolder.get(0)) {
                return newChannel;
            }

        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }


}
