package com.lindensys.poss.sdk.listener;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/16
 */
public class Process {

    private final transient int size;

    private final transient int proceeding;

    public Process(int size, int proceeding) {
        this.size = size;
        this.proceeding = proceeding;
    }

    public int getSize() {
        return size;
    }

    public int getProceeding() {
        return proceeding;
    }

    public double getPercentage() {
        return BigDecimal.valueOf(proceeding)
                .divide(BigDecimal.valueOf(size),4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2,RoundingMode.HALF_UP)
                .doubleValue();
    }

}
