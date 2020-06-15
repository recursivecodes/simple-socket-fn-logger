import static ch.qos.logback.classic.Level.DEBUG

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("CONSOLE", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        //pattern = "%msg%n"
        pattern = "%cyan(%d{MMM dd HH:mm:ss.SSS}) %gray([%thread]) %highlight(%-5level) %magenta(%logger{36}) - %msg%n"
    }
}
root(DEBUG, ["CONSOLE"])