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

package info.macias.sse.servlet3;

import info.macias.sse.EventTarget;
import info.macias.sse.subscribe.IdMapper;
import info.macias.sse.subscribe.RemoteCompletionListener;
import info.macias.sse.err.ClosedConnectionException;
import info.macias.sse.events.MessageEvent;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * SSE dispatcher for one-to-one connections from Server to client-side subscriber
 *
 * @param <I> Type of the identifier that distinguishes one remote subscriber from another
 *           (e.g. a session ID, a user ID...)
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class ServletEventTarget<I> implements EventTarget<AsyncContext, I> {

    private boolean completed = false;
    private RemoteCompletionListener<I> completionListener;
    private IdMapper<AsyncContext, I> idMapper;
    private final AsyncContext asyncContext;

    /**
     * Builds a new dispatcher from an {@link HttpServletRequest} object.
     * @param request The {@link HttpServletRequest} reference, as sent by the subscriber.
     * @deprecated Use {@link #create(HttpServletRequest)}
     */
    public ServletEventTarget(HttpServletRequest request) {
        asyncContext = request.startAsync();
        asyncContext.setTimeout(0);
        asyncContext.addListener(new AsyncListenerImpl());
    }

    private ServletEventTarget(AsyncContext asyncContext) {
        this.asyncContext = asyncContext;
        asyncContext.setTimeout(0); // TODO: allow setting timeout
        asyncContext.addListener(new AsyncListenerImpl());
    }

    /**
     * If the connection is accepted, the server sends the 200 (OK) status message, plus the next HTTP headers:
     * <pre>
     *     Content-type: text/event-stream;charset=utf-8
     *     Cache-Control: no-cache
     *     Connection: keep-alive
     * </pre>
     * @return The same {@link ServletEventTarget} object that received the method call
     */
	@Override
    public ServletEventTarget<I> ok() {
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
     * @return The same {@link ServletEventTarget} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public ServletEventTarget<I> open() throws IOException {
        assertConnectionStatus();

        HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print("event: open\n\n");
        response.getOutputStream().flush();

        return this;
    }

    /**
     * Sends a {@link MessageEvent} to the subscriber, containing only 'event' and 'data' fields.
     * @param event The descriptor of the 'event' field.
     * @param data The content of the 'data' field.
     * @return The same {@link ServletEventTarget} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public ServletEventTarget<I> send(String event, String data) throws IOException {
        assertConnectionStatus();
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
     * @return The same {@link ServletEventTarget} object that received the method call
     * @throws IOException if there was an error writing into the response's {@link java.io.OutputStream}. This may be
     * a common exception: e.g. it will be thrown when the SSE subscriber closes the connection
     */
	@Override
    public ServletEventTarget<I> send(MessageEvent messageEvent) throws IOException {
	    assertConnectionStatus();
		HttpServletResponse response = (HttpServletResponse)asyncContext.getResponse();
        response.getOutputStream().print(messageEvent.toString());
		response.getOutputStream().flush();
        return this;
    }

    private void assertConnectionStatus() throws ClosedConnectionException {
	    if (completed) {
	        throw new ClosedConnectionException();
        }
    }

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

    @Override
    public EventTarget<AsyncContext, I> onRemoteClose(RemoteCompletionListener<I> listener) {
	    this.completionListener = listener;
        return this;
    }

    @Override
    public EventTarget<AsyncContext, I> withMapper(IdMapper<AsyncContext, I> mapper) {
	    this.idMapper = mapper;
        return null;
    }

    private class AsyncListenerImpl implements AsyncListener {
	    private void complete() {
	        completed = true;
        }
        @Override
        public void onComplete(AsyncEvent event) {
            complete();
        }

        @Override
        public void onTimeout(AsyncEvent event) {
            complete();
        }

        @Override
        public void onError(AsyncEvent event) {
            complete();
        }

        @Override
        public void onStartAsync(AsyncEvent event) {
        }
    }

    public static ServletEventTarget create(HttpServletRequest request) {
        AsyncContext asyncContext = request.startAsync();
        return new ServletEventTarget(asyncContext);
    }
}
