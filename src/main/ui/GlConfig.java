package main.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import main.R;
import main.util.Log;
import org.jetbrains.annotations.NotNull;
import main.math.RMath;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntFunction;

public class GlConfig {


    public static final boolean FORCE_ANTIALIASING = true;
    public static final boolean DEFAULT_FULLSCREEN = false;
    public static final boolean DEFAULT_CONTROLS_VISIBLE = true;
    public static final boolean DEFAULT_MENUBAR_VISIBLE = true;

    public static final boolean DEFAULT_DRAW_CIRCLE = false;
    public static final boolean DEFAULT_DRAW_POINTS = false;

    // .................. Transforms  ................
    public static final boolean DEFAULT_INVERT_X = false;
    public static final boolean DEFAULT_INVERT_Y = false;

    public static final double DEFAULT_SALE_MIN = 0.1;
    public static final double DEFAULT_SCALE_MAX = 50;
    public static final double SCALE_WHEEL_ROTATION_MULTIPLIER = 0.2;
    public static final double DEFAULT_SCALE_UNIT_INCREMENT = 0.5;
    public static final double DEFAULT_SCALE_UNIT_DECREMENT_BELOW_1 = 0.1;
    public static final double DRAG_X_UNITS = 50;
    public static final double DRAG_Y_UNITS = 50;

    public static void init() {
        setDarkModeInternal(DEFAULT_DARK_MODE);
    }

    /*.............................. THEME .................................*/

    public enum PatternColorMode {
        /**
         * No colouring
         * */
        NONE("None"),

        /**
         * Each line is differently coloured
         * */
        LINE("Line"),

        /**
         * Each pattern (ie. each timesFactor) is differently coloured
         * */
        PATTERN("Pattern");


        @NotNull
        public final String displayName;



        PatternColorMode(@NotNull String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }


        @Nullable
        @Unmodifiable
        private static PatternColorMode[] sValues;

        public static PatternColorMode[] sharedValues() {
            if (sValues == null) {
                sValues = values();
            }

            return sValues;
        }
    }

    public static final int PATTERNS_IN_HUE_CYCLE = 10;

    private static float mapTimesFactorToHue(float timesFactor) {
        return RMath.map( Math.max(timesFactor - TimesTablePanel.TIMES_FACTOR_MIN, 0) % PATTERNS_IN_HUE_CYCLE, 0, PATTERNS_IN_HUE_CYCLE, 0, 1);
    }


    public static final boolean DEFAULT_DARK_MODE = true;

    private static boolean sDarkMode = DEFAULT_DARK_MODE;
    @NotNull
    private static PatternColorMode sPatternColorMode = PatternColorMode.PATTERN;

    public static boolean isDarkMode() {
        return sDarkMode;
    }

    /**
     * @return whether to update the ui tree
     * */
    public static boolean setDarkModeInternal(boolean darkMode) {
        sDarkMode = darkMode;
        return R.setLafDark(darkMode);
    }

    /**
     * @return whether to update the ui tree
     * */
    public static boolean setDarkMode(boolean darkMode) {
        if (sDarkMode == darkMode)
            return false;

        return setDarkModeInternal(darkMode);
    }

    @NotNull
    public static PatternColorMode getPatternColorMode() {
        return sPatternColorMode;
    }

    public static void setPatternColorMode(@NotNull PatternColorMode patternColorMode) {
        sPatternColorMode = patternColorMode;
    }

    /* DARK MODE */
    private static final Color DARK__BG = new Color(0, 0, 0);
    private static final Color DARK__FG_DARK = new Color(255, 255, 255);
    private static final Color DARK__FG_MEDIUM = new Color(225, 225, 225);;
    private static final Color DARK__FG_LIGHT = new Color(195, 195, 195);

    /* LIGHT MODE */
    private static final Color LIGHT__BG = new Color(255, 255, 255);
    private static final Color LIGHT__FG_DARK = new Color(0, 0, 0);
    private static final Color LIGHT__FG_MEDIUM = new Color(25, 25, 25);
    private static final Color LIGHT__FG_LIGHT = new Color(45, 45, 45);


    @NotNull
    public static Color bg() {
        return isDarkMode()? DARK__BG: LIGHT__BG;
    }

    @NotNull
    public static Color fgDark() {
        return isDarkMode()? DARK__FG_DARK : LIGHT__FG_DARK;
    }

    @NotNull
    public static Color fgMedium() {
        return isDarkMode()? DARK__FG_MEDIUM : LIGHT__FG_MEDIUM;
    }

    @NotNull
    public static Color fgLight() {
        return isDarkMode()? DARK__FG_LIGHT : LIGHT__FG_LIGHT;
    }

    @NotNull
    public static Color circleColor(float timesFactor) {
        return sPatternColorMode == PatternColorMode.PATTERN? patternColorExplicit(timesFactor): fgLight();

//        return fgLight();
    }

    @NotNull
    public static Color pointColor(float timesFactor) {
//        return sPatternColorMode == PatternColorMode.PATTERN? patternColorExplicit(timesFactor): fgMedium();

        return fgMedium();
    }

    @NotNull
    public static Color patternModeNoneColorExplicit() {
        return fgDark();
    }



    @NotNull
    public static Color patternColorExplicit(float timesFactor) {
        return Color.getHSBColor(mapTimesFactorToHue(timesFactor), 1, 1);
    }

    @NotNull
    public static Color lineColorExplicit(float index, float count) {
        return Color.getHSBColor(RMath.map(index, 0, count, 0, 1), 1, 1);
    }

    @NotNull
    public static Color lineColor(float index, int count, float timesFactor) {
        return switch (sPatternColorMode) {
            case NONE -> patternModeNoneColorExplicit();
            case LINE -> lineColorExplicit(index, count);
            case PATTERN -> patternColorExplicit(timesFactor);
        };
    }

    @NotNull
    public static IntFunction<Color> patternColorFunction(int count, float timesFactor) {
        switch (sPatternColorMode) {
            case LINE -> {
                return i -> lineColorExplicit(i, count);
            }

            case PATTERN -> {
                final Color c = patternColorExplicit(timesFactor);
                return i -> c;
            }

            default -> {
                final Color c = patternModeNoneColorExplicit();
                return i -> c;
            }
        }
    }

}
