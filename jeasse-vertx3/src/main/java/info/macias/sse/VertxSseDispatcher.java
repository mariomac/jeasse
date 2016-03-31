package info.macias.sse;

import info.macias.sse.events.MessageEvent;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;

/**
 * SSE dispatcher for one-to-one connections from Server to client-side subscriber
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class VertxSseDispatcher implements SseDispatcher {

	private HttpServerRequest request;

    /**
     * Builds a new dispatcher from an {@link HttpServerRequest} object.
     * @param request The {@link HttpServerRequest} reference, as sent by the subscriber.
     */
    public VertxSseDispatcher(HttpServerRequest request) {
		this.request = request;
    }

    /**
     * If the connection is accepted, the server sends the 200 (OK) status message, plus the next HTTP headers:
     * <pre>
     *     Content-type: text/event-stream;charset=utf-8
     *     Cache-Control: no-cache
     *     Connection: keep-alive
     * </pre>
     * @return The same {@link VertxSseDispatcher} object that received the method call
     */
	@Override
    public VertxSseDispatcher ok() {
		request.response().headers().add("Content-Type", "text/event-stream");
		request.response().headers().add("Cache-Control", "no-cache");
		request.response().headers().add("Connection", "keep-alive");
		request.response().setStatusCode(200);
		request.response().setChunked(true);
        return this;
    }

    /**
     * Responds to the client-side subscriber that the connection has been open
     *
     * @return The same {@link VertxSseDispatcher} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public VertxSseDispatcher open() throws IOException {
		System.out.println("request.version() = " + request.version());
		System.out.println("Sending event open");
		request.response().write("event: open\n\n");
		return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber, containing only 'event' and 'data' fields.
     * @param event The descriptor of the 'event' field.
     * @param data The content of the 'data' field.
     * @return The same {@link VertxSseDispatcher} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public VertxSseDispatcher send(String event, String data) throws IOException {
		request.response().write(
                new MessageEvent.Builder()
                    .setData(data)
                    .setEvent(event)
                    .build()
                    .toString()
        );
        return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber
     * @param messageEvent The instance that encapsulates all the desired fields for the {@link MessageEvent}
     * @return The same {@link VertxSseDispatcher} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public VertxSseDispatcher send(MessageEvent messageEvent) throws IOException {
		request.response().write(messageEvent.toString());

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
			request.response().close();
			request.response().end();
        }
    }
}
