package main;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme;
import main.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class R {

    public static final String TAG = "R";
    public static final String APP_NAME = "Times Table";



    @NotNull
    public static final java.util.List<UIManager.LookAndFeelInfo> LOOK_AND_FEELS_INTERNAL;
    @NotNull
    public static final java.util.List<UIManager.LookAndFeelInfo> LOOK_AND_FEELS_FLAT_LAF;
    @NotNull
    public static final List<UIManager.LookAndFeelInfo> LOOK_AND_FEELS_FLAT_LAF_MATERIAL;

    static {
        // internal LAF's
        final UIManager.LookAndFeelInfo[] infos = UIManager.getInstalledLookAndFeels();
        LOOK_AND_FEELS_INTERNAL = Arrays.asList(infos);

        // Main LAF's
        LOOK_AND_FEELS_FLAT_LAF = new ArrayList<>();
        LOOK_AND_FEELS_FLAT_LAF.add(new UIManager.LookAndFeelInfo(FlatLightLaf.NAME, FlatLightLaf.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF.add(new UIManager.LookAndFeelInfo(FlatIntelliJLaf.NAME, FlatIntelliJLaf.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF.add(new UIManager.LookAndFeelInfo(FlatDarkLaf.NAME, FlatDarkLaf.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF.add(new UIManager.LookAndFeelInfo(FlatDarculaLaf.NAME, FlatDarculaLaf.class.getName()));

        // Material LAF's
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL = new ArrayList<>();
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL.add(new UIManager.LookAndFeelInfo(FlatMaterialLighterIJTheme.NAME, FlatMaterialLighterIJTheme.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL.add(new UIManager.LookAndFeelInfo(FlatMaterialDarkerIJTheme.NAME, FlatMaterialDarkerIJTheme.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL.add(new UIManager.LookAndFeelInfo(FlatOneDarkIJTheme.NAME, FlatOneDarkIJTheme.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL.add(new UIManager.LookAndFeelInfo(FlatArcOrangeIJTheme.NAME, FlatArcOrangeIJTheme.class.getName()));
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL.add(new UIManager.LookAndFeelInfo(FlatDarkPurpleIJTheme.NAME, FlatDarkPurpleIJTheme.class.getName()));

        // Install
        LOOK_AND_FEELS_FLAT_LAF.forEach(UIManager::installLookAndFeel);
        LOOK_AND_FEELS_FLAT_LAF_MATERIAL.forEach(UIManager::installLookAndFeel);
    }

    public static final Class<?> LAF_DARK = FlatDarculaLaf.class;
    public static final Class<?> LAF_LIGHT = FlatArcOrangeIJTheme.class;

    public static void init() {

    }

    public static boolean setLookAndFeel(@NotNull String lafClassName) {
        try {
            UIManager.setLookAndFeel(lafClassName);
            return true;
        } catch (Exception exc) {
            Log.e(TAG, "Failed to set Theme: " + lafClassName, exc);
        }

        return false;
    }

    public static boolean setLafDark(boolean darkLaf) {
        return setLookAndFeel((darkLaf? LAF_DARK: LAF_LIGHT).getName());
    }



    /* Dir Structure */

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
    public static String getDarkModeShortDes() {
        return "Switch Dark mode [Shift-D]";
    }


    @NotNull
    public static String getInvertYText() {
        return "Invert-Y";
    }

    @NotNull
    public static String getInvertYShortDes() {
        return "Invert Y-axis [Shift-Y]";
    }

    @NotNull
    public static String getInvertXText() {
        return "Invert-X";
    }

    @NotNull
    public static String getInvertXShortDes() {
        return "Invert X-axis [Shift-X]";
    }


    @NotNull
    public static String getDrawCircleText() {
        return "Circle";
    }

    @NotNull
    public static String getDrawCircleShortDes() {
        return "Draw circle";
    }

    @NotNull
    public static String getDrawPointsText() {
        return "Points";
    }

    @NotNull
    public static String getDrawPointsShortDes() {
        return "Draw points around the circle";
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
    public static String getEndBehaviourShortDes() {
        return "Configure end behaviour";
    }

    @NotNull
    public static String getPatternColorModeLabelText() {
        return "Colors ";
    }

    @NotNull
    public static String getPatternColorModeShortDes() {
        return "configure how different patterns are coloured";
    }


    @NotNull
    public static String getPlayPauseText(boolean playing) {
        return playing? "Pause": "Play";
    }

    @NotNull
    public static String getPlayPauseShortDes(boolean playing) {
        return (playing? "pause times table": "start times table") + " [SPACE]";
    }

    @NotNull
    public static String getResetMainText() {
        return "Reset";
    }

    @NotNull
    public static String getResetMainShortDes() {
        return "Reset [R]";
    }

    @NotNull
    public static String getResetScaleAndDragText() {
        return "Reset View";
    }

    @NotNull
    public static String getResetScaleAndDragShortDes() {
        return "Reset viewport [Shift-R]";
    }

    @NotNull
    public static String getResetAllText() {
        return "Reset All";
    }

    @NotNull
    public static String getResetAllShortDes() {
        return "Reset everything [Ctrl-R]";
    }

    @NotNull
    public static String getTimesFactorText(float timesFactor) {
        return String.format("Times Factor: %.2f", timesFactor);
    }

    @NotNull
    public static String getTimesFactorShortDes() {
        return "Change Times Table Factor";
    }

    @NotNull
    public static String getPointsCountText(int points) {
        return "Points: " + points;
    }

    @NotNull
    public static String getPointsCountShortDes() {
        return "Change Times Table points";
    }


    @NotNull
    public static String getSpeedPercentText(int percent) {
        return "Speed: " + percent + "%";
    }

    @NotNull
    public static String getSpeedSliderShortDes() {
        return "change speed";
    }



    @Nullable
    public static String getStatusText(double timesFactor) {
        return String.format("Times: %.2f", timesFactor);
    }

    @NotNull
    public static String getToggleControlsText(boolean controlsShown) {
        return (controlsShown? "Hide": "Show") + " Dock";
    }

    @NotNull
    public static String getToggleControlsShortDescription(boolean controlsShown) {
        return (controlsShown? "Hide": "Show") + " Controls Dock [Shift-C]";
    }

    @NotNull
    public static String getToggleMenuBarText(boolean menuVisible) {
        return (menuVisible? "Hide": "Show") + " Menu";
    }

    @NotNull
    public static String getToggleMenuBarShortDescription(boolean menuVisible) {
        return (menuVisible? "Hide": "Show") + " Menu [Shift-M]";
    }

    @NotNull
    public static String getTogglePresentationModeText(boolean presenting) {
        return (presenting? "Exit": "Enter") + " Presentation Mode";
    }


    public static String getTogglePresentationModeShortDescription(boolean presenting) {
        return (presenting? "Leave": "Enter") + " Presentation [Shift-P]";
    }


    @NotNull
    public static String getScaleText(boolean inc) {
        return inc? "+": "-";
    }

    @NotNull
    public static String getScaleShortDescription(boolean inc) {
        return inc? "Zoom In [Shift-UP]": "Zoom Out [Shift-DOWN]";
    }

    @NotNull
    public static String getScaleText(double scale) {
        return "Scale: " + ((int) Math.round(scale * 100)) + "%";
    }


    @NotNull
    public static String getFullscreenText() {
        return "Fullscreen";
    }

    @NotNull
    public static String getFullscreenText(boolean isFullscreen) {
        return isFullscreen? "Exit Fullscreen": "Fullscreen";
    }

    @NotNull
    public static String getFullscreenShortDescription(boolean isFullscreen) {
        return (isFullscreen? "Exit": "Enter") + " Fullscreen [Ctrl-F]";
    }

}
