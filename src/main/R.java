package main;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.util.Log;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class R {

    public static final String TAG = "main.R";
    public static final String APP_NAME = "Times Table";

    public static final Path DIR_MAIN = Path.of("").toAbsolutePath();

    public static final Path DIR_RES = DIR_MAIN.resolve("res");
    public static final Path DIR_IMAGE = DIR_RES.resolve("image");
    public static final Path APP_ICON = DIR_IMAGE.resolve("icon.png");


    @Nullable
    public static Image createIcon(@NotNull Path path) {
        if (Files.exists(path)) {
            try {
                return Toolkit.getDefaultToolkit().createImage(path.toString());
            } catch (Throwable t) {
                Log.e(TAG, "failed to create image from file <" + path + ">", t);
            }
        }

        return null;
    }

    @Nullable
    public static Image createAppIcon() {
        return createIcon(APP_ICON);
    }

    /* ......................  Strings ............................. */

    @NotNull
    public static String getDarkModeText() {
        return "Dark Theme";
    }

    @NotNull
    public static String getDarkModeTooltipText() {
        return "Switch Dark mode";
    }


    @NotNull
    public static String getInvertYText() {
        return "Invert-Y";
    }

    @NotNull
    public static String getInvertYTooltipText() {
        return "Invert Y-axis";
    }

    @NotNull
    public static String getInvertXText() {
        return "Invert-X";
    }

    @NotNull
    public static String getInvertXTooltipText() {
        return "Invert X-axis";
    }


    @NotNull
    public static String getStickOnIntText() {
        return "Sticking";
    }

    @NotNull
    public static String getStickOnIntTooltipText() {
        return "Pause for a while at each pattern";
    }


    @NotNull
    public static String getEndBehaviourLabelText() {
        return "On End";
    }

    @NotNull
    public static String getEndBehaviourTooltipText() {
        return "Configure end behaviour";
    }

    @NotNull
    public static String getPatternColorModeLabelText() {
        return "Colors ";
    }

    @NotNull
    public static String getPatternColorModeTooltipText() {
        return "configure how different patterns are coloured";
    }


    @NotNull
    public static String getToggleControlsText(boolean controlsShown) {
        return controlsShown? "Hide Dock": "Show Dock";
    }

    @NotNull
    public static String getToggleControlsTooltipText(boolean controlsShown) {
        return (controlsShown? "Hide controls dock": "Show controls dock") + " [Ctrl-C]";
    }

    @NotNull
    public static String getPlayPauseText(boolean playing) {
        return playing? "Pause": "Play";
    }

    @NotNull
    public static String getPlayPauseTooltipText(boolean playing) {
        return (playing? "pause rotors": "start rotors") + " [SPACE]";
    }

    @NotNull
    public static String getResetText() {
        return "Reset";
    }

    @NotNull
    public static String getResetTooltipText() {
        return "Reset [Ctrl-main.R]";
    }

    @NotNull
    public static String getResetScaleAndDragText() {
        return "Reset View";
    }

    @NotNull
    public static String getResetScaleAndDragTooltipText() {
        return "Reset viewport [Shift-main.R]";
    }

    @NotNull
    public static String getTimesFactorText(float timesFactor) {
        return String.format("Times Factor: %.2f", timesFactor);
    }

    @NotNull
    public static String getTimesFactorTooltipText() {
        return "Change Times Table Factor";
    }

    @NotNull
    public static String getPointsCountText(int points) {
        return "Points: " + points;
    }

    @NotNull
    public static String getPointsCountTooltipText() {
        return "Change Times Table points";
    }


    @NotNull
    public static String getSpeedPercentText(int percent) {
        return "Speed: " + percent + "%";
    }

    @NotNull
    public static String getSpeedSliderTooltipText() {
        return "change speed";
    }

    @NotNull
    public static String getScaleText(double scale) {
        return "Scale: " + ((int) Math.round(scale * 100)) + "%";
    }

    @NotNull
    public static String getScaleIncTooltipText() {
        return "Zoom In [Shift-UP]";
    }

    @NotNull
    public static String getScaleDecTooltipText() {
        return "Zoom Out [Shift-DOWN]";
    }


    @Nullable
    public static String getStatusText(double timesFactor) {
        return String.format("Times: %.2f", timesFactor);
    }


}
