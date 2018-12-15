package com.gexincloud.springboot.shiro.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class ShiroMd5Util {

    public static String getMd5Str(String password, String salt) {
        String hashAlgorithmName = "MD5";
        Object credentials = password;
        Object salt1 = ByteSource.Util.bytes(salt);
        int hashIterations = 1024;
        Object result = new SimpleHash(hashAlgorithmName, credentials, salt1, hashIterations);
        return String.valueOf(result);
    }
}
