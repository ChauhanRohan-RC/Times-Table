package main.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public interface Ui {

    Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    int DEFAULT_LOOPER_DELAY_MS = 1;

    @NotNull
    static Rectangle windowBoundsCenterScreen(int width, int height) {
        return new Rectangle((SCREEN_SIZE.width - width) / 2, (SCREEN_SIZE.height - height) / 2, width, height);
    }

    @NotNull
    static Timer createLooper(@NotNull Runnable action, int delayMs) {
        final ActionListener l = e -> action.run();
        final Timer timer = new Timer(delayMs, l);
        timer.setRepeats(true);
        return timer;
    }

    @NotNull
    static Timer createLooper(@NotNull Runnable action) {
        return createLooper(action, DEFAULT_LOOPER_DELAY_MS);
    }


    static boolean isMainThread() {
        return EventQueue.isDispatchThread();
    }

    static void uiPost(@NotNull Runnable run) {
        EventQueue.invokeLater(run);
    }

    static void considerPost(@NotNull Runnable run) {
        if (isMainThread()) {
            run.run();
        } else {
            uiPost(run);
        }
    }



//    @NotNull
//    static Point2D add(@NotNull Point2D one, @NotNull Point2D two) {
//        return new Point2D.Double(one.getX() + two.getX(), one.getY() + two.getY());
//    }
//
//    @NotNull
//    static Point2D subtract(@NotNull Point2D one, @NotNull Point2D two) {
//        return new Point2D.Double(one.getX() - two.getX(), one.getY() - two.getY());
//    }
//
//    @NotNull
//    static Point2D multiply(@NotNull Point2D p, double by) {
//        return new Point2D.Double(p.getX() * by, p.getY() * by);
//    }
//
//    @NotNull
//    static Point2D negate(@NotNull Point2D p) {
//        return multiply(p, -1);
//    }


}
