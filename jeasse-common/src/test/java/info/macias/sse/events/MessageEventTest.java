package info.macias.sse.events;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessageEventTest {

    @Test
    public void testDataBuilder() {
        MessageEvent ev = new MessageEvent.Builder()
            .setData("1 2 3 4")
            .build();

        assertEquals("1 2 3 4", ev.getData());
        assertNull(ev.getEvent());
        assertNull(ev.getId());
        assertNull(ev.getRetry());
        assertEquals("data: 1 2 3 4\n\n", ev.toString());
    }

    @Test
    public void testMultilineDataBuilder() {
        MessageEvent ev = new MessageEvent.Builder()
            .setData("1 2 3 4\n5 6 7 8\n\n9 10 11 12")
            .build();

        assertEquals("1 2 3 4\n5 6 7 8\n\n9 10 11 12", ev.getData());
        assertNull(ev.getEvent());
        assertNull(ev.getId());
        assertNull(ev.getRetry());
        assertEquals("data: 1 2 3 4\n"
                     + "data: 5 6 7 8\n"
                     + "data: \n"
                     + "data: 9 10 11 12\n\n", ev.toString());
    }

    @Test
    public void testEventBuilder() {
        MessageEvent ev = new MessageEvent.Builder()
            .setEvent("1 2\n 3 4")
            .build();

        // line breaks are not removed from original event data
        assertEquals("1 2\n 3 4", ev.getEvent());
        assertNull(ev.getData());
        assertNull(ev.getId());
        assertNull(ev.getRetry());
        // line breaks are removed in toString
        assertEquals("event: 1 2 3 4\n\n", ev.toString());
    }

    @Test
    public void testIdBuilder() {
        MessageEvent ev = new MessageEvent.Builder()
            .setId("1\n 2 3 4")
            .build();

        assertEquals("1\n 2 3 4", ev.getId());
        assertNull(ev.getEvent());
        assertNull(ev.getData());
        assertNull(ev.getRetry());
        assertEquals("id: 1 2 3 4\n\n", ev.toString());
    }

    @Test
    public void testRetryBuilder() {
        MessageEvent ev = new MessageEvent.Builder()
            .setRetry(1234)
            .build();

        assertEquals(1234, (int) ev.getRetry());
        assertNull(ev.getEvent());
        assertNull(ev.getId());
        assertNull(ev.getData());
        assertEquals("retry: 1234\n\n", ev.toString());
    }

}
