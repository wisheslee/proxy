package com.liji.proxy.common.utils;

import com.google.protobuf.Any;
import com.liji.proxy.common.model.MessageProto;

/**
 * @author jili
 * @date 2020/1/23
 */
public class MessageFactory {
    public static MessageProto.NewConnectionFromOuter newConnectionFromOuter(String reqId, String localHost, int localPort) {
        return MessageProto.NewConnectionFromOuter.newBuilder().setReqId(reqId).setLocalHost(localHost).setLocalPort(localPort).build();
    }

    public static <T extends com.google.protobuf.Message> MessageProto.Message wrap(T t) {
        return MessageProto.Message.newBuilder().setMessageBody(Any.pack(t)).build();
    }
}
