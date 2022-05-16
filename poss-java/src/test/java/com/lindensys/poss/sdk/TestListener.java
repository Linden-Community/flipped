package com.lindensys.poss.sdk;

import com.lindensys.poss.sdk.listener.Process;
import com.lindensys.poss.sdk.listener.ProcessListener;

import java.io.InputStream;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/16
 */
public class TestListener<T> implements ProcessListener<T> {

    @Override
    public void onStart() {
        System.out.println("PROCESS START");
    }

    @Override
    public void onProcess(Process process) {
        System.out.println("PROCESS PERCENTAGE: " + process.getPercentage() + "%");
    }

    @Override
    public void onError(String msg, Exception e) {
        System.out.println("PROCESS ERROR: " + msg);
        e.printStackTrace();
    }

    @Override
    public void onComplete(T target) {
        System.out.println("PROCESS COMPLETE");
    }
}
