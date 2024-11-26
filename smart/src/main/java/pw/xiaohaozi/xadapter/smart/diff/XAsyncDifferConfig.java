package pw.xiaohaozi.xadapter.smart.diff;



import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

/**
 * Configuration object for {@link ListAdapter}, {@link AsyncListDiffer}, and similar
 * background-thread list diffing adapter logic.
 * <p>
 * At minimum, defines item diffing behavior with a {@link DiffUtil.ItemCallback}, used to compute
 * item differences to pass to a RecyclerView adapter.
 *
 * @param <T> Type of items in the lists, and being compared.
 */
public final class XAsyncDifferConfig<T> {
    @Nullable
    private final Executor mMainThreadExecutor;
    @NonNull
    private final Executor mBackgroundThreadExecutor;
    @NonNull
    private final DiffUtil.ItemCallback<T> mDiffCallback;

    @SuppressWarnings("WeakerAccess") /* synthetic access */
    XAsyncDifferConfig(
            @Nullable Executor mainThreadExecutor,
            @NonNull Executor backgroundThreadExecutor,
            @NonNull DiffUtil.ItemCallback<T> diffCallback) {
        mMainThreadExecutor = mainThreadExecutor;
        mBackgroundThreadExecutor = backgroundThreadExecutor;
        mDiffCallback = diffCallback;
    }

    /** @hide */
    @SuppressWarnings("WeakerAccess")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Nullable
    public Executor getMainThreadExecutor() {
        return mMainThreadExecutor;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public Executor getBackgroundThreadExecutor() {
        return mBackgroundThreadExecutor;
    }

    @SuppressWarnings("WeakerAccess")
    @NonNull
    public DiffUtil.ItemCallback<T> getDiffCallback() {
        return mDiffCallback;
    }

    /**
     * Builder class for {@link androidx.recyclerview.widget.AsyncDifferConfig}.
     *
     * @param <T>
     */
    public static final class Builder<T> {
        @Nullable
        private Executor mMainThreadExecutor;
        private Executor mBackgroundThreadExecutor;
        private final DiffUtil.ItemCallback<T> mDiffCallback;

        public Builder(@NonNull DiffUtil.ItemCallback<T> diffCallback) {
            mDiffCallback = diffCallback;
        }

        /**
         * If provided, defines the main thread executor used to dispatch adapter update
         * notifications on the main thread.
         * <p>
         * If not provided, it will default to the main thread.
         *
         * @param executor The executor which can run tasks in the UI thread.
         * @return this
         *
         * @hide
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY)
        @NonNull
        public Builder<T> setMainThreadExecutor(Executor executor) {
            mMainThreadExecutor = executor;
            return this;
        }

        /**
         * If provided, defines the background executor used to calculate the diff between an old
         * and a new list.
         * <p>
         * If not provided, defaults to two thread pool executor, shared by all ListAdapterConfigs.
         *
         * @param executor The background executor to run list diffing.
         * @return this
         */
        @SuppressWarnings({"unused", "WeakerAccess"})
        @NonNull
        public Builder<T> setBackgroundThreadExecutor(Executor executor) {
            mBackgroundThreadExecutor = executor;
            return this;
        }

        /**
         * Creates a {@link AsyncListDiffer} with the given parameters.
         *
         * @return A new AsyncDifferConfig.
         */
        @NonNull
        public XAsyncDifferConfig<T> build() {
            if (mBackgroundThreadExecutor == null) {
                synchronized (sExecutorLock) {
                    if (sDiffExecutor == null) {
                        sDiffExecutor = Executors.newFixedThreadPool(2);
                    }
                }
                mBackgroundThreadExecutor = sDiffExecutor;
            }
            return new XAsyncDifferConfig<T>(
                    mMainThreadExecutor,
                    mBackgroundThreadExecutor,
                    mDiffCallback);
        }

        // TODO: remove the below once supportlib has its own appropriate executors
        private static final Object sExecutorLock = new Object();
        private static Executor sDiffExecutor = null;
    }
}
