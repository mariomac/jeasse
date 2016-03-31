package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * This class implements a one-to-many connection for broadcasting messages across multiple subscribers.
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class ServletSseBroadcaster extends SseBroadcaster<HttpServletRequest> {

	/**
	 * Adds a subscriber to the broadcaster from a  {@link HttpServletRequest} reference.
	 * @param req The {@link HttpServletRequest} reference, as sent by the subscribers.
	 * @return the {@link ServletSseDispatcher} object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber
     */
	@Override
	public ServletSseDispatcher addSubscriber(HttpServletRequest req) throws IOException {
		ServletSseDispatcher dispatcher = null;
		synchronized (dispatchers) {
			dispatcher = new ServletSseDispatcher(req).ok().open();
			dispatchers.add(dispatcher);
		}
		return dispatcher;
	}

	/**
	 * Adds a subscriber to the broadcaster from a  {@link HttpServletRequest} reference. After the connection has been
	 * successfully established, the broadcaster sends a welcome message exclusively to this subscriber.
	 * @param req The {@link HttpServletRequest} reference, as sent by the subscribers.
	 * @param welcomeMessage The welcome message
	 * @return the {@link ServletSseDispatcher} object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber, or
	 *         if the subscriber immediately closed the connection before receiving the welcome message
	 */
	@Override
	public ServletSseDispatcher addSubscriber(HttpServletRequest req, MessageEvent welcomeMessage) throws IOException {
		ServletSseDispatcher dispatcher = null;
		synchronized (dispatchers) {
			dispatcher = new ServletSseDispatcher(req).ok().open().send(welcomeMessage);
			dispatchers.add(dispatcher);
		}
		return dispatcher;
	}

}
