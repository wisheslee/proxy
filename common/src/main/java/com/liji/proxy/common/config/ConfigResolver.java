package com.liji.proxy.common.config;

import com.liji.proxy.common.exception.ConfigException;

import java.util.List;

/**
 * @author jili
 * @date 2020/2/1
 */

public interface ConfigResolver {

    String LIST_SEPARATOR = ",";

    long getLong(String key);

    int getInt(String key);

    String getString(String key);

    boolean getBoolean(String key);

    List<Long> getLongList(String key);

    List<Integer> getIntList(String key);

    List<String> getStringList(String key);
}
