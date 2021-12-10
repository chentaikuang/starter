package com.xiaochen.starter.dingtalk.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class IPUtil {

    public static String get(){
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress()+" ("+address.getHostName()+")";
        } catch (UnknownHostException e) {
            log.error("获取ip异常",e);
            return "";
        }
    }
}
