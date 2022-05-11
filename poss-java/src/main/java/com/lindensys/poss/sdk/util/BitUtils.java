package com.lindensys.poss.sdk.util;

/**
 * 字节数组工具类
 *
 * @author Jiang Shunzhi
 * @date 2021/6/7
 */
public class BitUtils {

    /**
     * 向前填充0数据到字节数组到指定长度。若原数组长度超过指定长度则原样返回
     *
     * @param source 原字节数组
     * @param length 指定填充后长度
     * @return 填充后字节数组
     */
    public static byte[] paddingBytes(byte[] source, int length) {
        return paddingBytes(source, length, true);
    }

    /**
     * 填充0数据到字节数组到指定长度。若原数组长度超过指定长度则原样返回
     *
     * @param source 原字节数组
     * @param length 指定填充后长度
     * @param pre    是否向前填充，false则向后填充
     * @return 填充后字节数组
     */
    public static byte[] paddingBytes(byte[] source, int length, boolean pre) {
        if (source.length > length) {
            return source;
        }
        byte[] target = new byte[length];
        if (pre) {
            for (int i = source.length - 1, j = 1; i >= 0; i--, j++) {
                target[target.length - j] = source[i];
            }
        } else {
            System.arraycopy(source, 0, target, 0, source.length);
        }
        return target;
    }

}
