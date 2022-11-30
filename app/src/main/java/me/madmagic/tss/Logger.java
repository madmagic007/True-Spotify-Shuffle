package me.madmagic.tss;

import android.util.Log;

public class Logger {

    public static String redirect = "http://localhost:4613/callback";
    public static final String secret = "44baae296608412a941966c38160acf3";
    public static final String id = "f72a41e854c74533ae67ad81c0d2db7b";
    public static final int port = 4613;

    private static final String tag = "TSS";
    public static void doLog(String string) {
        Log.d(tag, string);
    }

    public static void doLog(Exception e) {
        Log.e(tag, e.getMessage(), e);
    }

    public static void doLog(Throwable e) {
        Log.d(tag, Log.getStackTraceString(e));
    }
}
