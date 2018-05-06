import pub.tbc.toolkit.core.EmptyUtil;
import pub.tbc.toolkit.core.Sleeps;
import pub.tbc.toolkit.core.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auth tbc on 2018/5/6.
 */
public class 红包 {
    private final int 金额;
    private final int 红包数量;
    private volatile int 已发出红包数量 = 0;
    private volatile int 已发红包包金额 = 0;
    private Random 随机数生成器 = new Random();
    private Map<String, Double> 抢到红包的人和金额 = Maps.newHashMap();

    public 红包(int 金额, int 红包数量) {
        this.金额 = 金额;
        this.红包数量 = 红包数量;
    }

    public synchronized int 随机红包金额(String 抢到红包的人) {
        int 本次发放金额;
        int 剩余红包数量 = 红包数量 - 已发出红包数量;
        switch (剩余红包数量) {
            case 0:
                return 0;
            case 1:
                本次发放金额 = 金额 - 已发红包包金额;
                break;
            default:
                int 随机上限 = 金额 - 已发红包包金额 - 剩余红包数量 + 2;
                本次发放金额 = EmptyUtil.ifZero(随机数生成器.nextInt(随机上限), 1);
        }
        已发出红包数量++;
        已发红包包金额 += 本次发放金额;
        抢到红包的人和金额.put(抢到红包的人, 本次发放金额 / 100.0);
        Sleeps.milliseconds(100);
        return 本次发放金额;
    }

    public int 抢红包(String 抢红包的人) {
        boolean 红包是否发完 = 已发出红包数量 == 红包数量;
        if (红包是否发完) {
            return 0;
        }
        return 随机红包金额(抢红包的人);
    }

    public List<String> 抢红包的人列表() {
        return 抢红包的人列表();
    }

    public int 剩余红包数量() {
        return 红包数量 - 已发出红包数量;
    }

    public void test() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(200);

        AtomicInteger 统计 = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(110000);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 110000; i++) {
            String 抢红包的人 = "抢红包的人" + i + "号";
            executor.execute(() -> {
                int 抢到的红包金额 = 抢红包(抢红包的人);
                if (抢到的红包金额 == 0) {
//                    System.out.println("对不起，" + 抢红包的人 + "，您手速太慢，建议多撸");
                } else {
                    System.out.println("恭喜，" + 抢红包的人 + "抢到了红包，金额（元）：" + (抢到的红包金额 / 100.0) + "||" + Thread.currentThread().getName());
                    if (统计.addAndGet(抢到的红包金额) == 1000) {
                        System.out.println("1000 ++++++++++++++++++++++++++++++++++++++++++++++++");
                    }
                }
                latch.countDown();
            });
        }
        executor.shutdown();
        latch.await();
        System.out.println("十万人抢15个红包，共耗时：" + (System.currentTimeMillis() - start));
        System.out.println("抢红包情况：");
        抢到红包的人和金额.forEach((抢到红包的人, 抢到的金额) -> System.out.println("抢到红包的人: " + 抢到红包的人 + ", 抢到的金额" + 抢到的金额));
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println(0x7fffffff);
        红包 我发的红包 = new 红包(10 * 100, 15);
        我发的红包.test();

    }
}
