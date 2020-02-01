package com.liji.proxy.common.exception;

/**
 * @author jili
 * @date 2020/2/1
 */
public class ProxyException extends RuntimeException {
    public ProxyException(String message) {
        super(message);
    }

    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProxyException() {
    }
}

