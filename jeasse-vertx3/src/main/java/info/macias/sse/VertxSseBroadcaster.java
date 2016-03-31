package info.macias.sse;

import info.macias.sse.events.MessageEvent;
import io.vertx.core.http.HttpServerRequest;

import java.io.IOException;

/**
 * This class implements a one-to-many connection for broadcasting messages across multiple subscribers.
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class VertxSseBroadcaster extends SseBroadcaster<HttpServerRequest> {

	/**
	 * Adds a subscriber to the broadcaster from a  {@link HttpServerRequest} reference.
	 * @param req The {@link HttpServerRequest} reference, as sent by the subscribers.
	 * @return the {@link VertxSseDispatcher} object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber
     */
	@Override
	public VertxSseDispatcher addSubscriber(HttpServerRequest req) throws IOException {
		VertxSseDispatcher dispatcher = null;
		synchronized (dispatchers) {
			dispatcher = new VertxSseDispatcher(req).ok().open();
			dispatchers.add(dispatcher);
		}
		return dispatcher;
	}

	/**
	 * Adds a subscriber to the broadcaster from a  {@link HttpServerRequest} reference. After the connection has been
	 * successfully established, the broadcaster sends a welcome message exclusively to this subscriber.
	 * @param req The {@link HttpServerRequest} reference, as sent by the subscribers.
	 * @param welcomeMessage The welcome message
	 * @return the {@link VertxSseDispatcher} object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber, or
	 *         if the subscriber immediately closed the connection before receiving the welcome message
	 */
	@Override
	public VertxSseDispatcher addSubscriber(HttpServerRequest req, MessageEvent welcomeMessage) throws IOException {
		VertxSseDispatcher dispatcher = null;
		synchronized (dispatchers) {
			dispatcher = new VertxSseDispatcher(req).ok().open().send(welcomeMessage);
			dispatchers.add(dispatcher);
		}
		return dispatcher;
	}

}
