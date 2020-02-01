package com.liji.proxy.server.common.config;

import com.liji.proxy.common.config.AbstractConfigReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author jili
 * @date 2020/2/1
 */
public class ServerConfigReader extends AbstractConfigReader {

    private static final String DEFAULT_CONFIG_PATH = "server.config";
    private static final String CUSTOM_CONFIG_PATH = "../server.config";

    @Override
    protected InputStream getDefaultConfigStream() {
        return getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_PATH);
    }

    @Override
    protected InputStream getCustomConfigStream() {
        return getOutJarFileInputStream(CUSTOM_CONFIG_PATH);
    }
}
