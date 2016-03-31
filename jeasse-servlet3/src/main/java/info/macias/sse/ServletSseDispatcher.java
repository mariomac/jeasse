package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * SSE dispatcher for one-to-one connections from Server to client-side subscriber
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class ServletSseDispatcher implements SseDispatcher {

	private final AsyncContext asyncContext;

    /**
     * Builds a new dispatcher from an {@link HttpServletRequest} object.
     * @param request The {@link HttpServletRequest} reference, as sent by the subscriber.
     */
    public ServletSseDispatcher(HttpServletRequest request) {
        asyncContext = request.startAsync();
        asyncContext.setTimeout(0);
        asyncContext.addListener(new AsyncListenerImpl());
    }

    /**
     * If the connection is accepted, the server sends the 200 (OK) status message, plus the next HTTP headers:
     * <pre>
     *     Content-type: text/event-stream;charset=utf-8
     *     Cache-Control: no-cache
     *     Connection: keep-alive
     * </pre>
     * @return The same {@link ServletSseDispatcher} object that received the method call
     */
	@Override
    public ServletSseDispatcher ok() {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.setStatus(200);
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Connection","keep-alive");
        return this;
    }

    /**
     * Responds to the client-side subscriber that the connection has been open
     *
     * @return The same {@link ServletSseDispatcher} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public ServletSseDispatcher open() throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print("event: open\n\n");
        response.getOutputStream().flush();

        return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber, containing only 'event' and 'data' fields.
     * @param event The descriptor of the 'event' field.
     * @param data The content of the 'data' field.
     * @return The same {@link ServletSseDispatcher} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public ServletSseDispatcher send(String event, String data) throws IOException {
        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print(
                new MessageEvent.Builder()
                    .setData(data)
                    .setEvent(event)
                    .build()
                    .toString()
        );
        response.getOutputStream().flush();
        return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber
     * @param messageEvent The instance that encapsulates all the desired fields for the {@link MessageEvent}
     * @return The same {@link ServletSseDispatcher} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public ServletSseDispatcher send(MessageEvent messageEvent) throws IOException {
		HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print(messageEvent.toString());
		response.getOutputStream().flush();
        return this;
    }

    private boolean completed = false;

    /**
     * Closes the connection between the server and the client.
     */
	@Override
    public void close() {
        if(!completed) {
            completed = true;
            asyncContext.complete();
        }
    }

    private class AsyncListenerImpl implements AsyncListener {
        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            completed = true;
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }
}
