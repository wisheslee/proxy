package com.liji.proxy.common.config;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jili
 * @date 2020/2/1
 */
@Slf4j
public abstract class AbstractConfigReader implements ConfigReader {

    @Override
    public Map<String, String> getConfig() {
        InputStream defaultConfigStream = getDefaultConfigStream();
        Map<String, String> defaultConfig = decodeConfig(defaultConfigStream);
        InputStream customConfigStream = getCustomConfigStream();
        Map<String, String> customConfig = decodeConfig(customConfigStream);
        quietClose(defaultConfigStream);
        quietClose(customConfigStream);
        return mix(defaultConfig, customConfig);
    }

    protected abstract InputStream getDefaultConfigStream();

    protected abstract InputStream getCustomConfigStream();

    protected InputStream getOutJarFileInputStream(String relativePath) {
        String jarDirPath = System.getProperty("user.dir");
        try {
            URL jarDir = new File(jarDirPath).toURI().toURL();
            String path = new URL(jarDir, relativePath).getPath();
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return new FileInputStream(file);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Map<String, String> decodeConfig(InputStream inputStream) {
        Map<String, String> map = new HashMap<>();
        if (inputStream == null) {
            return map;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                String[] strs = line.split(ConfigReader.KEY_VALUE_SEPERATOR);
                map.put(strs[0], strs[1]);
            }
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> mix(Map<String, String> defaultMap, Map<String, String> customMap) {
        for (Map.Entry<String, String> customEntry : customMap.entrySet()) {
            defaultMap.put(customEntry.getKey(), customEntry.getValue());
        }
        return defaultMap;
    }

    private void quietClose(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
