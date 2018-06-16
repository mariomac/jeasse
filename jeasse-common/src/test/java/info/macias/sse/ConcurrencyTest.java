/*
Copyright 2016 - Mario Macias Lloret

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package info.macias.sse;

import info.macias.sse.events.MessageEvent;
import info.macias.sse.subscribe.RemoteCompletionListener;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.*;

public class ConcurrencyTest {
    private static final int CONCURRENT_THREADS = 100;
    private static final int REPETITIONS_PER_THREAD = 1000;

    private Executor executor = Executors.newScheduledThreadPool(CONCURRENT_THREADS);

    public ConcurrencyTest() throws FileNotFoundException {
    }

    /**
     * Event target that closes after receiving a given number of events
     */
    private class AutoDisconnectingEventTarget implements EventTarget {
        int receivedEvents = 0;
        int exceptions = 0;
        final int eventsBeforeClosing;

        public AutoDisconnectingEventTarget(int eventsBeforeClosing) {
            this.eventsBeforeClosing = eventsBeforeClosing;
        }

        @Override
        public Object getIdentifier() {
            return null;
        }

        @Override
        public EventTarget onRemoteClose(RemoteCompletionListener listener) {
            return this;
        }

        @Override
        public EventTarget ok() {
            return this;
        }

        @Override
        public EventTarget open() throws IOException {
            return this;
        }

        @Override
        public EventTarget send(String event, String data) throws IOException {
            return this;
        }

        @Override
        public EventTarget send(MessageEvent messageEvent) throws IOException {
            if (receivedEvents++ >= eventsBeforeClosing) {
                exceptions++;
                throw new IOException();
            }
            return this;
        }

        @Override
        public void close() {

        }
    }

    private static final void yield() {
        try {
            Thread.yield();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConcurrentAdditions() throws Exception {
        final List<AutoDisconnectingEventTarget>[] eventTargets = new ArrayList[CONCURRENT_THREADS];
        for (int t = 0; t < CONCURRENT_THREADS; t++) {
            eventTargets[t] = new ArrayList<>(REPETITIONS_PER_THREAD);
        }

        // Given an event broadcaster
        final EventBroadcast eventBroadcast = new EventBroadcast();

        // Which continuously sends messages
        AtomicBoolean finishBroadcasterThread = new AtomicBoolean(false);
        AtomicInteger broadcasts = new AtomicInteger();
        executor.execute(() -> {
            while (!finishBroadcasterThread.get()) {
                broadcasts.incrementAndGet();
                eventBroadcast.broadcast(Mockito.mock(MessageEvent.class));
                yield();
            }
        });

        // And a set of event receivers running in different threads
        AtomicInteger finishedThreads = new AtomicInteger();
        for (int t = 0; t < CONCURRENT_THREADS; t++) {
            final int threadNum = t;
            executor.execute(() -> {
                for (int i = 0; i < REPETITIONS_PER_THREAD; i++) {
                    AutoDisconnectingEventTarget eventTarget = new AutoDisconnectingEventTarget(
                            REPETITIONS_PER_THREAD - threadNum * REPETITIONS_PER_THREAD / CONCURRENT_THREADS);
                    eventTargets[threadNum].add(eventTarget);
                    try {
                        eventBroadcast.addSubscriber(eventTarget);
                        yield();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                finishedThreads.incrementAndGet();
            });
        }

        while (finishedThreads.get() < CONCURRENT_THREADS);

        // Waiting a prudential number of broadcasts in order to be sure all the eventTargets had time to be removed
        while(broadcasts.get() < CONCURRENT_THREADS * REPETITIONS_PER_THREAD);
        finishBroadcasterThread.set(true);

        // Check the broadcaster is empty
        assertFalse(eventBroadcast.hasSubscribers());

        // All the event targets have received exactly the number of events before disconnecting
        for (int i = 0 ; i < CONCURRENT_THREADS ; i++) {
            for (AutoDisconnectingEventTarget eventTarget : eventTargets[i]) {
                assertEquals(eventTarget.eventsBeforeClosing, eventTarget.receivedEvents - eventTarget.exceptions);
                // And at least one disconnection
                assertTrue(eventTarget.exceptions > 0);
            }
        }
    }
}