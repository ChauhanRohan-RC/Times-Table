package main.ui;

import main.R;
import main.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import main.util.Listeners;
import main.math.RMath;
import main.util.Size;
import main.util.Ui;
import main.math.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class TimesTablePanel extends JPanel {

    public static final String TAG = "TimesTableCanvas";

    public enum EndBehaviour {
        PAUSE("Pause"),
        REPEAT("Repeat"),
        CYCLE("Cycle")

        ;


        @NotNull
        public final String displayName;

        EndBehaviour(@NotNull String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }


        @Nullable
        @Unmodifiable
        private static EndBehaviour[] sValues;

        public static EndBehaviour[] sharedValues() {
            if (sValues == null) {
                sValues = values();
            }

            return sValues;
        }
    }


    public interface Listener {

        void onIsPlayingChanged(@NotNull TimesTablePanel panel, boolean playing);

        void onTimesFactorSpeedChanged(@NotNull TimesTablePanel panel, float percent);

        void onTimesFactorChanged(@NotNull TimesTablePanel panel, float timesFactor);

        void onPointsCountChanged(@NotNull TimesTablePanel panel, int count);

        void onTimesFactorStickOnIntChanged(@NotNull TimesTablePanel panel, boolean stickingEnabled);

        void onEndBehaviourChanged(@NotNull TimesTablePanel panel, @NotNull EndBehaviour old, @NotNull EndBehaviour _new);

        void onDrawCircleChanged(@NotNull TimesTablePanel panel, boolean drawCircle);

        void onDrawPointsChanged(@NotNull TimesTablePanel panel, boolean drawPoints);

        void onYInvertedChanged(@NotNull TimesTablePanel panel, boolean yInverted);

        void onXInvertedChanged(@NotNull TimesTablePanel panel, boolean xInverted);

        default void onScaleChanged(@NotNull TimesTablePanel panel, double scale) { }

        default void onDragChanged(@NotNull TimesTablePanel panel, @Nullable Size drag) { }
    }



    @NotNull
    public static final EndBehaviour DEFAULT_END_BEHAVIOUR = EndBehaviour.CYCLE;


    public static final boolean DEFAULT_TIMES_FACTOR_STICK_WHEN_INT_ENABLED = false;
    public static final long TIMES_FACTOR_STICK_WHEN_INT_DURATION_MS = 600;

    public static final float TIMES_FACTOR_MIN = 1;
    public static final float TIMES_FACTOR_MAX = 500;

    protected static float getTimesFactorStart() {
        return TIMES_FACTOR_MIN;
    }

    public static float getTimesFactorIn01(float timesFactor) {
        return RMath.map(timesFactor, TIMES_FACTOR_MIN, TIMES_FACTOR_MAX, 0, 1);
    }

    public static float getTimesFactorPercentage(float timesFactor) {
        return RMath.map(timesFactor, TIMES_FACTOR_MIN, TIMES_FACTOR_MAX, 0, 100);
    }

    public static final int POINTS_COUNT_MIN = 10;
    public static final int POINTS_COUNT_MAX = 400;
    public static final int POINTS_COUNT_DEFAULT = 200;

    public static final float TIMES_FACTOR_STEP_PER_MS_MIN = 0.0001f;
    public static final float TIMES_FACTOR_STEP_PER_MS_MAX = 0.002f;
    public static final float TIMES_FACTOR_STEP_PER_MS_DEFAULT = getTimesFactorStepPerMs(25);

    public static float getTimesFactorSpeedPercent(float stepPerMs) {
        return RMath.map(RMath.constraint(Math.abs(stepPerMs), TIMES_FACTOR_STEP_PER_MS_MIN, TIMES_FACTOR_STEP_PER_MS_MAX), TIMES_FACTOR_STEP_PER_MS_MIN, TIMES_FACTOR_STEP_PER_MS_MAX, 0, 100);
    }

    public static float getTimesFactorStepPerMs(float speedPercent) {
        return RMath.map(RMath.constraint(speedPercent, 0, 100), 0, 100, TIMES_FACTOR_STEP_PER_MS_MIN, TIMES_FACTOR_STEP_PER_MS_MAX);
    }


    private float mTimesFactor = getTimesFactorStart();
    private float mTimesFactorStepPerMs = TIMES_FACTOR_STEP_PER_MS_DEFAULT;
    @Nullable
    private Float mPendingStartTimesFactor;
    private long mLastMainLoopTimeStamp = -1;
    @Nullable
    private Long mLastStickTimeStamp;


    private int mPointsCount = POINTS_COUNT_DEFAULT;
    private boolean mTimesFactorStickOnIntEnabled = DEFAULT_TIMES_FACTOR_STICK_WHEN_INT_ENABLED;

    @NotNull
    private EndBehaviour mEndBehaviour = DEFAULT_END_BEHAVIOUR;

    @NotNull
    private final Timer mLooper;
    @NotNull
    private final Listeners<Listener> mListeners;

    private boolean mInvertX = GlConfig.DEFAULT_INVERT_X;
    private boolean mInvertY = GlConfig.DEFAULT_INVERT_Y;
    private boolean mDrawCircle = GlConfig.DEFAULT_DRAW_CIRCLE;
    private boolean mDrawPoints = GlConfig.DEFAULT_DRAW_POINTS;
    private double mScale = 1;
    @Nullable
    private Size mDrag;
    @NotNull
    private final MouseHandler mMouseHandler = new MouseHandler();
    @NotNull
    private final ComponentListener mComponentListener = new ComponentListener();

    public TimesTablePanel() {
        mLooper = Ui.createLooper(this::mainLoop);

        mListeners = new Listeners<>();
        mListeners.setSafeIterationEnabled(false);

        updateTheme();

        addMouseWheelListener(mMouseHandler);
        addMouseMotionListener(mMouseHandler);
        addMouseListener(mMouseHandler);
        addComponentListener(mComponentListener);
    }

    public void updateTheme() {
        setBackground(GlConfig.bg());
        update();
    }

    protected float getCircleRadius(int width, int height) {
        return Math.min(width, height) / 2.8f;
    }

    protected float getPointRadius(float circleRadius, int pointsCount) {
        return 1.4f;
    }

//    @NotNull
//    protected Vector createVector(float index, int count, float mag) {
//        return Vector.fromAngle(U.map(index, 0, count, 0, U.TWO_PI)).mult(mag);
//    }

    @NotNull
    protected Vector createVector(float index, float unitTheta, float mag) {
        return Vector.fromAngle(index * unitTheta + RMath.PI).mult(mag);
    }


    protected void draw(Graphics2D g) {
        final int width = getWidth(), height = getHeight();
        final float timesFactor = mTimesFactor;
        final int pointsCount = mPointsCount;

        // BG
//        g.setColor(main.ui.GlConfig.bg());
//        g.fillRect(0, 0, width, height);

        // 1. Status
        final String statusText = R.getStatusText(timesFactor);
        if (!(statusText == null || statusText.isEmpty())) {
            g.setColor(GlConfig.fgDark());
            g.setFont(g.getFont().deriveFont(20f));
            g.drawString(String.format("Times: %.2f", timesFactor), 20, height - 20);
        }

        /* ..........................  Pre-Transforms ...........................*/
        final AffineTransform t = g.getTransform();

        // 1. Translate
        Size translation = new Size(width / 2f, height / 2f);
        final Size drag = getDrag();
        if (drag != null)
            translation = translation.add(drag);
        t.translate(translation.width, translation.height);

        // 2. Scale
        final double scale = mScale;
        t.scale((mInvertX? -1: 1) * scale, (mInvertY? -1: 1) * scale);

        g.setTransform(t);


        /* ........................... Main Drawing ............................... */

        // Circle
        final float circleRadius = getCircleRadius(width, height);
        final boolean drawCircle = mDrawCircle;
        if (drawCircle) {
            g.setColor(GlConfig.circleColor(timesFactor));
            g.draw(new Ellipse2D.Float(-circleRadius, -circleRadius, circleRadius * 2, circleRadius * 2));
        }

        // Points
        final float pointRadius = getPointRadius(circleRadius, pointsCount), pointDia = pointRadius * 2;
        final float delTheta = RMath.TWO_PI / pointsCount;
        Vector v1, v2;

        final boolean drawPoints = mDrawPoints;
        final Color pointColor = GlConfig.pointColor(timesFactor);
        final IntFunction<Color> colorFunc = GlConfig.patternColorFunction(pointsCount, timesFactor);

        for (int i=0; i < pointsCount; i++) {
            // point
            v1 = createVector(i, delTheta, circleRadius);
            if (drawPoints) {
                g.setColor(pointColor);
                g.fill(new Ellipse2D.Float(v1.x - pointRadius, v1.y - pointRadius, pointDia, pointDia));
            }

            // line
            float i2 = (i * timesFactor) % pointsCount;
            if (i != i2) {
                v2 = createVector(i2, delTheta, circleRadius);
                g.setColor(colorFunc.apply(i));
                g.draw(new Line2D.Float(v1.x, v1.y, v2.x, v2.y));
            }
        }

    }


    @Override
    protected void paintComponent(Graphics _g) {
        super.paintComponent(_g);

        final Graphics2D g2d = (Graphics2D) _g;
        if (GlConfig.FORCE_ANTIALIASING) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        draw(g2d);
    }


    protected void mainLoop() {
        final long pTimeMs = mLastMainLoopTimeStamp;
        final long cTimeMs = System.currentTimeMillis();

        if (pTimeMs != -1) {
            final Long lastStickMs = mLastStickTimeStamp;
            boolean sticked = mTimesFactorStickOnIntEnabled && lastStickMs != null && (cTimeMs - lastStickMs) < TIMES_FACTOR_STICK_WHEN_INT_DURATION_MS;

            if (!sticked) {
                final boolean inc = mTimesFactorStepPerMs > 0;
                final float step = mTimesFactorStepPerMs * (cTimeMs - pTimeMs);
                if (mTimesFactorStickOnIntEnabled) {
                    float nextStop;
                    if (RMath.isInt(mTimesFactor)) {
                        nextStop = (mTimesFactor + (inc? 1: -1));
                    } else {
                        nextStop = (float) (inc? Math.ceil(mTimesFactor): Math.floor(mTimesFactor));
                    }

                    if (Math.abs(nextStop - mTimesFactor) <= Math.abs(step)) {
                        mTimesFactor = nextStop;
                        sticked = true;
                    }
                }

                if (sticked) {
                    mLastStickTimeStamp = cTimeMs;
                }  else {
                    mTimesFactor += step;
                    mLastStickTimeStamp = null;
                }

                if (inc? mTimesFactor >= TIMES_FACTOR_MAX: mTimesFactor <= TIMES_FACTOR_MIN) {        // DONE
                    mTimesFactor = RMath.constraint(mTimesFactor, TIMES_FACTOR_MIN, TIMES_FACTOR_MAX);

                    switch (mEndBehaviour) {
                        case PAUSE -> setPlay(false);
                        case REPEAT -> mTimesFactor = getTimesFactorStart();       // start again
                        case CYCLE ->  mTimesFactorStepPerMs *= -1;        // keep cycling back and forth
                    }
                }
            }
        } else {
            if (mPendingStartTimesFactor != null) {
                mTimesFactor = mPendingStartTimesFactor;
                mPendingStartTimesFactor = null;
            } else {
                mTimesFactor = getTimesFactorStart();
            }
        }

        mLastMainLoopTimeStamp = cTimeMs;
        update();
    }

    public final void update() {
        repaint();
    }






    public void addListener(@NotNull Listener l) {
        mListeners.addListener(l);
    }

    public boolean removeListener(@NotNull Listener l) {
        return mListeners.removeListener(l);
    }

    private void forEachListener(@NotNull Consumer<Listener> action) {
        mListeners.forEachListener(action);
    }



    public void resetTimesFactor(boolean update) {
        mPendingStartTimesFactor = null;
        mTimesFactor = getTimesFactorStart();
        mLastMainLoopTimeStamp = -1;

        if (update) {
            update();
        }
    }

    private boolean resetScale(boolean update) {
//        final boolean pivotChanged = setScalePivot(null, false);
        final boolean scaleChanged = setScale(1, false);

//        final boolean changed = pivotChanged || scaleChanged;

        if (update && scaleChanged) {
            update();
        }

        return scaleChanged;
    }

    public final boolean resetScale() {
        return resetScale(true);
    }

    public boolean resetDrag(boolean update) {
        return setDrag(null, update);
    }

    private boolean resetScaleAndDrag(boolean update) {
        final boolean scaleChanged = resetScale(false);
        final boolean dragChanged = resetDrag(false);

        final boolean changed = dragChanged || scaleChanged;
        if (update && changed) {
            update();
        }

        return changed;
    }

    public final void resetScaleAndDrag() {
        resetScaleAndDrag(true);
    }

    public final void reset(boolean resetScaleAndDrag) {
        resetTimesFactor(false);
        if (resetScaleAndDrag) {
            resetScaleAndDrag(false);
        }

        update();
    }


    private void noteCurrentTimesFactorOnPause() {
        mPendingStartTimesFactor = mTimesFactor;
        mLastMainLoopTimeStamp = -1;
    }


    protected void onIsPlayingChanged(boolean playing) {
        if (!playing) {
            noteCurrentTimesFactorOnPause();
        }

        forEachListener(l -> l.onIsPlayingChanged(this, playing));
    }


    public final boolean isPlaying() {
        return mLooper.isRunning();
    }

    public final void setPlay(boolean play) {
        if (isPlaying() == play) {
            return;
        }

        if (play) {
            mLooper.start();
        } else {
            mLooper.stop();
        }

        onIsPlayingChanged(play);
    }

    public final boolean togglePlay() {
        final boolean play = !isPlaying();
        setPlay(play);
        return play;
    }

    public final void stop() {
        setPlay(false);
        reset(false);
    }

    public final int getLoopDelay() {
        return mLooper.getDelay();
    }

    public final void setLoopDelay(int loopDelay) {
        mLooper.setDelay(loopDelay);
    }



    protected void onTimesFactorChanged(float timesFactor) {
        update();
        forEachListener(l -> l.onTimesFactorChanged(this, timesFactor));
    }

    private void setTimesFactorInternal(float timesFactor) {
        mTimesFactor = timesFactor;
        onTimesFactorChanged(timesFactor);
    }

    public final float setTimesFactor(float timesFactor) {
        timesFactor = RMath.constraint(timesFactor, TIMES_FACTOR_MIN, TIMES_FACTOR_MAX);
        if (mTimesFactor != timesFactor) {
            setTimesFactorInternal(timesFactor);
        }

        return mTimesFactor;
    }

    public final float getTimesFactor() {
        return mTimesFactor;
    }


    protected void onPointsCountChanged(int pointsCount) {
        update();
        forEachListener(l -> l.onPointsCountChanged(this, pointsCount));
    }

    private void setPointsCountInternal(int pointsCount) {
        mPointsCount = pointsCount;
        onPointsCountChanged(pointsCount);
    }

    public final int setPointsCount(int pointsCount) {
        pointsCount = RMath.constraint(pointsCount, POINTS_COUNT_MIN, POINTS_COUNT_MAX);
        if (mPointsCount != pointsCount) {
            setPointsCountInternal(pointsCount);
        }

        return mPointsCount;
    }

    public final int getPointsCount() {
        return mPointsCount;
    }

    protected void onTimesFactorStepPerMsChanged(float stepPerMs) {
        final float percent = getTimesFactorSpeedPercent(stepPerMs);
        forEachListener(l -> l.onTimesFactorSpeedChanged(this, percent));
    }

    private float setTimesFactorStepPerMsInternal(float newStepPerMs) {
        if (mTimesFactorStepPerMs != newStepPerMs) {
            mTimesFactorStepPerMs = newStepPerMs;
            onTimesFactorStepPerMsChanged(newStepPerMs);
        }

        return mTimesFactorStepPerMs;
    }

    public final float setTimesFactorSpeedPercentage(float percent) {
        return setTimesFactorStepPerMsInternal((mTimesFactorStepPerMs < 0? -1: 1) * getTimesFactorStepPerMs(percent));
    }

    public final float getTimesFactorSpeedPercent() {
        return getTimesFactorSpeedPercent(mTimesFactorStepPerMs);
    }



    protected void onTimesFactorStickOnIntChanged(boolean stickOnIntEnabled) {
        update();
        forEachListener(l -> l.onTimesFactorStickOnIntChanged(this, stickOnIntEnabled));
    }

    private void setTimesFactorStickOnIntEnabledInternal(boolean stickOnIntEnabled) {
        mTimesFactorStickOnIntEnabled = stickOnIntEnabled;
        onTimesFactorStickOnIntChanged(stickOnIntEnabled);
    }

    public final boolean setTimesFactorStickOnIntEnabled(boolean stickOnIntEnabled) {
        if (mTimesFactorStickOnIntEnabled == stickOnIntEnabled)
            return false;
        setTimesFactorStickOnIntEnabledInternal(stickOnIntEnabled);
        return true;
    }

    public final boolean toggleTimesFactorStickOnIntEnabled() {
        setTimesFactorStickOnIntEnabledInternal(!mTimesFactorStickOnIntEnabled);
        return mTimesFactorStickOnIntEnabled;
    }

    public final boolean isTimesFactorStickOnIntEnabled() {
        return mTimesFactorStickOnIntEnabled;
    }


    protected void onEndBehaviourChanged(@NotNull EndBehaviour old, @NotNull EndBehaviour _new) {
        forEachListener(l -> l.onEndBehaviourChanged(this, old, _new));
    }

    public void setEndBehaviour(@NotNull EndBehaviour endBehaviour) {
        if (mEndBehaviour != endBehaviour) {
            final EndBehaviour prev = mEndBehaviour;
            mEndBehaviour = endBehaviour;
            onEndBehaviourChanged(prev, endBehaviour);
        }
    }

    @NotNull
    public EndBehaviour getEndBehaviour() {
        return mEndBehaviour;
    }

    public void setPatternColorMode(@NotNull GlConfig.PatternColorMode colorMode) {
        GlConfig.setPatternColorMode(colorMode);
        updateTheme();
    }



    /*  ..................................... Transforms ............................*/

    private void onDrawCircleChanged(boolean drawCircle) {
        update();
        forEachListener(l -> l.onDrawCircleChanged(this, drawCircle));
    }

    public void setDrawCircle(boolean drawCircle) {
        if (mDrawCircle != drawCircle) {
            mDrawCircle = drawCircle;
            onDrawCircleChanged(drawCircle);
        }
    }

    public void toggleDrawCircle() {
        setDrawCircle(!mDrawCircle);
    }

    public boolean isDrawCircleEnabled() {
        return mDrawCircle;
    }


    private void onDrawPointsChanged(boolean drawPoints) {
        update();
        forEachListener(l -> l.onDrawPointsChanged(this, drawPoints));
    }

    public void setDrawPoints(boolean drawPoints) {
        if (mDrawPoints != drawPoints) {
            mDrawPoints = drawPoints;
            onDrawPointsChanged(drawPoints);
        }
    }

    public void toggleDrawPoints() {
        setDrawPoints(!mDrawPoints);
    }

    public boolean isDrawPointsEnabled() {
        return mDrawPoints;
    }


    /* Inversion */

    private void onInvertYChanged(boolean yInverted) {
        update();
        forEachListener(l -> l.onYInvertedChanged(this, yInverted));
    }

    public void setInvertY(boolean invertY) {
        if (mInvertY != invertY) {
            mInvertY = invertY;
            onInvertYChanged(invertY);
        }
    }

    public void toggleInvertY() {
        setInvertY(!mInvertY);
    }

    public boolean isYInverted() {
        return mInvertY;
    }



    private void onInvertXChanged(boolean xInverted) {
        update();
        forEachListener(l -> l.onXInvertedChanged(this, xInverted));
    }

    public void setInvertX(boolean invertX) {
        if (mInvertX != invertX) {
            mInvertX = invertX;
            onInvertXChanged(invertX);
        }
    }

    public void toggleInvertX() {
        setInvertX(!mInvertX);
    }

    public boolean isXInverted() {
        return mInvertX;
    }


    /* Scale */

    protected boolean shouldScaleByMouseWheel(@NotNull MouseWheelEvent e) {
        return true;
    }

    protected double getScaleIncrement(@NotNull MouseWheelEvent e) {
        return -e.getPreciseWheelRotation() * GlConfig.SCALE_WHEEL_ROTATION_MULTIPLIER;
    }

    protected double getScaleUnitIncrement(double scale) {
        final int int_scale = (int) scale;
        if (int_scale == scale)
            return GlConfig.DEFAULT_SCALE_UNIT_INCREMENT;

        return Math.min(int_scale + 1 - scale, GlConfig.DEFAULT_SCALE_UNIT_INCREMENT);
    }

    protected double getScaleUnitDecrement(double scale) {
        final int int_scale = (int) scale;
        final double def = scale > 1? GlConfig.DEFAULT_SCALE_UNIT_INCREMENT: GlConfig.DEFAULT_SCALE_UNIT_DECREMENT_BELOW_1;
        if (int_scale == scale)
            return def;
        return Math.min(scale - int_scale, def);
    }

    public double getMinimumScale() {
        return GlConfig.DEFAULT_SALE_MIN;
    }

    public double getMaximumScale() {
        return GlConfig.DEFAULT_SCALE_MAX;
    }


    protected void onScaleChanged(double scale, boolean update) {
        if (update) {
            update();
        }

        forEachListener(l -> l.onScaleChanged(this, scale));
    }

    private boolean setScale(double scale, boolean update) {
        scale = RMath.constraint(getMinimumScale(), getMaximumScale(), scale);
        if (mScale == scale)
            return false;

        mScale = scale;
        onScaleChanged(scale, update);
        return true;
    }

    public final boolean setScale(double scale) {
        return setScale(scale, true);
    }

    private boolean increaseScale(double scaleDelta, boolean update) {
        return setScale(mScale + scaleDelta, update);
    }

    public final boolean increaseScale(double scaleDelta) {
        return increaseScale(scaleDelta, true);
    }

    public final boolean incrementScaleByUnit() {
        return increaseScale(getScaleUnitIncrement(mScale));
    }

    public final boolean decrementScaleByUnit() {
        return increaseScale(-getScaleUnitDecrement(mScale));
    }


    public final double getScale() {
        return mScale;
    }

//    protected void onScalePivotChanged(@Nullable Point2D scalePivot, boolean update) {
//        if (update) {
//            update();
//        }
//
//        forEachPanelListener(l -> l.onScalePivotChanged(scalePivot));
//    }
//
//    private boolean setScalePivot(@Nullable Point2D scalePivot, boolean update) {
//        if (Objects.equals(scalePivot, mScalePivot))
//            return false;
//        mScalePivot = scalePivot;
//        onScalePivotChanged(scalePivot, update);
//        return true;
//    }
//
//    public final boolean setScalePivot(@Nullable Point2D scalePivot) {
//        return setScalePivot(scalePivot, true);
//    }
//
//    @NotNull
//    public final Point2D getScalePivot() {
//        return mScalePivot != null? mScalePivot: new Point2D.Double(getWidth() / 2f, getHeight() / 2f);
//    }



    /* Drag */

    protected boolean shouldDragOnMousePress(@NotNull MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1;
    }

    public boolean isMaxDragDimensionDependent() {
        return true;
    }

    @Nullable
    public Size getMaxDrag() {
        final double s = Math.max(0.5, mScale);
        return new Size(getWidth() * s, getHeight() * s);
    }

    protected void onDragChanged(@Nullable Size drag, boolean update) {
        if (update) {
            update();
        }

        final Size copy = drag != null? drag.copy(): null;
        forEachListener(l -> l.onDragChanged(this, copy));
    }

    private boolean setDrag(@Nullable Size drag, boolean update) {
        Size max;
        if (drag != null && (max = getMaxDrag()) != null) {
            drag = new Size(Math.signum(drag.width) * Math.min(Math.abs(max.width), Math.abs(drag.width)), Math.signum(drag.height) * Math.min(Math.abs(max.height), Math.abs(drag.height)));
        }

        if (Objects.equals(mDrag, drag))
            return false;

        mDrag = drag;
        onDragChanged(drag, update);
        return true;
    }

    public final boolean setDrag(@Nullable Size drag) {
        return setDrag(drag, true);
    }

    private boolean dragBy(@NotNull Size dragDelta, boolean update) {
        return setDrag(mDrag != null? mDrag.add(dragDelta): dragDelta, update);
    }

    public final boolean dragBy(@NotNull Size dragDelta) {
        return dragBy(dragDelta, true);
    }

    public final boolean dragXBy(double dragDelta) {
        return dragBy(new Size(dragDelta, 0), true);
    }

    public final boolean dragYBy(double dragDelta) {
        return dragBy(new Size(0, dragDelta), true);
    }

    @Nullable
    public final Size getDrag() {
        return mDrag;
    }

    public final boolean hasScale() {
        return mScale != 1;
    }


    public final boolean hasDrag() {
        return mDrag != null && (mDrag.width != 0 || mDrag.height != 0);
    }

    public final boolean hasScaleOrDrag() {
        return hasScale() || hasDrag();
    }



    public double getXDragUnitIncrement() {
        return getWidth() / GlConfig.DRAG_X_UNITS;
    }

    public double getYDragUnitIncrement() {
        return getHeight() / GlConfig.DRAG_Y_UNITS;
    }

    public final boolean dragXByUnit(boolean right) {
        return dragXBy((right? 1: -1) * getXDragUnitIncrement());
    }

    public final boolean dragYByUnit(boolean down) {
        return dragYBy((down? 1: -1) * getYDragUnitIncrement());
    }

    @Nullable
    public final TimesTableUi getTimesTableUi() {
        final Window w = SwingUtilities.windowForComponent(TimesTablePanel.this);
        return w instanceof TimesTableUi? (TimesTableUi) w: null;
    }





    /* ........................... Listeners .......................*/

    private class ComponentListener implements java.awt.event.ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            if (mDrag != null && isMaxDragDimensionDependent()) {
                setDrag(mDrag.copy());      // update
            }
        }

        @Override
        public void componentMoved(ComponentEvent e) {
        }

        @Override
        public void componentShown(ComponentEvent e) {

        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }



    private class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

        @Nullable
        private Point2D mMouseDragStartPoint;
        @Nullable
        private Size mMouseDragStart;     // For each Press-Release

        // Mouse main.main.Main

        @Override
        public void mouseClicked(MouseEvent e) {
            TimesTableUi ui;
            if (e.getClickCount() == 2 && (ui = getTimesTableUi()) != null) {
                ui.toggleFullscreen();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (shouldDragOnMousePress(e)) {
                mMouseDragStartPoint = e.getPoint();
                mMouseDragStart = mDrag;
            } else {
                mMouseDragStartPoint = null;
                mMouseDragStart = null;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mMouseDragStartPoint = null;
            mMouseDragStart = null;
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        // Mouse Motion

        @Override
        public void mouseDragged(MouseEvent e) {
            final Point2D startPoint = mMouseDragStartPoint;
            final Size startDrag = mMouseDragStart;
            if (startPoint != null) {
                final Size del = new Size(startPoint, e.getPoint());
                setDrag(startDrag != null? startDrag.add(del): del);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            // no-op
        }

        // Mouse Wheel

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            if (!shouldScaleByMouseWheel(e))
                return;

//            final boolean pivotChanged = setScalePivot(e.getPoint(), false);
            final boolean scaleChanged = increaseScale(getScaleIncrement(e), false);

//            if (pivotChanged || scaleChanged) {
//                update();
//            }

            if (scaleChanged) {
                update();
            }
        }
    }

}
