package info.macias.sse.subscribe;

import info.macias.sse.EventTarget;

@FunctionalInterface
public interface RemoteCompletionListener<I> {
    void onClose(EventTarget<I> eventTarget);
}
