package com.xiaochen.starter.test.util;

public class ThreadUtil {

    /**
     * 主程永久阻塞
     */
    public static void joinThread() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 主程阻塞指定时间
     *
     * @param millis
     */
    public static void joinThread(long millis) {
        try {
            Thread.currentThread().join(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 睡眠
     *
     * @param millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
