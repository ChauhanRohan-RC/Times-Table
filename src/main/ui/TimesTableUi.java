package main.ui;

import main.R;
import main.util.Format;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.util.Size;
import main.util.Ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class TimesTableUi extends JFrame implements TimesTablePanel.Listener {

    private static final Dimension MINIMUM_SIZE = new Dimension(400, 400);

    public static final int INITIAL_WIDTH = Ui.SCREEN_SIZE.width - 200;
    public static final int INITIAL_HEIGHT = Ui.SCREEN_SIZE.height - 200;


    @NotNull
    private final TimesTablePanel panel;
    private boolean mFullscreen = GlConfig.DEFAULT_FULLSCREEN;

    final JPanel controlPanel;
    final JScrollPane controlScrollPane;

    //    final JPanel timesFactorPanel;
    final JLabel timesfactorText;
    final JSlider timesFactorSlider;
    final JLabel pointsCountText;
    final JSlider pointsCountSlider;
    final JLabel speedText;
    final JSlider speedSlider;


    final JToggleButton playButton;
    final JButton resetMainButton;
    final JButton resetScaleAndDragButton;
    final JCheckBox invertXCheck;
    final JCheckBox invertYCheck;
    final JCheckBox drawCircleCheck;
    final JCheckBox drawPointsCheck;
    final JCheckBox darkModeCheck;
    final JCheckBox stickOnIntCheck;

    final JLabel endBehaviourLabel;
    final JComboBox<TimesTablePanel.EndBehaviour> endBehaviourComboBox;

    final JLabel patternColorModeLabel;
    final JComboBox<GlConfig.PatternColorMode> patternColorModeComboBox;

    final JLabel scaleText;
    final JButton scaleIncButton;
    final JButton scaleDecButton;

    final JButton leftButton;
    final JButton rightButton;
    final JButton upButton;
    final JButton downButton;

    final JButton toggleControlsButton;

    private boolean mIgnoreTimesSliderEvent;

    /* Menu */
    private final JMenuBar menuBar;
    private final JMenu viewMenu;

    public TimesTableUi() {
        this(null);
    }

    private static int timesFactorToInt(float timesFactor) {
        return Math.round(timesFactor);
    }

    private static int speedPercentToInt(float speedPercent) {
        return Math.round(speedPercent);
    }

    public TimesTableUi(@Nullable String title) {
        super(title == null || title.isEmpty() ? R.APP_NAME : title);

        panel = new TimesTablePanel();

        // Points Count
        final int pointsCount = panel.getPointsCount();
        pointsCountText = new JLabel(R.getPointsCountText(pointsCount));

        pointsCountSlider = new JSlider(SwingConstants.HORIZONTAL, TimesTablePanel.POINTS_COUNT_MIN, TimesTablePanel.POINTS_COUNT_MAX, pointsCount);
        pointsCountSlider.setToolTipText(R.getPointsCountShortDes());
        pointsCountSlider.setLabelTable(pointsCountSlider.createStandardLabels(TimesTablePanel.POINTS_COUNT_MAX - TimesTablePanel.POINTS_COUNT_MIN, TimesTablePanel.POINTS_COUNT_MIN));
        pointsCountSlider.setPaintLabels(true);

        // Times factor
        final float timesFactor = panel.getTimesFactor();
        timesfactorText = new JLabel(R.getTimesFactorText(timesFactor));

        final int min = timesFactorToInt(TimesTablePanel.TIMES_FACTOR_MIN), max = timesFactorToInt(TimesTablePanel.TIMES_FACTOR_MAX), intTimesF = timesFactorToInt(timesFactor);
        timesFactorSlider = new JSlider(SwingConstants.HORIZONTAL, min, max, intTimesF);
        timesFactorSlider.setToolTipText(R.getTimesFactorShortDes());
        timesFactorSlider.setLabelTable(timesFactorSlider.createStandardLabels(max - min, min));
        timesFactorSlider.setPaintLabels(true);

        // Speed
        final int speed = speedPercentToInt(panel.getTimesFactorSpeedPercent());
        speedText = new JLabel(R.getSpeedPercentText(speed));

        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, speed);
        speedSlider.setToolTipText(R.getSpeedSliderShortDes());
        speedSlider.setLabelTable(speedSlider.createStandardLabels(25, 0));
        speedSlider.setPaintLabels(true);


        // Play/Pause Toggle
        final boolean playing = panel.isPlaying();
        final BaseAction togglePlayAction = uia(ActionInfo.TOGGLE_PLAY_PAUSE)
                .setName(R.getPlayPauseText(playing))
                .setShortDescription(R.getPlayPauseShortDes(playing))
                .setSelected(!playing);

        playButton = new JToggleButton(togglePlayAction);

        // Reset
        resetMainButton = new JButton(uia(ActionInfo.RESET_MAIN));

        resetScaleAndDragButton = new JButton(uia(ActionInfo.RESET_SCALE_DRAG));
        syncResetScaleAndDragButton();


        // Ops
        drawCircleCheck = new JCheckBox(uia(ActionInfo.TOGGLE_DRAW_CIRCLE).setSelected(panel.isDrawCircleEnabled()));
        drawPointsCheck = new JCheckBox(uia(ActionInfo.TOGGLE_DRAW_POINTS).setSelected(panel.isDrawPointsEnabled()));

        invertXCheck = new JCheckBox(uia(ActionInfo.INVERT_X).setSelected(panel.isXInverted()));
        invertYCheck = new JCheckBox(uia(ActionInfo.INVERT_Y).setSelected(panel.isYInverted()));

        darkModeCheck = new JCheckBox(uia(ActionInfo.TOGGLE_DARK_MODE).setSelected(GlConfig.isDarkMode()));
        stickOnIntCheck = new JCheckBox(uia(ActionInfo.TOGGLE_STICK_ON_INT).setSelected(panel.isTimesFactorStickOnIntEnabled()));

        endBehaviourLabel = new JLabel(R.getEndBehaviourLabelText());
        endBehaviourLabel.setToolTipText(R.getEndBehaviourShortDes());
        endBehaviourComboBox = new JComboBox<>(TimesTablePanel.EndBehaviour.sharedValues());
        endBehaviourComboBox.setSelectedIndex(panel.getEndBehaviour().ordinal());
        endBehaviourComboBox.setToolTipText(R.getEndBehaviourShortDes());

        patternColorModeLabel = new JLabel(R.getPatternColorModeLabelText());
        patternColorModeLabel.setToolTipText(R.getPatternColorModeShortDes());
        patternColorModeComboBox = new JComboBox<>(GlConfig.PatternColorMode.sharedValues());
        patternColorModeComboBox.setSelectedIndex(GlConfig.getPatternColorMode().ordinal());
        patternColorModeComboBox.setToolTipText(R.getPatternColorModeShortDes());

        // Transforms
        scaleText = new JLabel(R.getScaleText(panel.getScale()));
        scaleIncButton = new JButton(uia(ActionInfo.SCALE_UP));
        scaleDecButton = new JButton(uia(ActionInfo.SCALE_DOWN));

        leftButton = new JButton(uia(ActionInfo.DRAG_LEFT));
        rightButton = new JButton(uia(ActionInfo.DRAG_RIGHT));
        upButton = new JButton(uia(ActionInfo.DRAG_UP));
        downButton = new JButton(uia(ActionInfo.DRAG_DOWN));

        // Toggle Controls
        final Action controlUia = uia(ActionInfo.TOGGLE_CONTROLS)
                .setName(R.getToggleControlsText(GlConfig.DEFAULT_CONTROLS_VISIBLE))
                .setShortDescription(R.getToggleControlsShortDescription(GlConfig.DEFAULT_CONTROLS_VISIBLE))
                .setSelected(GlConfig.DEFAULT_CONTROLS_VISIBLE);

        toggleControlsButton = new JButton(controlUia);

        // Layout
        controlPanel = new JPanel();
        controlPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 10, 5));
        controlScrollPane = new JScrollPane(controlPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);


        final JPanel endComboPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 2));
        endComboPanel.add(endBehaviourLabel);
        endComboPanel.add(endBehaviourComboBox);

        final JPanel patternColorModeComoPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 8, 2));
        patternColorModeComoPanel.add(patternColorModeLabel);
        patternColorModeComoPanel.add(patternColorModeComboBox);

        final JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        buttonsPanel.add(playButton);
        buttonsPanel.add(resetMainButton);

        final JPanel checksPanel = new JPanel(new GridLayout(2, 3, 2, 2));
        checksPanel.add(stickOnIntCheck);
        checksPanel.add(invertXCheck);
        checksPanel.add(drawCircleCheck);
        checksPanel.add(darkModeCheck);
        checksPanel.add(invertYCheck);
        checksPanel.add(drawPointsCheck);

        final JPanel comboPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        comboPanel.add(endComboPanel);
        comboPanel.add(patternColorModeComoPanel);

