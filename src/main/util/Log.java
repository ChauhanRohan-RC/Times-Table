package main.util;

public class Log {

    public static final String TAG = "LOG";
    public static final boolean DEBUG = true;

    private static String createMsg(String tag, Object msg) {
        if (tag == null) {
            tag = TAG;
        }

        return tag + ": " + msg;
    }

    public static void v(String tag, Object msg) {
        System.out.println(createMsg(tag, msg));
    }

    public static void v(Object msg) {
        v(null, msg);
    }

    public static void d(String tag, Object msg, Throwable t) {
        if (DEBUG) {
            System.out.println(createMsg(tag, msg));
            if (t != null) {
                t.printStackTrace(System.err);
            }
        }
    }

    public static void d(String tag, Object msg) {
        d(tag, msg, null);
    }

    public static void d(Object msg) {
        d(null, msg);
    }


    public static void e(String tag, Object msg, Throwable t) {
        System.err.println(createMsg(tag, msg));
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }

    public static void e(String tag, Object msg) {
        e(tag, msg, null);
    }

    public static void e(Object msg) {
        e(null, msg);
    }


}
