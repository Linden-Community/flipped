package com.lindensys.poss.sdk.util;


import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Base58工具类
 *
 * @author Jiang Shunzhi
 */
public class Base58 {

    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    private static final int[] INDEXES = new int[128];

    private static final char BOM = '\uFEFF';

    static {
        Arrays.fill(INDEXES, -1);
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEXES[ALPHABET[i]] = i;
        }
    }

    /**
     * 将字节数据编码为Base58字符串
     *
     * @param input 字节数组数据
     * @return Base58字符串
     */
    public static String encode(byte[] input) {
        if (input.length == 0) {
            return "";
        }
        input = copyOfRange(input, 0, input.length);
        // Count leading zeroes.
        int zeroCount = 0;
        while (zeroCount < input.length && input[zeroCount] == 0) {
            ++zeroCount;
        }
        // The actual encoding.
        byte[] temp = new byte[input.length * 2];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input.length) {
            byte mod = divMod58(input, startAt);
            if (input[startAt] == 0) {
                ++startAt;
            }
            temp[--j] = (byte) ALPHABET[mod];
        }

        // Strip extra '1' if there are some after decoding.
        while (j < temp.length && temp[j] == ALPHABET[0]) {
            ++j;
        }
        // Add as many leading '1' as there were leading zeros.
        while (--zeroCount >= 0) {
            temp[--j] = (byte) ALPHABET[0];
        }

        byte[] output = copyOfRange(temp, j, temp.length);
        return new String(output, StandardCharsets.US_ASCII);
    }

    /**
     * 将字符数组的Base58数据转码为真实数据
     *
     * @param input 字符数组的Base58数据
     * @return 字节数组数据
     */
    public static byte[] decode(char[] input) {

        if (input.length == 0) {
            return new byte[0];
        }
        int inputLen = input.length;
        int startIndex = 0;
        if (input[0] == BOM) {
            inputLen--;
            startIndex = 1;
        }

        byte[] input58 = new byte[inputLen];
        // Transform the String to a base58 byte sequence
        for (int i = startIndex; i < inputLen; ++i) {
            char c = input[i];

            int digit58 = -1;
            if (c < 128) {
                digit58 = INDEXES[c];
            }
            if (digit58 < 0) {
                return null;
            }

            input58[i] = (byte) digit58;
        }
        // Count leading zeroes
        int zeroCount = 0;
        while (zeroCount < input58.length && input58[zeroCount] == 0) {
            ++zeroCount;
        }
        // The encoding
        byte[] temp = new byte[inputLen];
        int j = temp.length;

        int startAt = zeroCount;
        while (startAt < input58.length) {
            byte mod = divMod256(input58, startAt);
            if (input58[startAt] == 0) {
                ++startAt;
            }

            temp[--j] = mod;
        }
        // Do no add extra leading zeroes, move j to first non null byte.
        while (j < temp.length && temp[j] == 0) {
            ++j;
        }

        return copyOfRange(temp, j - zeroCount, temp.length);
    }

    /**
     * 将字符串的Base58数据转码为真实数据
     *
     * @param input 字符串的Base58数据
     * @return 字节数组数据
     */
    public static byte[] decode(String input) {

        if (input.length() == 0) {
            return new byte[0];
        }
        return decode(input.toCharArray());
    }

    /**
     * 将字符串的Base58数据转码为正数的超整型数据
     *
     * @param input 字符串的Base58数据
     * @return 超级整型数据
     */
    public static BigInteger decodeToBigInteger(String input) {
        return new BigInteger(1, decode(input));
    }

    /**
     * number -> number / 58, returns number % 58
     */
    private static byte divMod58(byte[] number, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number.length; i++) {
            int digit256 = (int) number[i] & 0xFF;
            int temp = remainder * 256 + digit256;

            number[i] = (byte) (temp / 58);

            remainder = temp % 58;
        }

        return (byte) remainder;
    }

    /**
     * number -> number / 256, returns number % 256
     */
    private static byte divMod256(byte[] number58, int startAt) {
        int remainder = 0;
        for (int i = startAt; i < number58.length; i++) {
            int digit58 = (int) number58[i] & 0xFF;
            int temp = remainder * 58 + digit58;

            number58[i] = (byte) (temp / 256);

            remainder = temp % 256;
        }

        return (byte) remainder;
    }

    private static byte[] copyOfRange(byte[] source, int from, int to) {
        byte[] range = new byte[to - from];
        System.arraycopy(source, from, range, 0, range.length);

        return range;
    }
}