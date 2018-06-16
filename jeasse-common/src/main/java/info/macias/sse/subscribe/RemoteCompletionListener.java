package info.macias.sse.subscribe;

import info.macias.sse.EventTarget;

@FunctionalInterface
public interface RemoteCompletionListener<T> {
    void onClose(EventTarget<T> eventTarget);
}
