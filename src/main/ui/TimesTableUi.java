package main.ui;

import main.R;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.util.Size;
import main.util.Ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
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


    final JToggleButton playToggle;
    final JButton resetButton;
    final JButton resetScaleAndDragButton;
    final JCheckBox invertXCheck;
    final JCheckBox invertYCheck;
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
        super(title == null || title.isEmpty()? R.APP_NAME : title);

        panel = new TimesTablePanel();

        // Points Count
        final int pointsCount = panel.getPointsCount();
        pointsCountText = new JLabel(R.getPointsCountText(pointsCount));

        pointsCountSlider = new JSlider(SwingConstants.HORIZONTAL, TimesTablePanel.POINTS_COUNT_MIN, TimesTablePanel.POINTS_COUNT_MAX, pointsCount);
        pointsCountSlider.setToolTipText(R.getPointsCountTooltipText());
        pointsCountSlider.setLabelTable(pointsCountSlider.createStandardLabels(TimesTablePanel.POINTS_COUNT_MAX - TimesTablePanel.POINTS_COUNT_MIN, TimesTablePanel.POINTS_COUNT_MIN));
        pointsCountSlider.setPaintLabels(true);

        // Times factor
        final float timesFactor = panel.getTimesFactor();
        timesfactorText = new JLabel(R.getTimesFactorText(timesFactor));

        final int min = timesFactorToInt(TimesTablePanel.TIMES_FACTOR_MIN), max = timesFactorToInt(TimesTablePanel.TIMES_FACTOR_MAX), intTimesF = timesFactorToInt(timesFactor);
        timesFactorSlider = new JSlider(SwingConstants.HORIZONTAL, min, max, intTimesF);
        timesFactorSlider.setToolTipText(R.getTimesFactorTooltipText());
        timesFactorSlider.setLabelTable(timesFactorSlider.createStandardLabels(max - min, min));
        timesFactorSlider.setPaintLabels(true);

        // Speed
        final int speed = speedPercentToInt(panel.getTimesFactorSpeedPercent());
        speedText = new JLabel(R.getSpeedPercentText(speed));

        speedSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 100, speed);
        speedSlider.setToolTipText(R.getSpeedSliderTooltipText());
        speedSlider.setLabelTable(speedSlider.createStandardLabels(25, 0));
        speedSlider.setPaintLabels(true);



        // Play/Pause Toggle
        final boolean playing = panel.isPlaying();
        playToggle = new JToggleButton(R.getPlayPauseText(playing), !playing);
        playToggle.setToolTipText(R.getPlayPauseTooltipText(playing));

        // Reset
        resetButton = new JButton(R.getResetText());
        resetButton.setToolTipText(R.getResetTooltipText());

        resetScaleAndDragButton = new JButton(R.getResetScaleAndDragText());
        resetScaleAndDragButton.setToolTipText(R.getResetScaleAndDragTooltipText());
        syncResetScaleAndDragButton();


        // Ops
        invertXCheck = new JCheckBox(R.getInvertXText());
        invertXCheck.setToolTipText(R.getInvertXTooltipText());
        invertXCheck.setSelected(panel.isXInverted());

        invertYCheck = new JCheckBox(R.getInvertYText());
        invertYCheck.setToolTipText(R.getInvertYTooltipText());
        invertYCheck.setSelected(panel.isYInverted());

        darkModeCheck = new JCheckBox(R.getDarkModeText());
        darkModeCheck.setToolTipText(R.getDarkModeTooltipText());
        darkModeCheck.setSelected(GlConfig.isDarkMode());

        stickOnIntCheck = new JCheckBox(R.getStickOnIntText());
        stickOnIntCheck.setToolTipText(R.getStickOnIntTooltipText());
        stickOnIntCheck.setSelected(panel.isTimesFactorStickOnIntEnabled());

        endBehaviourLabel = new JLabel(R.getEndBehaviourLabelText());
        endBehaviourLabel.setToolTipText(R.getEndBehaviourTooltipText());
        endBehaviourComboBox = new JComboBox<>(TimesTablePanel.EndBehaviour.sharedValues());
        endBehaviourComboBox.setSelectedIndex(panel.getEndBehaviour().ordinal());
        endBehaviourComboBox.setToolTipText(R.getEndBehaviourTooltipText());

        patternColorModeLabel = new JLabel(R.getPatternColorModeLabelText());
        patternColorModeLabel.setToolTipText(R.getPatternColorModeTooltipText());
        patternColorModeComboBox = new JComboBox<>(GlConfig.PatternColorMode.sharedValues());
        patternColorModeComboBox.setSelectedIndex(GlConfig.getPatternColorMode().ordinal());
        patternColorModeComboBox.setToolTipText(R.getPatternColorModeTooltipText());

        // Transforms
        scaleText = new JLabel(R.getScaleText(panel.getScale()));
        scaleIncButton = new JButton("+");
        scaleDecButton = new JButton("-");

        leftButton = new JButton("<");
        rightButton = new JButton(">");
        upButton = new JButton("\u02C4");
        downButton = new JButton("\u02C5");


        // Toggle Controls
        toggleControlsButton = new JButton(R.getToggleControlsText(GlConfig.DEFAULT_CONTROLS_VISIBLE));
        toggleControlsButton.setToolTipText(R.getToggleControlsTooltipText(GlConfig.DEFAULT_CONTROLS_VISIBLE));



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
        buttonsPanel.add(playToggle);
        buttonsPanel.add(resetButton);

        final JPanel checksPanel = new JPanel(new GridLayout(2, 2, 2, 2));
        checksPanel.add(stickOnIntCheck);
        checksPanel.add(darkModeCheck);
        checksPanel.add(invertXCheck);
        checksPanel.add(invertYCheck);

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
        playToggle.addItemListener(e -> setPlay(!playToggle.isSelected()));
        resetButton.addActionListener(e -> reset(false));
        resetScaleAndDragButton.addActionListener(e -> resetScaleAndDrag());

        stickOnIntCheck.addItemListener(e -> setStickOnIntEnabled(stickOnIntCheck.isSelected()));
        darkModeCheck.addItemListener(e -> setDarkMode(darkModeCheck.isSelected()));
        invertXCheck.addItemListener(e -> setInvertX(invertXCheck.isSelected()));
        invertYCheck.addItemListener(e -> setInvertY(invertYCheck.isSelected()));

        patternColorModeComboBox.addActionListener(e -> setPatternColorMode(GlConfig.PatternColorMode.sharedValues()[patternColorModeComboBox.getSelectedIndex()]));
        endBehaviourComboBox.addActionListener(e -> setEndBehaviour(TimesTablePanel.EndBehaviour.sharedValues()[endBehaviourComboBox.getSelectedIndex()]));
        scaleIncButton.addActionListener(e -> incrementScaleByUnit());
        scaleDecButton.addActionListener(e -> decrementScaleByUnit());

        leftButton.addActionListener(e -> panel.dragXByUnit(false));
        rightButton.addActionListener(e -> panel.dragXByUnit(true));
        upButton.addActionListener(e -> panel.dragYByUnit(false));
        downButton.addActionListener(e -> panel.dragYByUnit(true));

        toggleControlsButton.addActionListener(e -> toggleControlsVisibility());

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
            setFullscreenInternal(mFullscreen);     // sync
            requestFocusInWindow();
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

    }

    public final boolean isFullscreen() {
        return mFullscreen;
    }

    private void setFullscreenInternal(boolean fullscreen) {
        getGraphicsConfiguration().getDevice().setFullScreenWindow(fullscreen? TimesTableUi.this: null);
    }

    public final boolean setFullscreen(boolean fullscreen) {
        if (mFullscreen == fullscreen)
            return false;

        setFullscreenInternal(fullscreen);
        mFullscreen = fullscreen;
        onFullscreenChanged(fullscreen);
        return true;
    }

    public final void toggleFullscreen() {
        setFullscreen(!mFullscreen);
    }


    protected void onControlsVisibilityChanged(boolean controlsVisible) {
        toggleControlsButton.setText(R.getToggleControlsText(controlsVisible));
        toggleControlsButton.setToolTipText(R.getToggleControlsTooltipText(controlsVisible));
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


    public void setPlay(boolean play) {
        panel.setPlay(play);

        playToggle.setToolTipText(R.getPlayPauseTooltipText(play));
        playToggle.setText(R.getPlayPauseText(play));

        if (playToggle.isSelected() == play) {
            playToggle.setSelected(!play);
        }

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
        panel.setDarkMode(darkMode);

        if (darkModeCheck.isSelected() != darkMode) {
            darkModeCheck.setSelected(darkMode);
        }
    }

    public void setInvertX(boolean invertX) {
        panel.setInvertX(invertX);

        if (invertXCheck.isSelected() != invertX) {
            invertXCheck.setSelected(invertX);
        }
    }

    public void setInvertY(boolean invertY) {
        panel.setInvertY(invertY);

        if (invertYCheck.isSelected() != invertY) {
            invertYCheck.setSelected(invertY);
        }
    }

    public void setStickOnIntEnabled(boolean enabled) {
        panel.setTimesFactorStickOnIntEnabled(enabled);

        if (stickOnIntCheck.isSelected() != enabled) {
            stickOnIntCheck.setSelected(enabled);
        }
    }

    public void setEndBehaviour(@NotNull TimesTablePanel.EndBehaviour endBehaviour) {
        panel.setEndBehaviour(endBehaviour);

        if (endBehaviourComboBox.getSelectedIndex() != endBehaviour.ordinal()) {
            endBehaviourComboBox.setSelectedIndex(endBehaviour.ordinal());
        }
    }

    public void setPatternColorMode(@NotNull GlConfig.PatternColorMode patternColorMode) {
        panel.setPatternColorMode(patternColorMode);

        final int index =  patternColorMode.ordinal();
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
        scaleIncButton.setEnabled(scale < panel.getMaximumScale());
        scaleDecButton.setEnabled(scale > panel.getMinimumScale());
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
        resetScaleAndDragButton.setVisible(panel.hasScaleOrDrag());
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
    public void onIsPlayingChanged(boolean playing) {
        setPlay(playing);
    }

    @Override
    public void onTimesFactorSpeedChanged(float percent) {
        setSpeedPercent(percent);
    }

    @Override
    public void onTimesFactorChanged(float timesFactor) {
        setTimesFactor(timesFactor, true);
    }

    @Override
    public void onPointsCountChanged(int count) {
        setPointsCount(count);
    }

    @Override
    public void onTimesFactorStickOnIntChanged(boolean stickingEnabled) {
        setStickOnIntEnabled(stickingEnabled);
    }

    @Override
    public void onEndBehaviourChanged(TimesTablePanel.@NotNull EndBehaviour old, TimesTablePanel.@NotNull EndBehaviour _new) {
        setEndBehaviour(_new);
    }

    @Override
    public void onYInvertedChanged(boolean yInverted) {
        setInvertY(yInverted);
    }

    @Override
    public void onXInvertedChanged(boolean xInverted) {
        setInvertX(xInverted);
    }

    @Override
    public void onScaleChanged(double scale) {
        setScale(scale, true);
    }

    @Override
    public void onDragChanged(@Nullable Size drag) {
        syncResetScaleAndDragButton();
    }


    /* ................................ Actions ................... */

    public enum Action {
        DRAG_UP(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK)),
        DRAG_DOWN(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK)),
        DRAG_LEFT(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.CTRL_DOWN_MASK)),
        DRAG_RIGHT(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.CTRL_DOWN_MASK)),

        SCALE_UP(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK)),
        SCALE_DOWN(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK)),

        PLAY(null),
        PAUSE(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0)),
        STOP(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)),
        TOGGLE_PLAY_PAUSE(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0)),

        RESET_MAIN(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0)),
        RESET_SCALE(null),
        RESET_SCALE_DRAG(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.SHIFT_DOWN_MASK)),
        RESET_FULL(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)),

        TOGGLE_FULLSCREEN(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)),
        TOGGLE_STICK_ON_INT(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)),
        TOGGLE_CONTROLS(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));

        ;

        @Nullable
        public final KeyStroke keyStroke;

        Action(@Nullable KeyStroke keyStroke) {
            this.keyStroke = keyStroke;
        }
    }


    public class ActionHandler extends AbstractAction {

        @NotNull
        private final Action action;

        private ActionHandler(@NotNull Action action) {
            this.action = action;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (action) {
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
        for (Action action: Action.values()) {
            if (action.keyStroke != null) {
                for (InputMap inMap: inputMaps) {
                    inMap.put(action.keyStroke, action);
                }

                actionMap.put(action, new ActionHandler(action));
            }
        }
    }

    private void setupActionKeyBindings(@NotNull JComponent component, @NotNull int... inputMapConditions) {
        final List<InputMap> maps = new LinkedList<>();
        for (int i: inputMapConditions) {
            maps.add(component.getInputMap(i));
        }

        setupActionKeyBindings(maps, component.getActionMap());
    }


}
