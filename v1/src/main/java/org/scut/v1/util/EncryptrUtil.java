package org.scut.v1.util;

import lombok.extern.slf4j.Slf4j;
import java.util.Base64;

import java.security.MessageDigest;

@Slf4j
public class EncryptrUtil {

    public static String md5Encrypt(String s) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder base64Encoder = Base64.getEncoder();
            return base64Encoder.encodeToString(md5.digest(s.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("MD5加密失效");
        }
        return s;
    }

}