//        checksPanel.add(pointsJoinCheck);

//        final JPanel actionsPanel = new JPanel(new GridLayout(2, 1, 2, 2));
//        actionsPanel.add(buttonsPanel);
//        actionsPanel.add(checksPanel);
//        actionsPanel.add(comboPanel);
        controlPanel.add(buttonsPanel);
        controlPanel.add(checksPanel);
        controlPanel.add(comboPanel);


        final JPanel slidersPanel = new JPanel(new GridLayout(2, 3, 5, 2));
        pointsCountText.setHorizontalAlignment(SwingConstants.CENTER);
        timesfactorText.setHorizontalAlignment(SwingConstants.CENTER);
        speedText.setHorizontalAlignment(SwingConstants.CENTER);

        slidersPanel.add(pointsCountText);
        slidersPanel.add(timesfactorText);
        slidersPanel.add(speedText);
        slidersPanel.add(pointsCountSlider);
        slidersPanel.add(timesFactorSlider);
        slidersPanel.add(speedSlider);
        controlPanel.add(slidersPanel);

        final JPanel scalePanel = new JPanel(new GridLayout(2, 1, 5, 2));
        final JPanel scaleButtonsPanel = new JPanel(new GridLayout(1, 2, 3, 2));
        scaleButtonsPanel.add(scaleIncButton);
        scaleButtonsPanel.add(scaleDecButton);

        scaleText.setHorizontalAlignment(SwingConstants.CENTER);
        scalePanel.add(scaleText);
        scalePanel.add(scaleButtonsPanel);
        controlPanel.add(scalePanel);

        final JPanel navigationPanel = new JPanel(new GridLayout(2, 1, 3, 4));
        final JPanel navigationPanelInternal = new JPanel(new GridLayout(1, 3, 3, 2));
        upButton.setHorizontalAlignment(SwingConstants.CENTER);
        navigationPanelInternal.add(leftButton);
        navigationPanelInternal.add(downButton);
        navigationPanelInternal.add(rightButton);
        navigationPanel.add(upButton);
        navigationPanel.add(navigationPanelInternal);
        controlPanel.add(navigationPanel);

        final JPanel otherButtonsPanel = new JPanel(new GridLayout(2, 1, 3, 2));
        otherButtonsPanel.add(resetScaleAndDragButton);
        otherButtonsPanel.add(toggleControlsButton);

        controlPanel.add(otherButtonsPanel);

        setBounds(Ui.windowBoundsCenterScreen(INITIAL_WIDTH, INITIAL_HEIGHT));
        setMinimumSize(MINIMUM_SIZE);
        setLayout(new BorderLayout(0, 0));
        add(controlScrollPane, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);


        // Listeners
        panel.addListener(this);

        pointsCountSlider.addChangeListener(ev -> setPointsCount(pointsCountSlider.getValue()));
        timesFactorSlider.addChangeListener(ev -> {
            if (mIgnoreTimesSliderEvent) {
                mIgnoreTimesSliderEvent = false;
            } else {
                setTimesFactor(timesFactorSlider.getValue(), false);
            }
        });

        speedSlider.addChangeListener(ev -> setSpeedPercent(speedSlider.getValue()));
