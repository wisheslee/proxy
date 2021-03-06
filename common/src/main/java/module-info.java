module com.liji.proxy.common {
    requires com.google.protobuf;
    requires static lombok;
    requires slf4j.api;
    requires io.netty.all;

    exports com.liji.proxy.common;
    exports com.liji.proxy.common.model;
    exports com.liji.proxy.common.utils;
    exports com.liji.proxy.common.constants;
    exports com.liji.proxy.common.handler;
    exports com.liji.proxy.common.config;
    exports com.liji.proxy.common.exception;
}
