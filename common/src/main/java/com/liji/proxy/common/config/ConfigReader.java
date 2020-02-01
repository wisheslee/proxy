package com.liji.proxy.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author jili
 * @date 2020/2/1
 */
public interface ConfigReader {

    String KEY_VALUE_SEPERATOR = "=";

    Map<String, Object> getConfig();

}