//        playButton.addItemListener(e -> setPlay(!playButton.isSelected()));
//        resetMainButton.addActionListener(e -> reset(false));
//        resetScaleAndDragButton.addActionListener(e -> resetScaleAndDrag());

//        stickOnIntCheck.addItemListener(e -> setStickOnIntEnabled(stickOnIntCheck.isSelected()));
//        darkModeCheck.addItemListener(e -> setDarkMode(darkModeCheck.isSelected()));
//        invertXCheck.addItemListener(e -> setInvertX(invertXCheck.isSelected()));
//        invertYCheck.addItemListener(e -> setInvertY(invertYCheck.isSelected()));
//        drawCircleCheck.addItemListener(e -> setDrawCircle(drawCircleCheck.isSelected()));
//        drawPointsCheck.addItemListener(e -> setDrawPoints(drawPointsCheck.isSelected()));

        patternColorModeComboBox.addActionListener(e -> setPatternColorMode(GlConfig.PatternColorMode.sharedValues()[patternColorModeComboBox.getSelectedIndex()]));
        endBehaviourComboBox.addActionListener(e -> setEndBehaviour(TimesTablePanel.EndBehaviour.sharedValues()[endBehaviourComboBox.getSelectedIndex()]));
//        scaleIncButton.addActionListener(e -> incrementScaleByUnit());
//        scaleDecButton.addActionListener(e -> decrementScaleByUnit());
//
//        leftButton.addActionListener(e -> panel.dragXByUnit(false));
//        rightButton.addActionListener(e -> panel.dragXByUnit(true));
//        upButton.addActionListener(e -> panel.dragYByUnit(false));
//        downButton.addActionListener(e -> panel.dragYByUnit(true));
//
//        toggleControlsButton.addActionListener(e -> toggleControlsVisibility());

        // Menu
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        viewMenu = new JMenu("View");
        menuBar.add(viewMenu);
        viewMenu.add(new JCheckBoxMenuItem(uia(ActionInfo.TOGGLE_DARK_MODE)));
        viewMenu.addSeparator();
        viewMenu.add(uia(ActionInfo.TOGGLE_MENUBAR));
        viewMenu.add(uia(ActionInfo.TOGGLE_CONTROLS));
        viewMenu.addSeparator();
        viewMenu.add(uia(ActionInfo.TOGGLE_FULLSCREEN));
        viewMenu.add(uia(ActionInfo.TOGGLE_PRESENTATION_MODE));

        // Run
        setupActionKeyBindings(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        addMouseListener(new MouseHandler());
//        panel.setPlay(true);
//        panel.setTimesFactor(51);

        final Image appIcon = R.createAppIcon();
        if (appIcon != null) {
            setIconImage(appIcon);
        }

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setFocusable(true);
        setVisible(true);
        setResizable(true);

        Ui.uiPost(() -> {
            // sync
            setControlsVisibleInternal(GlConfig.DEFAULT_CONTROLS_VISIBLE);
            setMenuBarVisibleInternal(GlConfig.DEFAULT_MENUBAR_VISIBLE);
            setFullscreenInternal(GlConfig.DEFAULT_FULLSCREEN);
            requestFocusInWindow();
            update();
        });
    }

    @NotNull
    public final TimesTablePanel getPanel() {
        return panel;
    }

    private void update() {
        revalidate();
        repaint();
    }

    protected void onFullscreenChanged(boolean fullscreen) {
        uia(ActionInfo.TOGGLE_FULLSCREEN)
                .setName(R.getFullscreenText(fullscreen))
                .setShortDescription(R.getFullscreenShortDescription(fullscreen))
                .setSelected(fullscreen);

        syncPresentationMode();
    }

    public final boolean isFullscreen() {
        return mFullscreen;
    }

    private void setFullscreenInternal(boolean fullscreen) {
        getGraphicsConfiguration().getDevice().setFullScreenWindow(fullscreen? TimesTableUi.this: null);
//        setMenuBarVisibleInternal(!fullscreen);
        mFullscreen = fullscreen;
        onFullscreenChanged(fullscreen);
    }

    public final boolean setFullscreen(boolean fullscreen) {
        if (mFullscreen == fullscreen)
            return false;
        setFullscreenInternal(fullscreen);
        return true;
    }

    public final void toggleFullscreen() {
        setFullscreen(!mFullscreen);
    }

    protected void onMenuBarVisibilityChanged(boolean visible) {
        uia(ActionInfo.TOGGLE_MENUBAR)
                .setName(R.getToggleMenuBarText(visible))
                .setShortDescription(R.getToggleMenuBarShortDescription(visible))
                .setSelected(visible);

        syncPresentationMode();
        update();
    }

    public final boolean isMenuBarVisible() {
        final JMenuBar menuBar = getJMenuBar();
        return menuBar != null && menuBar.isVisible();
    }

    protected final void setMenuBarVisibleInternal(boolean visible) {
        final JMenuBar menuBar = getJMenuBar();
        if (menuBar == null)
            return;

        menuBar.setVisible(visible);
        onMenuBarVisibilityChanged(visible);
    }

    public final void setMenuBarVisible(boolean visible) {
        if (visible == isMenuBarVisible())
            return;

        setMenuBarVisibleInternal(visible);
    }

    public final boolean toggleMenuBarVisible() {
        final boolean newState = !isMenuBarVisible();
        setMenuBarVisibleInternal(newState);
        return newState;
    }


    protected void onControlsVisibilityChanged(boolean controlsVisible) {
        uia(ActionInfo.TOGGLE_CONTROLS)
                .setName(R.getToggleControlsText(controlsVisible))
                .setShortDescription(R.getToggleControlsShortDescription(controlsVisible))
                .setSelected(controlsVisible);

        syncPresentationMode();
        update();
    }

    public final boolean areControlsVisible() {
        return controlScrollPane.isVisible();
    }

    private void setControlsVisibleInternal(boolean visible) {
        controlScrollPane.setVisible(visible);
        onControlsVisibilityChanged(visible);
    }

    public final boolean setControlsVisible(boolean visible) {
        if (visible == areControlsVisible())
            return false;

        setControlsVisibleInternal(visible);
        return true;
    }

    public final void toggleControlsVisibility() {
        setControlsVisibleInternal(!areControlsVisible());
    }

    protected void onPresentationModeEnabledChanged(boolean presenting) {
        uia(ActionInfo.TOGGLE_PRESENTATION_MODE)
                .setName(R.getTogglePresentationModeText(presenting))
                .setShortDescription(R.getTogglePresentationModeShortDescription(presenting))
                .setSelected(presenting);
    }

    protected final void syncPresentationMode() {
        onPresentationModeEnabledChanged(isPresenting());
    }

    public final boolean isPresenting() {
        return isFullscreen() && !areControlsVisible();
    }

    protected final void setPresentationModeEnabledInternal(boolean present) {
        setMenuBarVisible(!present);
        setControlsVisible(!present);
        setFullscreen(present);

        onPresentationModeEnabledChanged(present);
    }

    public final void setPresentationModeEnabled(boolean present) {
        if (present == isPresenting())
            return;

        setPresentationModeEnabledInternal(present);
    }

    public final boolean togglePresentationMode() {
        final boolean newState = !isPresenting();
        setPresentationModeEnabledInternal(newState);
        return newState;
    }




    public void setPlay(boolean play) {
        panel.setPlay(play);

        uia(ActionInfo.TOGGLE_PLAY_PAUSE)
                .setName(R.getPlayPauseText(play))
                .setShortDescription(R.getPlayPauseShortDes(play))
                .setSelected(!play);

        updateTimesFactorUi();
    }

    public boolean isPlaying() {
        return panel.isPlaying();
    }

    public boolean togglePlay() {
        return panel.togglePlay();
    }


    public void updateTimesFactorUi(float timesFactor) {
        if (isPlaying()) {
//            timesFactorPanel.setEnabled(false);
            timesfactorText.setEnabled(false);
            timesFactorSlider.setEnabled(false);
        } else {
            timesfactorText.setEnabled(true);
            timesFactorSlider.setEnabled(true);

            final int intVal = timesFactorToInt(timesFactor);
            if (timesFactorSlider.getValue() != intVal) {
                mIgnoreTimesSliderEvent = true;
                timesFactorSlider.setValue(intVal);
                mIgnoreTimesSliderEvent = false;
            }

            timesfactorText.setText(R.getTimesFactorText(timesFactor));
        }
    }

    public void updateTimesFactorUi() {
        updateTimesFactorUi(panel.getTimesFactor());
    }

    public void setTimesFactor(float factor, boolean fromPanel) {
        if (!fromPanel) {
            factor = panel.setTimesFactor(factor);
        }

        updateTimesFactorUi(factor);
    }

    public void setPointsCount(int count) {
        count = panel.setPointsCount(count);

        if (pointsCountSlider.getValue() != count) {
            pointsCountSlider.setValue(count);
        }

        pointsCountText.setText(R.getPointsCountText(count));
    }


    public void setSpeedPercent(float percent) {
        panel.setTimesFactorSpeedPercentage(percent);

        final int val = speedPercentToInt(panel.getTimesFactorSpeedPercent());
        if (speedSlider.getValue() != val) {
            speedSlider.setValue(val);
        }

        speedText.setText(R.getSpeedPercentText(val));
    }


    public void setDarkMode(boolean darkMode) {
        if (GlConfig.setDarkMode(darkMode)) {
            panel.updateTheme();
            SwingUtilities.updateComponentTreeUI(this);
        }

        uia(ActionInfo.TOGGLE_DARK_MODE).setSelected(darkMode);
    }

    public void toggleDarkMode() {
        setDarkMode(!GlConfig.isDarkMode());
    }

    public void setInvertX(boolean invertX) {
        panel.setInvertX(invertX);
        uia(ActionInfo.INVERT_X).setSelected(invertX);
    }

    public void setInvertY(boolean invertY) {
        panel.setInvertY(invertY);
        uia(ActionInfo.INVERT_Y).setSelected(invertY);
    }

    public void setDrawCircle(boolean drawCircle) {
        panel.setDrawCircle(drawCircle);
        uia(ActionInfo.TOGGLE_DRAW_CIRCLE).setSelected(drawCircle);
    }

    public void setDrawPoints(boolean drawPoints) {
        panel.setDrawPoints(drawPoints);
        uia(ActionInfo.TOGGLE_DRAW_POINTS).setSelected(drawPoints);
    }

    public void setStickOnIntEnabled(boolean enabled) {
        panel.setTimesFactorStickOnIntEnabled(enabled);
        uia(ActionInfo.TOGGLE_STICK_ON_INT).setSelected(enabled);
    }

    public void setEndBehaviour(@NotNull TimesTablePanel.EndBehaviour endBehaviour) {
        panel.setEndBehaviour(endBehaviour);

        if (endBehaviourComboBox.getSelectedIndex() != endBehaviour.ordinal()) {
            endBehaviourComboBox.setSelectedIndex(endBehaviour.ordinal());
        }
    }

    public void setPatternColorMode(@NotNull GlConfig.PatternColorMode patternColorMode) {
        panel.setPatternColorMode(patternColorMode);

        final int index = patternColorMode.ordinal();
        if (patternColorModeComboBox.getSelectedIndex() != index) {
            endBehaviourComboBox.setSelectedIndex(index);
        }
    }

    public void setScale(double scale, boolean fromPanel) {
        if (!fromPanel) {
            panel.setScale(scale);
        }

        scaleText.setText(R.getScaleText(scale));
        scale = panel.getScale();

        uia(ActionInfo.SCALE_UP).setEnabled(scale < panel.getMaximumScale());
        uia(ActionInfo.SCALE_DOWN).setEnabled(scale > panel.getMinimumScale());

        syncResetScaleAndDragButton();
    }

    public void setScale(double scale) {
        setScale(scale, false);
    }

    public boolean incrementScaleByUnit() {
        return panel.incrementScaleByUnit();
    }

    public boolean decrementScaleByUnit() {
        return panel.decrementScaleByUnit();
    }

    public void syncResetScaleAndDragButton() {
        uia(ActionInfo.RESET_SCALE_DRAG).setEnabled(panel.hasScaleOrDrag());
        update();
    }


    public void reset(boolean scaleAndDrag) {
        panel.reset(scaleAndDrag);
    }

    public void resetScaleAndDrag() {
        panel.resetScaleAndDrag();
    }



    /* Panel Listener */

    @Override
    public void onIsPlayingChanged(@NotNull TimesTablePanel panel, boolean playing) {
        setPlay(playing);
    }

    @Override
    public void onTimesFactorSpeedChanged(@NotNull TimesTablePanel panel, float percent) {
        setSpeedPercent(percent);
    }

    @Override
    public void onTimesFactorChanged(@NotNull TimesTablePanel panel, float timesFactor) {
        setTimesFactor(timesFactor, true);
    }

    @Override
    public void onPointsCountChanged(@NotNull TimesTablePanel panel, int count) {
        setPointsCount(count);
    }

    @Override
    public void onTimesFactorStickOnIntChanged(@NotNull TimesTablePanel panel, boolean stickingEnabled) {
        setStickOnIntEnabled(stickingEnabled);
    }

    @Override
    public void onEndBehaviourChanged(@NotNull TimesTablePanel panel, TimesTablePanel.@NotNull EndBehaviour old, TimesTablePanel.@NotNull EndBehaviour _new) {
        setEndBehaviour(_new);
    }

    @Override
    public void onDrawCircleChanged(@NotNull TimesTablePanel panel, boolean drawCircle) {
        setDrawCircle(drawCircle);
    }

    @Override
    public void onDrawPointsChanged(@NotNull TimesTablePanel panel, boolean drawPoints) {
        setDrawPoints(drawPoints);
    }

    @Override
    public void onYInvertedChanged(@NotNull TimesTablePanel panel, boolean yInverted) {
        setInvertY(yInverted);
    }

    @Override
    public void onXInvertedChanged(@NotNull TimesTablePanel panel, boolean xInverted) {
        setInvertX(xInverted);
    }

    @Override
    public void onScaleChanged(@NotNull TimesTablePanel panel, double scale) {
        setScale(scale, true);
    }

    @Override
    public void onDragChanged(@NotNull TimesTablePanel panel, @Nullable Size drag) {
        syncResetScaleAndDragButton();
    }


    /* ................................ Actions ................... */

    public enum ActionInfo implements ActionInfoI {
        DRAG_UP(String.valueOf(Format.ARROW_UP), "Drag Up", KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK)),
        DRAG_DOWN(String.valueOf(Format.ARROW_DOWN), "Drag Down", KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK)),
        DRAG_LEFT(String.valueOf(Format.ARROW_LEFT), "Drag Left", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK)),
        DRAG_RIGHT(String.valueOf(Format.ARROW_RIGHT), "Drag Right", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK)),

        SCALE_UP(R.getScaleText(true), R.getScaleShortDescription(true), KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK)),
        SCALE_DOWN(R.getScaleText(false), R.getScaleShortDescription(false), KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK)),

        INVERT_X(R.getInvertXText(), R.getInvertXShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK)),
        INVERT_Y(R.getInvertYText(), R.getInvertYShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.SHIFT_DOWN_MASK)),
        TOGGLE_DARK_MODE(R.getDarkModeText(), R.getDarkModeShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.SHIFT_DOWN_MASK)),


        PLAY("Play", null, null),
        PAUSE("Pause", null, KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0)),
        STOP("Stop", null, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)),
        TOGGLE_PLAY_PAUSE(R.getPlayPauseText(true), R.getPlayPauseShortDes(true), KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0)),

        RESET_MAIN(R.getResetMainText(), R.getResetMainShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_R, 0)),
        RESET_SCALE("Reset Scale", null, null),
        RESET_SCALE_DRAG(R.getResetScaleAndDragText(), R.getResetScaleAndDragShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK)),
        RESET_FULL(R.getResetAllText(), R.getResetAllShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)),

        TOGGLE_MENUBAR(R.getToggleMenuBarText(true), R.getToggleMenuBarShortDescription(true), KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.SHIFT_DOWN_MASK)),
        TOGGLE_FULLSCREEN(R.getFullscreenText(), R.getFullscreenShortDescription(false), KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)),
        TOGGLE_STICK_ON_INT(R.getStickOnIntText(), R.getStickOnIntTooltipText(), KeyStroke.getKeyStroke(KeyEvent.VK_S, 0)),
        TOGGLE_CONTROLS(R.getToggleControlsText(true), R.getToggleControlsShortDescription(true), KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.SHIFT_DOWN_MASK)),
        TOGGLE_DRAW_CIRCLE(R.getDrawCircleText(), R.getDrawCircleShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_C, 0)),
        TOGGLE_DRAW_POINTS(R.getDrawPointsText(), R.getDrawPointsShortDes(), KeyStroke.getKeyStroke(KeyEvent.VK_P, 0)),
        TOGGLE_PRESENTATION_MODE(R.getTogglePresentationModeText(false), R.getTogglePresentationModeShortDescription(false), KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK))
        ;


        @NotNull
        public final String displayName;

        @Nullable
        public final String shortDescription;

        @Nullable
        public final KeyStroke keyStroke;

        ActionInfo(@NotNull String name, @Nullable String shortDescription, @Nullable KeyStroke keyStroke) {
            this.displayName = name;
            this.shortDescription = shortDescription;
            this.keyStroke = keyStroke;
        }

        @Override
        public @NotNull String displayName() {
            return displayName;
        }

        @Override
        public @Nullable String shortDescription() {
            return shortDescription;
        }

        @Override
        public @Nullable KeyStroke keyStroke() {
            return keyStroke;
        }

        @Override
        public @Nullable Icon getLargeIconOnSelect(boolean selected) {
            return null;
        }

        @Override
        public @Nullable Icon getSmallIconOnSelect(boolean selected) {
            return null;
        }
    }


    public class UiAction extends BaseAction {

        @NotNull
        private final TimesTableUi.ActionInfo actionInfo;

        private UiAction(@NotNull TimesTableUi.ActionInfo actionInfo) {
            this.actionInfo = actionInfo;
            useInfo(actionInfo);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (actionInfo) {
                case DRAG_UP -> panel.dragYByUnit(false);
                case DRAG_DOWN -> panel.dragYByUnit(true);
                case DRAG_LEFT -> panel.dragXByUnit(false);
                case DRAG_RIGHT -> panel.dragXByUnit(true);
                case SCALE_UP -> panel.incrementScaleByUnit();
                case SCALE_DOWN -> panel.decrementScaleByUnit();
                case PLAY -> panel.setPlay(true);
                case PAUSE -> panel.setPlay(false);
                case STOP -> panel.stop();
                case TOGGLE_PLAY_PAUSE -> panel.togglePlay();
                case RESET_MAIN -> panel.reset(false);
                case RESET_SCALE -> panel.resetScale();
                case RESET_SCALE_DRAG -> panel.resetScaleAndDrag();
                case RESET_FULL -> panel.reset(true);
                case TOGGLE_FULLSCREEN -> toggleFullscreen();
                case TOGGLE_STICK_ON_INT -> panel.toggleTimesFactorStickOnIntEnabled();
                case TOGGLE_CONTROLS -> toggleControlsVisibility();
                case TOGGLE_DRAW_CIRCLE -> panel.toggleDrawCircle();
                case TOGGLE_DRAW_POINTS -> panel.toggleDrawPoints();
                case INVERT_X -> panel.toggleInvertX();
                case INVERT_Y -> panel.toggleInvertY();
                case TOGGLE_DARK_MODE -> toggleDarkMode();
                case TOGGLE_MENUBAR -> toggleMenuBarVisible();
                case TOGGLE_PRESENTATION_MODE -> togglePresentationMode();
            }
        }

    }


    private class MouseHandler implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                toggleFullscreen();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }


    private void setupActionKeyBindings(@NotNull Collection<InputMap> inputMaps, @NotNull ActionMap actionMap) {
        for (ActionInfo actionInfo : ActionInfo.values()) {
            if (actionInfo.keyStroke != null) {
                for (InputMap inMap : inputMaps) {
                    inMap.put(actionInfo.keyStroke, actionInfo);
                }

                actionMap.put(actionInfo, uia(actionInfo));
            }
        }
    }

    private void setupActionKeyBindings(@NotNull JComponent component, @NotNull int... inputMapConditions) {
        final List<InputMap> maps = new LinkedList<>();
        for (int i : inputMapConditions) {
            maps.add(component.getInputMap(i));
        }

        setupActionKeyBindings(maps, component.getActionMap());
    }

    @NotNull
    private final EnumMap<ActionInfo, UiAction> mActionMap = new EnumMap<>(ActionInfo.class);

    @NotNull
    public final TimesTableUi.UiAction getUia(@NotNull TimesTableUi.ActionInfo info) {
        UiAction uia = mActionMap.get(info);

        if (uia == null) {
            uia = new UiAction(info);
            mActionMap.put(info, uia);
        }

        return uia;
    }

    @NotNull
    public final TimesTableUi.UiAction uia(@NotNull TimesTableUi.ActionInfo actionInfo) {
        final UiAction uiAction = getUia(actionInfo);
//        action.ensureListener(this);
        return uiAction;
    }

}
