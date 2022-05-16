package com.lindensys.poss.sdk.listener;

import java.util.EventListener;

/**
 * @author Jiang Shunzhi
 * @date 2022/5/12
 */
public interface ProcessListener<T> extends EventListener {

    default void onStart() {}

    default void onProcess(Process process) {}

    default void onStatusChange() {}

    default void onError(String msg, Exception e) {}

    default void onComplete(T target) {}

}
