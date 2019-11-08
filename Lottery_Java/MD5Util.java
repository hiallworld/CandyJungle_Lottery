package com.marcopolo.candyplatform.common.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Random;

/**
 * MD5工具类
 */
public class MD5Util {

    private static final Logger logger = LoggerFactory.getLogger(MD5Util.class);

    private static final String HEXDIGITS[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 生成含有随机盐的密码
     */
    public static String generate(String password) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 校验密码是否正确
     */
    public static boolean verify(String password, String md5) {
        if (null == password || "".equals(password) || null == md5 || "".equals(md5)){
            return false;
        }
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5Hex(password + salt).equals(new String(cs1));
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    public static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    // 固定MD5（不推荐，如无特殊需求请使用上面的随机盐算法）
    public static String encodeMD5(String sString) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        md5.update(sString.getBytes());
        byte[] abResult = md5.digest();

        return convertByte2Hex(abResult);
    }

    public static String convertByte2Hex(byte[] abValue) {
        if (abValue == null) {
            return null;
        }

        String sTemp;
        StringBuffer sbHex = new StringBuffer();

        for (int i = 0; i < abValue.length; i++) {
            sTemp = Integer.toHexString(abValue[i] & 0XFF);
            if (1 == sTemp.length()) {
                sbHex.append('0').append(sTemp);
            } else {
                sbHex.append(sTemp);
            }
        }

        return sbHex.toString();
    }

    /**
     * 以下为长软方面提供的MD5加密算法
     */

    //md5
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++){
            resultSb.append(byteToHexString(b[i]));
        }

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0){
            n += 256;
        }
           
        int d1 = n / 16;
        int d2 = n % 16;
        return HEXDIGITS[d1] + HEXDIGITS[d2];
    }

    public static String MD5Encode(String origin, String charsetName) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetName == null || "".equals(charsetName)){
                resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
            } else{
                resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes(charsetName)));
            }
        } catch (Exception exception) {
        }
        return resultString;
    }

    public static String MD5Encode(String origin) {
        String charsetName = "UTF-8";
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetName == null || "".equals(charsetName)){
                resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes())); 
            } else{
                resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes(charsetName)));
            }
        } catch (Exception exception) {
        }
        return resultString;
    }

    public static void main(String[] args) {
        String password = generate("123456");
        System.out.println(password);
        String password1 = generate("123456");
        System.out.println(password1);
        System.out.println(verify("123456", password));
        System.out.println(verify("123456", password1));


        boolean verifyResult = MD5Util.verify("123456", "");
        System.out.println(verifyResult);
    }

}
