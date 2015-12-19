package info.macias.sse.events;

/**
 * @author Mario Macías (http://github.com/mariomac)
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

    public final String getData() {
        return data;
    }

    public final String getEvent() {
        return event;
    }

    public Integer getRetry() {
        return retry;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return toStringCache;
    }

    public static class Builder {
        private String data = null;
        private String event = null;
        private Integer retry = null;
        private String id = null;

        public Builder setData(String data) {
            this.data = data;
            return this;
        }

        public Builder setEvent(String event) {
            this.event = event;
            return this;
        }

        public Builder setRetry(int retry) {
            this.retry = retry;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

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
