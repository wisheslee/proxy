package com.liji.proxy.common.handler;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author jili
 * @date 2020/1/29
 */
public class GlobalSharableHandlerFactory {
    private static LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);
    private static ExceptionHandler exceptionHandler = new ExceptionHandler();

    public static LoggingHandler getLoggingHandler() {
        return loggingHandler;
    }

    public static ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }
}
