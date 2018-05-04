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
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LastEventIdTest {

    public LastEventIdTest() {
    }

    /**
     * Event target that closes after receiving a given number of events
     */
    private class CustomEventTarget implements EventTarget {
        String lastEventId = null;
        boolean willThrow = false;

        public CustomEventTarget() {
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
            if (willThrow) {
                throw new IOException();
            }
            lastEventId = messageEvent.getId();
            return this;
        }

        @Override
        public void close() {

        }
    }

    @Test
    public void testLastEventId() throws Exception {
        // Get an event broadcaster
        final EventBroadcast eventBroadcast = new EventBroadcast();
        
        // and our custom target
        CustomEventTarget eventTarget = new CustomEventTarget();
        eventBroadcast.addSubscriber(eventTarget);

        // send event #1
        eventBroadcast.broadcast(new MessageEvent.Builder().setEvent("test").setData("foo").setId("1").build());
        assertTrue(eventTarget.lastEventId.equals("1"));

        // fail to send event #2
        eventTarget.willThrow = true;
        eventBroadcast.broadcast(new MessageEvent.Builder().setEvent("test").setData("foo").setId("2").build());
        assertTrue(eventTarget.lastEventId.equals("1"));

        // send event #3
        eventBroadcast.broadcast(new MessageEvent.Builder().setEvent("test").setData("foo").setId("3").build());

        // subscribe again, using the last-event-id field
        eventTarget.willThrow = false;
        eventBroadcast.addSubscriber(eventTarget, "1");

        // then check events #2 and #3 have been sent again
        assertTrue(eventTarget.lastEventId.equals("3"));
    }
}
