package com.liji.proxy.common.utils;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.liji.proxy.common.model.MessageProto;

import java.util.UUID;

/**
 * @author jili
 * @date 2020/1/23
 */
public class MessageFactory {

    public static <T extends Message> MessageProto.Message newMessage(T t) {
        return newMessage(t, UUID.randomUUID().toString());
    }

    public static <T extends Message> MessageProto.Message newMessage(T t, String reqId) {
        return MessageProto.Message.newBuilder().setHeader(newHeader(reqId)).setBody(Any.pack(t)).build();
    }

    public static <T extends Message> MessageProto.Message newMessage(T t, MessageProto.Header header) {
        return MessageProto.Message.newBuilder().setHeader(header).setBody(Any.pack(t)).build();
    }

    public static MessageProto.Header newHeader() {
        return newHeader(UUID.randomUUID().toString());
    }

    private static MessageProto.Header newHeader(String reqId) {
        return MessageProto.Header.newBuilder()
                .setTimestamp(System.currentTimeMillis())
                .setVersion(1)
                .setReqId(reqId)
                .build();
    }
}
