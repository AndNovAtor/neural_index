package com.andnovator.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ivan Novikov mailto: <a href="mailto:novikov@pragmatix-corp.com">novikov@pragmatix-corp.com</a>
 *         Created: 07.06.2016 14:37
 *         <p>
 * An utility for measuring time between instants
 * (e.g. for rough assessments of performance)
 * <b>NB: do not use this for precise measurements of performance</b>
 */
public class Stopwatch {

    private long start = now();

    private static ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss.SSS");
        }
    };

    public void start() {
        start = now();
    }

    /**
     * Starts new lap and returns time between previous lap() or start() and now
     * @return time of last lap in millis
     */
    public long newLap() {
        long now = now();
        long lapTime = now - start;
        start = now;
        return lapTime;
    }

    /**
     * @return time of last lap in millis (without starting a new lap)
     */
    public long getLapTime() {
        return now() - start;
    }

    /**
     * @return Current time in millis
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * @return Current time in format "14:47:00.103"
     */
    public static String formatNow() {
        return format.get().format(new Date());
    }
}
