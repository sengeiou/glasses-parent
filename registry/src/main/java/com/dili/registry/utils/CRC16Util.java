package com.dili.registry.utils;

import java.math.BigInteger;

/**
 * 处理串口指令的工具类
 **/

public class CRC16Util {

    private static String getCRC16(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return Integer.toHexString(CRC);
    }

    /**
     2  * 16进制直接转换成为字符串(无需Unicode解码)
     3  * @param hexStr
     4  * @return
     5  */
    private static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    public static String getCRC16Format(String str){
        String crc = getCRC16(str.getBytes());
        BigInteger bigint=new BigInteger(crc, 16);
        return String.format("%06d", bigint.intValue());
    }

    /**
     * @param args
     */
    public static void main1(String[] args) {
        // TODO Auto-generated method stub
        String sbuf = "00,862177040027891,0037,0001";
        System.out.println(getCRC16Format(sbuf));
    }

}

