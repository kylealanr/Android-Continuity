package com.bottlerocketstudios.continuity;

import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created on 8/22/16.
 */
public class ContinuityRepository {
    private final String TAG = ContinuityRepository.class.getSimpleName() + " " + this.hashCode();

    public static final long DEFAULT_CHECK_INTERVAL_MS = 500;
    public static final long DEFAULT_IDLE_SHUTDOWN_MS = TimeUnit.SECONDS.toMillis(30);
    public static final long DEFAULT_LIFETIME_MS = TimeUnit.SECONDS.toMillis(2);
    public static final int DEFAULT_MAX_EMPTY_ITERATIONS = 10;

    private final long mCheckIntervalMs;
    private final long mIdleShutdownMs;
    private final long mDefaultLifetimeMs;
    private final int mMaxEmptyIterations;

    private final String mThreadLock = "";

    private final WeakHashMap<Object, ContinuityId> mAnchoredContinuityIdMap = new WeakHashMap<>();
    private final Map<ContinuityId, ContinuityContainer> mHeterogenousCache = Collections.synchronizedMap(new TreeMap<ContinuityId, ContinuityContainer>());
    private final List<ContinuityId> mDeletionCandidates = new ArrayList<>(50);

    private CleanupThread mThread;

    /**
     * Create a ContinuityRepository using the default values
     */
    public ContinuityRepository() {
        this(DEFAULT_CHECK_INTERVAL_MS, DEFAULT_IDLE_SHUTDOWN_MS, DEFAULT_LIFETIME_MS, DEFAULT_MAX_EMPTY_ITERATIONS);
    }

    /**
     * Create a ContinuityRepository with the provided settings.
     *
     * Several parameters can be adjusted to change how quickly cached items are released and how long
     * the watchdog thread that cleans the items will run while idle or empty.
     *
     * @param checkIntervalMs       Time in milliseconds between checks to cleanup unused references.
     * @param idleShutdownMs        Time in milliseconds before inactivity will release the Thread.
     * @param defaultLifetimeMs     Default time in milliseconds to keep an un-anchored reference around.
     * @param maxEmptyIterations    Number of checks to perform with an empty collection before releasing the Thread.
     */
    public ContinuityRepository(long checkIntervalMs, long idleShutdownMs, long defaultLifetimeMs, int maxEmptyIterations) {
        mCheckIntervalMs = checkIntervalMs;
        mIdleShutdownMs = idleShutdownMs;
        mDefaultLifetimeMs = defaultLifetimeMs;
        mMaxEmptyIterations = maxEmptyIterations;
    }

    /**
     * Set from {@link Log#VERBOSE} to {@link Log#ERROR} to prevent logging anything below that level of importance.
     */
    public void setMinLoggingLevel(int loggingLevel) {
        ContinuityLog.setMinLoggingLevel(loggingLevel);
    }

    public <T> ContinuityBuilder<T> with(Object anchor, Class<T> continuousClass) {
        return new ContinuityBuilder<>(this, anchor, continuousClass, mDefaultLifetimeMs);
    }

