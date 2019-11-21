package com.asianwallets.common.utils;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadUtil {

    public static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(SystemUtil.cupNum());

    private ThreadUtil() {

    }

    /**
     * 休息N天
     *
     * @param N 天
     */
    public static void SET_DAYS(int N) {
        log.debug("休息  " + N + " 天开始。");
        try {
            TimeUnit.DAYS.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 天出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 天结束。");
    }

    /**
     * 休息N小时
     *
     * @param N 小时
     */
    public static void SET_HOURS(int N) {
        log.debug("休息  " + N + " 小时开始。");
        try {
            TimeUnit.HOURS.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 小时出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 小时结束。");
    }

    /**
     * 休息N毫秒
     *
     * @param N 毫秒
     */
    public static void SET_MILLISECONDS(int N) {
        log.debug("休息  " + N + " 毫秒开始。");
        try {
            TimeUnit.MILLISECONDS.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 毫秒出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 毫秒结束。");
    }

    /**
     * 休息N微秒
     *
     * @param N 微秒
     */
    public static void SET_MICROSECONDS(int N) {
        log.debug("休息  " + N + " 微秒开始。");
        try {
            TimeUnit.MICROSECONDS.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 微秒出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 微秒结束。");
    }

    /**
     * 休息N纳秒
     *
     * @param N 纳秒
     */
    public static void SET_NANOSECONDS(int N) {
        log.debug("休息  " + N + " 纳秒开始。");
        try {
            TimeUnit.NANOSECONDS.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 纳秒出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 纳秒结束。");
    }

    /**
     * 休息N分钟
     *
     * @param N 分钟
     */
    public static void SET_MINUTES(int N) {
        log.debug("休息  " + N + " 分钟开始。");
        try {
            TimeUnit.MINUTES.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 分钟出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 分钟结束。");
    }

    /**
     * 休息N秒
     *
     * @param N 秒
     */
    public static void SET_SECONDS(int N) {
        log.debug("休息  " + N + " 秒开始。");
        try {
            TimeUnit.SECONDS.sleep(N);
        } catch (InterruptedException e) {
            log.error("休息  " + N + " 秒出错：", e);
            e.printStackTrace();
        }
        log.debug("休息  " + N + " 秒结束。");

    }
}
