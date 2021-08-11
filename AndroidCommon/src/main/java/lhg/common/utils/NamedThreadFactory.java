package lhg.common.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private final String threadName;

    public NamedThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r, threadName);
    }
}
