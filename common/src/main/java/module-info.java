module com.liji.proxy.common {
    requires com.google.protobuf;
    requires static lombok;
    requires slf4j.api;
    requires io.netty.all;

    exports com.liji.proxy.common.model;
    exports com.liji.proxy.common.utils;
    exports com.liji.proxy.common.constants;
}
