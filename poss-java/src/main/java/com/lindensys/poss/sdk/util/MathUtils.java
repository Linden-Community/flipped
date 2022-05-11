package com.lindensys.poss.sdk.util;

import org.bouncycastle.math.ec.ECConstants;

import java.math.BigInteger;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/10
 */
public class MathUtils {

    private static final BigInteger NEGATIVE_ONE = BigInteger.ONE.negate();

    /**
     * Returns k such that b^k = 1 (mod p)
     * @param p
     * @param b
     * @return
     */
    private static BigInteger order(BigInteger p, BigInteger b) {
        if (!p.gcd(b).equals(ECConstants.ONE))
        {
            System.out.println("p and b are" +
                    "not co-prime.");
            return NEGATIVE_ONE;
        }

        // Initializing k with first
        // odd prime number
        BigInteger k = ECConstants.THREE;
        while (true)
        {
            if (b.modPow(k,p).equals(ECConstants.ONE)) {
                return k;
            }
            k = k.add(ECConstants.ONE);
        }
    }

    /**
     * function return p - 1 (= x argument) as x * 2^e, where x will be odd
     * sending e as reference because update is needed in actual e
     * @param x
     * @return
     */
    private static BigInteger[] convertX2E(BigInteger x) {
        BigInteger z = ECConstants.ZERO;
        while (x.mod(ECConstants.TWO).equals(ECConstants.ZERO)) {
            x = x.divide(ECConstants.TWO);
            z = z.add(ECConstants.ONE);
        }
        return new BigInteger[]{x,z};
    }

    /**
     * Main function for finding the modular square root with Shanks-Tonelli algorithm
     * @param n
     * @param p
     * @return
     */
    public static BigInteger modSqrt(BigInteger n, BigInteger p) {
        if (!n.gcd(p).equals(ECConstants.ONE))
        {
            System.out.println("n and p are not co-prime");
            return NEGATIVE_ONE;
        }

        if (n.modPow(p.subtract(ECConstants.ONE).divide(ECConstants.TWO),p).equals(p))
        {
            System.out.println("no sqrt possible");
            return NEGATIVE_ONE;
        }

        BigInteger[] seArray = convertX2E(p.subtract(ECConstants.ONE));
        BigInteger s = seArray[0], r = seArray[1];

        int q;
        for (q = ECConstants.TWO.intValue(); ; q++)
        {
            if (BigInteger.valueOf(q).modPow(p.subtract(ECConstants.ONE).divide(ECConstants.TWO),p)
                    .equals(p.subtract(ECConstants.ONE))) {
                break;
            }
        }

        BigInteger x = n.modPow(s.add(ECConstants.ONE).divide(ECConstants.TWO),p);
        BigInteger b = n.modPow(s,p);
        BigInteger g = BigInteger.valueOf(q).modPow(s,p);

        while (true)
        {
            int m;
            for (m = 0; BigInteger.valueOf(m).compareTo(r)<0; m++)
            {
                if (order(p, b).equals(NEGATIVE_ONE)) {
                    return NEGATIVE_ONE;
                }

                if (order(p, b).intValue() == Double.valueOf(Math.pow(2, m)).intValue()) {
                    break;
                }
            }
            if (m == 0) {
                return x;
            }

            x = x.multiply(g.modPow(BigInteger.valueOf((int)Math.pow(2, r.intValue() - m - 1)),p))
                    .mod(p);
            g = g.modPow(BigInteger.valueOf((int)Math.pow(2, r.intValue() - m)),p);
            b = b.multiply(g).mod(p);
            if (b.equals(ECConstants.ONE)) {
                return x;
            }
            r = BigInteger.valueOf(m);
        }
    }

}
