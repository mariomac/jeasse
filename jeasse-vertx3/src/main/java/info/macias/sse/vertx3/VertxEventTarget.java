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

package info.macias.sse.vertx3;

import info.macias.sse.EventTarget;
import info.macias.sse.events.MessageEvent;
import io.vertx.core.http.HttpServerRequest;

import java.io.IOException;

/**
 * SSE dispatcher for one-to-one connections from Server to client-side subscriber
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class VertxEventTarget implements EventTarget {

	private HttpServerRequest request;

    /**
     * Builds a new dispatcher from an {@link HttpServerRequest} object.
     * @param request The {@link HttpServerRequest} reference, as sent by the subscriber.
     */
    public VertxEventTarget(HttpServerRequest request) {
		this.request = request;
    }

    /**
     * If the connection is accepted, the server sends the 200 (OK) status message, plus the next HTTP headers:
     * <pre>
     *     Content-type: text/event-stream;charset=utf-8
     *     Cache-Control: no-cache
     *     Connection: keep-alive
     * </pre>
     * @return The same {@link VertxEventTarget} object that received the method call
     */
	@Override
    public VertxEventTarget ok() {
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
     * @return The same {@link VertxEventTarget} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public VertxEventTarget open() throws IOException {
		System.out.println("request.version() = " + request.version());
		System.out.println("Sending event open");
		request.response().write("event: open\n\n");
		return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber, containing only 'event' and 'data' fields.
     * @param event The descriptor of the 'event' field.
     * @param data The content of the 'data' field.
     * @return The same {@link VertxEventTarget} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public VertxEventTarget send(String event, String data) throws IOException {
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
     * @return The same {@link VertxEventTarget} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public VertxEventTarget send(MessageEvent messageEvent) throws IOException {
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
