package com.liji.proxy.common.exception;

/**
 * @author jili
 * @date 2020/2/1
 */
public class ConfigException extends  ProxyException{
    public ConfigException() {
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
