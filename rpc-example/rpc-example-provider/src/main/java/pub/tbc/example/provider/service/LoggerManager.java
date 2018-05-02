package pub.tbc.example.provider.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Created by tbc on 2018/5/2.
 */
@Slf4j
public class LoggerManager {
    public static void logSetting() {
        StaticLoggerBinder loggerBinder = StaticLoggerBinder.getSingleton();

        LoggerContext loggerContext = (LoggerContext) loggerBinder.getLoggerFactory();
        loggerContext.getLoggerList().forEach(System.out::println);

        Logger root = loggerContext.getLogger("ROOT");
        root.setLevel(Level.DEBUG);
        System.out.println("console appender exist:" + (root.getAppender("console") == null));
        ConsoleAppender console = (ConsoleAppender) root.getAppender("console");
        LayoutWrappingEncoder encoder = (LayoutWrappingEncoder) console.getEncoder();

        PatternLayout layout = new PatternLayout();
        layout.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%-8thread] [%-5level] %logger{36} - %msg%n");

        encoder.setLayout(layout);

        layout.setContext(loggerContext);
        layout.start();

        Logger zkLogger = loggerContext.getLogger("org.apache.zookeeper");
        zkLogger.setLevel(Level.ERROR);
        Logger nettyLogger = loggerContext.getLogger("io.netty");
        nettyLogger.setLevel(Level.INFO);


    }
}
