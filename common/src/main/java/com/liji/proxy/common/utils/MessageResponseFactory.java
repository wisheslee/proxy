package com.liji.proxy.common.utils;

import com.liji.proxy.common.model.MessageProto;

/**
 * @author jili
 * @date 2020/1/23
 */
public class MessageResponseFactory {
    public static MessageProto.Message success(MessageProto.Header header) {
        return MessageFactory.newMessage(MessageProto.Response.newBuilder().setStatus(200).build(),
                header);
    }

    public static MessageProto.Message fail(MessageProto.Header header) {
        return fail(header, 400, "");
    }

    public static MessageProto.Message fail(MessageProto.Header header, String msg) {
        return fail(header, 400, msg);
    }

    public static MessageProto.Message fail(MessageProto.Header header, int status, String msg) {
        return MessageFactory.newMessage(MessageProto.Response.newBuilder().setStatus(status).setMsg(msg).build(),
                header);
    }
}
