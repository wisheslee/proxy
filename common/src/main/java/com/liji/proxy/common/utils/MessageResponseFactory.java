package com.liji.proxy.common.utils;

import com.liji.proxy.common.model.MessageProto;

/**
 * @author jili
 * @date 2020/1/23
 */
public class MessageResponseFactory {
    public static MessageProto.Response success() {
        return MessageProto.Response.newBuilder().setStatus(200).build();
    }

    public static MessageProto.Response fail() {
        return fail(400, null);
    }

    public static MessageProto.Response fail(String msg) {
        return fail(400, msg);
    }

    public static MessageProto.Response fail(int status, String msg) {
        return MessageProto.Response.newBuilder().setStatus(status).setMsg(msg).build();
    }


}
