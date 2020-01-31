package com.liji.proxy.common.config;

import com.liji.proxy.common.model.Server;

/**
 * @author jili
 * @date 2020/1/30
 */

public interface Config {
    /**
     * 获取服务端管理服务
     *
     * @param
     * @return java.lang.String
     * @author jili
     * @date 2020/1/30
     */
    Server getServerManagement();

    /**
     * 获取服务端数据转发服务
     *
     * @param
     * @return int
     * @author jili
     * @date 2020/1/30
     */
    Server getServerData();

    String getServerManagementSecret();

    int getServerDataReqIdLength();
}
