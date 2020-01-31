package com.liji.proxy.common.config;

/**
 * @author jili
 * @date 2020/1/31
 */
public abstract class AbstractConfig implements Config {
    @Override
    public int getServerDataReqIdLength() {
        return 36;
    }
}