    @SuppressWarnings("unchecked")
    <T> T get(Object anchor, ContinuityId continuityId, long lifetimeMs) {
        ContinuityContainer continuityContainer = mHeterogenousCache.get(continuityId);
        if (continuityContainer != null) {
            mAnchoredContinuityIdMap.put(anchor, continuityId);
            continuityContainer.updateLifetimeMs(lifetimeMs);
            continuityContainer.setExpirationMs(0);
            touchCleanupThread();
            return (T) continuityContainer.getObject();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    <T> void put(ContinuityId continuityId, Object anchor, T instance, long lifetimeMs) {
        mAnchoredContinuityIdMap.put(anchor, continuityId);
        mHeterogenousCache.put(continuityId, new ContinuityContainer(instance, lifetimeMs));
        touchCleanupThread();
    }

    private void performCleanup(CleanupThread cleanupThread) {
        //Check for error state of more than the known thread calling us.
        if (cleanupThread != mThread) {
            ContinuityLog.w(TAG, "Two threads were started");
            cleanupThread.stopRunning();
            return;
        }

        //Get the set of items that are in the cache which do not have anchors.
        //Reuse deletion candidate List to reduce GC churn on each iteration.
        mDeletionCandidates.clear();
        mDeletionCandidates.addAll(mHeterogenousCache.keySet());
        mDeletionCandidates.removeAll(mAnchoredContinuityIdMap.values());

        //Walk the set of unanchored values and either set their expiration time or delete expired items.
        //Use for instead of foreach to reduce GC churn on iterator creation/destruction.
        for (int i = 0; i < mDeletionCandidates.size(); i++) {
            ContinuityId continuityId = mDeletionCandidates.get(i);
            ContinuityContainer continuityContainer = mHeterogenousCache.get(continuityId);
            if (continuityContainer.getExpirationMs() > 0) {
                if (continuityContainer.getExpirationMs() < SystemClock.uptimeMillis()) {
                    //Past deadline, delete it
                    mHeterogenousCache.remove(continuityId);
                    Object retainedObject = continuityContainer.getObject();
                    if (retainedObject instanceof ContinuousObject) {
                        ((ContinuousObject) retainedObject).onContinuityDiscard();
                    }
                }
            } else {
                //Set expiration deadline.
                continuityContainer.setExpirationMs(SystemClock.uptimeMillis() + continuityContainer.getLifetimeMs());
            }
        }
    }

    boolean isEmpty() {
        return mHeterogenousCache.isEmpty();
    }

    boolean isRunning() {
        return (mThread != null && mThread.isRunning());
    }

    private void touchCleanupThread() {
        if (mThread == null) {
            ContinuityLog.v(TAG, "Cleanup thread was missing");
            //Don't synchronize unless the thread is null.
            synchronized (mThreadLock) {
                //We block this section to ensure that only one instance is created.
                //Check that another thread didn't already create it while waiting on synchronize block.
                if (mThread == null) {
                    mThread = createThread();
                }
            }
        }
        mThread.touch();
    }

    private CleanupThread createThread() {
        ContinuityLog.v(TAG, "Creating new cleanup thread");
        CleanupThread thread = new CleanupThread();
        thread.start();
        return thread;
    }

    private void onThreadShutdown(CleanupThread cleanupThread) {
        if (mThread == cleanupThread) {
            ContinuityLog.v(TAG, "Removing thread reference");
            mThread = null;
        } else {
            ContinuityLog.w(TAG, "An unexpected thread was shutdown");
        }
    }

    public void remove(ContinuityId continuityId) {
        mHeterogenousCache.remove(continuityId);
    }

    private class CleanupThread extends Thread {
        private boolean mRunning;
        private long mLastTouchTimestampMs;
        private int mEmptyCount = 0;

        @Override
        public synchronized void start() {
            super.start();
            mRunning = true;
        }

        @Override
        public void run() {
            ContinuityLog.v(TAG, "Running thread");
            while (mRunning) {
                synchronized (this) {
                    try {
                        wait(mCheckIntervalMs);
                        performCleanup(this);

                        if (SystemClock.uptimeMillis() - mLastTouchTimestampMs > mIdleShutdownMs) {
                            ContinuityLog.v(TAG, "Repository was idle too long.");
                            stopRunning();
                        }

                        if (isEmpty()) {
                            mEmptyCount++;
                            ContinuityLog.v(TAG, "Cache is empty.");
                            if (mEmptyCount > mMaxEmptyIterations) {
                                stopRunning();
                            }
                        } else {
                            mEmptyCount = 0;
                        }
                    } catch (InterruptedException e) {
                        ContinuityLog.e(TAG, "Caught java.lang.InterruptedException", e);
                    }
                }
            }
            ContinuityLog.v(TAG, "Shutting down thread");
            onThreadShutdown(this);
        }

        public void touch() {
            mLastTouchTimestampMs = SystemClock.uptimeMillis();
            if (!mRunning) start();
        }

        public void stopRunning() {
            mRunning = false;
        }

        public boolean isRunning() {
            return mRunning;
        }
    }

}
