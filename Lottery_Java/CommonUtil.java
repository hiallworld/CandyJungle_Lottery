package com.marcopolo.candyplatform.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;

public class CommonUtil {
    public static final SimpleDateFormat LONG_LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    
    /**
     * 获取指定位数的随机数
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 获取随机抽奖码
     * @param walletAddress 钱包地址
     * @param times 次数
     * @param rondomNumber 随机码
     * @return
     */
    public static String getRandomLotteryNumber(
        String walletAddress, int times, String rondomNumber) {
        // 拼接随机数
        String lotteryNumbers = "";
        for (int i = 0; i < times; i++) {
            String longTime = getLongLongDateStr();
            String str = MD5Util.generate(walletAddress + longTime + i + rondomNumber);
            
            //字符串替换数字
            String newStr = "";
            for(int j=0;j<str.length();j++){
                if(isInteger(String.valueOf(str.charAt(j)))){
                    newStr = newStr.concat(String.valueOf(str.charAt(j)));
                }else{
                    newStr = newStr.concat(String.valueOf(letterToNumber(String.valueOf(str.charAt(j)))));
                }
            }

            String lotteryNumber = newStr.substring(0, 7);
            int newStrSum = 0;
            for(int k=0;k<newStr.length();k++){
                newStrSum = newStrSum + Integer.parseInt(String.valueOf(newStr.charAt(k)));
            }
            
            newStrSum = newStrSum % 10;
            lotteryNumber = lotteryNumber.concat(String.valueOf(newStrSum));
            lotteryNumbers = lotteryNumbers.concat(lotteryNumber).concat(",");
        }

        return (lotteryNumbers.length() > 0) ? lotteryNumbers.substring(0, (lotteryNumbers.length() - 1)) : "";
    }

    //字母转数字
    public static int letterToNumber(String letter) {
        int length = letter.length();
        int num = 0;
        int number = 0;
        for (int i = 0; i < length; i++) {
            char ch = letter.charAt(length - i - 1);
            num = (int)(ch - 'A' + 1);
            num *= Math.pow(26, i);
            number += num;
        }
        return number;
    }

    //判断是否为数字
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    
    public static String getLongLongDateStr() {
        return LONG_LONG_DATE_FORMAT.format(new Date());
    }
    
    public static void main(String[] args) {
        System.out.println(getRandomLotteryNumber("0x898bA8603b7afa7B6A4f6720280A0EF28C312a77",100,"234"));
    }
}
