package com.liji.proxy.common.utils;

import com.liji.proxy.common.model.MessageProto;

/**
 * @author jili
 * @date 2020/1/23
 */
public class MessageFactory {
    public static MessageProto.NewConnectionFromOuter newConnectionFromOuter(String reqId) {
        return MessageProto.NewConnectionFromOuter.newBuilder().setReqId(reqId).build();
    }
}
