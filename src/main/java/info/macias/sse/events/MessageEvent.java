package info.macias.sse.events;

/**
 * This class encapsulates a SSE Message Event. It may specify the next optional fields:
 *
 * <ul>
 *     <li>event: an arbitrary name describing an event type (e.g. "message", "error", "newEvent", etc...)</li>
 *     <li>data: string data to be transmitted with the event</li>
 *     <li>id: a string that provides an unique identifier for this event</li>
 *     <li>retry: a number representing the timeout (in milliseconds) that the client must wait before
 *         reconnecting again to the server, after the event has been received</li>
 * </ul>
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class MessageEvent {
    private final String data;
    private final String event;
    private final Integer retry;
    private final String id;

    private final String toStringCache;

    private MessageEvent(String event, String data, Integer retry, String id, String toStringCache) {
        this.data = data;
        this.event = event;
        this.toStringCache = toStringCache;
        this.retry = retry;
        this.id = id;

    }

    /**
     * Returns the 'data' field: string data to be transmitted with the event
     * @return the value of the 'data' field
     */
    public final String getData() {
        return data;
    }

    /**
     * Returns the 'event' field: an arbitrary name describing an event type (e.g. "message", "error", "newEvent", etc...)
     * @return the value of the 'event' field
     */
    public final String getEvent() {
        return event;
    }

    /**
     * Returns the 'retry' field: a number representing the timeout (in milliseconds) that the client must wait before
     *         reconnecting again to the server, after the event has been received
     * @return the value of the 'retry' field
     */
    public Integer getRetry() {
        return retry;
    }

    /**
     * Returns the 'id' field: a string that provides an unique identifier for this event
     * @return the 'id' field
     */
    public String getId() {
        return id;
    }

    /**
     * Converts the MessageEvent to a String in the format to be transmitted to the listener clients. E.g.:
     * <pre>
     *     event: message
     *     id: ea23-234a-f334-c899
     *     retry: 10000
     *     data: This is a multi-line
     *     data: text that must be transmitted
     * </pre>
     * @return the string representation of the MessageEvent. Ready to be transmitted.
     */
    public String toString() {
        return toStringCache;
    }

    /**
     * Helper class used to build a {@link MessageEvent} instance.
     */
    public static class Builder {
        private String data = null;
        private String event = null;
        private Integer retry = null;
        private String id = null;

        /**
         * Sets the information of the 'data' field: string data to be transmitted with the event
         * @param data string data to be transmitted with the event
         * @return The same target instance where the method has been invoked on.
         */
        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        /**
         * Sets the information of the 'event' field: an arbitrary name describing an event type (e.g. "message", "error", "newEvent", etc...)
         * @param event an arbitrary name describing an event type
         * @return The same target instance where the method has been invoked on.
         */
        public Builder setEvent(String event) {
            this.event = event;
            return this;
        }

        /**
         * Sets the information of the 'retry' field: a number representing the timeout (in milliseconds) that the client
         * must wait before reconnecting again to the server, after the event has been received
         * @param retry a number representing the timeout (in milliseconds) that the client
         * must wait before reconnecting again to the server
         * @return The same target instance where the method has been invoked on.
         */
        public Builder setRetry(int retry) {
            this.retry = retry;
            return this;
        }

        /**
         * Sets the information of the 'id' field: a string that provides an unique identifier for this event
         * @param id A string that provides an unique identifier for this event string data to be transmitted with the event
         * @return The same target instance where the method has been invoked on.
         */
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Instantiates a {@link MessageEvent} object with the same attributes as the Builder object.
         * @return the {@link MessageEvent} instance
         */
        public MessageEvent build() {
            StringBuilder sb = new StringBuilder("event: ").append(event.replace("\n","")).append('\n');
            if(data != null) {
                for(String s : data.split("\n")) {
                    sb.append("data: ").append(s).append('\n');
                }
            }
            if(retry != null) {
                sb.append("retry: ").append(retry).append('\n');
            }
            if(id != null) {
                sb.append("id: ").append(id.replace("\n","")).append('\n');
            }

            // an empty line dispatches the event
            sb.append('\n');
            return new MessageEvent(event,data,retry,id,sb.toString());
        }
    }
}
