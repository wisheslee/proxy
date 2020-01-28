package com.liji.proxy.common.utils;

import com.liji.proxy.common.model.MessageProto;

/**
 * @author jili
 * @date 2020/1/23
 */
public class MessageResponseFactory {
    public static MessageProto.Message success() {
        return MessageFactory.wrap(MessageProto.Response.newBuilder().setStatus(200).build());
    }

    public static MessageProto.Message fail() {
        return fail(400, null);
    }

    public static MessageProto.Message fail(String msg) {
        return fail(400, msg);
    }

    public static MessageProto.Message fail(int status, String msg) {
        return MessageFactory.wrap(MessageProto.Response.newBuilder().setStatus(status).setMsg(msg).build());
    }


}
