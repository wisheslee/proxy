package com.liji.proxy.client.common.config;

import com.liji.proxy.common.config.AbstractConfigReader;
import com.liji.proxy.common.exception.ProxyException;

import java.io.InputStream;

/**
 * @author jili
 * @date 2020/2/1
 */
public class ClientConfigReader extends AbstractConfigReader {
    private static final String DEFAULT_CONFIG_PATH = "client.config";
    private static final String CUSTOM_CONFIG_PATH = "../client.config";

    @Override
    protected InputStream getDefaultConfigStream() {
        return getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH);
    }

    @Override
    protected InputStream getCustomConfigStream() {
        InputStream inputStream = getOutJarFileInputStream(CUSTOM_CONFIG_PATH);
        if (inputStream == null) {
            throw new ProxyException("请配置client.config");
        }
        return inputStream;
    }
}
