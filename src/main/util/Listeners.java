package main.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class Listeners<T> {

    public static final boolean DEFAULT_SAFE_ITERATION = true;

    @Nullable
    private LinkedList<T> mListeners;
    private boolean mSafeIteration = DEFAULT_SAFE_ITERATION;

    public final int listenersCount() {
        return CollectionUtil.size(mListeners);
    }


    public boolean isSafeIterationEnabled() {
        return mSafeIteration;
    }

    public void setSafeIterationEnabled(boolean safeIteration) {
        mSafeIteration = safeIteration;
    }


    public final void addListener(@NotNull T listener) {
        if (mListeners == null) {
            mListeners = new LinkedList<>();
        }

        mListeners.add(listener);
    }

    public final boolean removeListener(@NotNull T listener) {
        return mListeners != null && mListeners.remove(listener);
    }

    public final void forEachListener(@NotNull Consumer<T> action) {
        final List<T> ls = mListeners;
        if (CollectionUtil.isEmpty(ls))
            return;

        for (T l: mSafeIteration? CollectionUtil.linkedListCopy(ls): ls) {          // safe-iteration (prevents concurrent modification)
            action.accept(l);
        }
    }
}
