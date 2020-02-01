package com.liji.proxy.common.config;

import com.liji.proxy.common.exception.ConfigException;

import java.util.*;
import java.util.function.Function;

/**
 * @author jili
 * @date 2020/2/1
 */
public class ConfigResolverImpl implements ConfigResolver{

    private Map<String, Object> configMap;

    public ConfigResolverImpl(Map<String, Object> configMap) {
        Objects.requireNonNull(configMap);
        this.configMap = configMap;
    }

    @Override
    public long getLong(String key) throws ConfigException {
        return wrap(key, k -> (long) configMap.get(k));
    }

    @Override
    public int getInt(String key) throws ConfigException {
        return wrap(key, k -> (int) configMap.get(k));
    }

    @Override
    public String getString(String key) throws ConfigException {
        return wrap(key, k -> (String) configMap.get(k));
    }

    @Override
    public boolean getBoolean(String key) throws ConfigException {
        return wrap(key, k -> (boolean) configMap.get(k));
    }

    @Override
    public List<Long> getLongList(String key) throws ConfigException {
        return wrap(key, k -> {
            String str = (String) configMap.get(k);
            String[] strings = str.split(LIST_SEPARATOR);
            List<Long> list = new LinkedList<>();
            for (String value : strings) {
                list.add(Long.valueOf(value));
            }
            return list;
        });
    }

    @Override
    public List<Integer> getIntList(String key) throws ConfigException {
        return wrap(key, k -> {
            String str = (String) configMap.get(k);
            String[] strings = str.split(LIST_SEPARATOR);
            List<Integer> list = new LinkedList<>();
            for (String value : strings) {
                list.add(Integer.valueOf(value));
            }
            return list;
        });
    }

    @Override
    public List<String> getStringList(String key) throws ConfigException {
        return wrap(key, k -> {
            String str = (String) configMap.get(k);
            String[] strings = str.split(LIST_SEPARATOR);
            return Arrays.asList(strings);
        });
    }

    private <T> T wrap(String key, Function<String, T> function) {
        try {
            return function.apply(key);
        } catch (Exception e) {
            throw new ConfigException(key + "related value can not convert to appointed type");
        }
    }

}
