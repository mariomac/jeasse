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
import info.macias.sse.subscribe.IdMapper;
import info.macias.sse.subscribe.RemoteCompletionListener;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

/**
 * SSE dispatcher for one-to-one connections from Server to client-side subscriber
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class VertxEventTarget<I> implements EventTarget<I> {


    private HttpServerRequest request;
    private RemoteCompletionListener<I> closeListener;
    private I identifier;

    /**
     * Builds a new dispatcher from an {@link HttpServerRequest} object.
     *
     * @param request The {@link HttpServerRequest} reference, as sent by the subscriber.
     * @deprecated TODO put constructor
     */
    @Deprecated
    public VertxEventTarget(HttpServerRequest request) {
        this.request = request;
        ExceptionHandler eh = new ExceptionHandler();
        request.exceptionHandler(eh);
        request.response().exceptionHandler(eh).closeHandler(new CloseHandler());
    }

    private VertxEventTarget(HttpServerRequest request, IdMapper<HttpServerRequest, I> mapper) {
        this.request = request;
        request.exceptionHandler(new ExceptionHandler());
        this.identifier = mapper.map(request);
    }

    /**
     * If the connection is accepted, the server sends the 200 (OK) status message, plus the next HTTP headers:
     * <pre>
     *     Content-type: text/event-stream;charset=utf-8
     *     Cache-Control: no-cache
     *     Connection: keep-alive
     * </pre>
     *
     * @return The same {@link VertxEventTarget} object that received the method call
     */
    @Override
    public VertxEventTarget<I> ok() {
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
     */
    @Override
    public VertxEventTarget<I> open() {
        request.response().write("event: open\n\n");
        return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber, containing only 'event' and 'data' fields.
     *
     * @param event The descriptor of the 'event' field.
     * @param data  The content of the 'data' field.
     * @return The same {@link VertxEventTarget} object that received the method call
     */
    @Override
    public VertxEventTarget<I> send(String event, String data) {
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
     *
     * @param messageEvent The instance that encapsulates all the desired fields for the {@link MessageEvent}
     * @return The same {@link VertxEventTarget} object that received the method call
     */
    @Override
    public VertxEventTarget<I> send(MessageEvent messageEvent) {
        request.response().write(messageEvent.toString());
        return this;
    }

    @Override
    public I getIdentifier() {
        return identifier;
    }

    @Override
    public EventTarget<I> onRemoteClose(RemoteCompletionListener<I> listener) {
        closeListener = listener;
        return this;
    }

    private boolean completed = false;

    /**
     * Closes the connection between the server and the client.
     */
    @Override
    public void close() {
        System.out.println("Invoking close for " + identifier);
        if (!completed) {
            completed = true;
            request.response().close();
            request.response().end();
            if (closeListener != null) {
                closeListener.onClose(this);
            }
        }
    }

    private class CloseHandler implements Handler<Void> {
        @Override
        public void handle(Void event) {
            close();
        }
    }
    private class ExceptionHandler implements Handler<Throwable> {
        @Override
        public void handle(Throwable event) {
            close();
        }
    }

    public static VertxEventTarget<HttpServerRequest> create(HttpServerRequest request) {
        return create(request, IdMapper::identity);
    }

    public static <IdT> VertxEventTarget<IdT> create(HttpServerRequest request, IdMapper<HttpServerRequest, IdT> mapper) {
        return new VertxEventTarget<>(request, mapper);
    }
}