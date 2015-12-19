package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
public class MessageDispatcher {
    private final HttpServletResponse response;

    public MessageDispatcher(HttpServletResponse response) {
        this.response = response;
    }

    public MessageDispatcher ok() {
        response.setStatus(200);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        return this;
    }

    public MessageDispatcher open() throws IOException {
        response.getOutputStream().println("event: open");
        response.getOutputStream().flush();

        return this;
    }

    public MessageDispatcher send(String event, String data) throws IOException {
        response.getOutputStream().println(
                new MessageEvent.Builder()
                    .setData(data)
                    .setEvent(event)
                    .build()
                    .toString()
        );
        response.getOutputStream().flush();

        return this;
    }

    public MessageDispatcher send(MessageEvent me) throws IOException {
        response.getOutputStream().println(me.toString());
        return this;
    }




}
