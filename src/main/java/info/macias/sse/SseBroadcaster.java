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
public class SseBroadcaster {
	private Set<SseDispatcher> dispatchers = Collections.synchronizedSet(new HashSet<SseDispatcher>());

	/**
	 * Adds a subscriber to the broadcaster from a  {@link HttpServletRequest} reference.
	 * @param req The {@link HttpServletRequest} reference, as sent by the subscribers.
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber
     */
	public void addSubscriber(HttpServletRequest req) throws IOException {
		dispatchers.add(new SseDispatcher(req).ok().open());
	}

	/**
	 * Broadcasts a {@link MessageEvent} to all the subscribers, containing only 'event' and 'data' fields.
	 * <p/>
	 * This method relies on the {@link SseDispatcher#send(MessageEvent)} method. If this method throws an
	 * {@link IOException}, the broadcaster assumes the subscriber went offline and silently detaches it
	 * from the collection of subscribers.
	 *
	 * @param event The descriptor of the 'event' field.
	 * @param data The content of the 'data' field.
	 */
	public void broadcast(String event, String data) {
		broadcast(new MessageEvent.Builder()
				.setEvent(event)
				.setData(data)
				.build());
	}

	/**
	 * Broadcasts a {@link MessageEvent} to the subscribers.
	 * <p/>
	 * This method relies on the {@link SseDispatcher#send(MessageEvent)} method. If this method throws an
	 * {@link IOException}, the broadcaster assumes the subscriber went offline and silently detaches it
	 * from the collection of subscribers.
	 *
	 * @param messageEvent The instance that encapsulates all the desired fields for the {@link MessageEvent}
	 */
	public void broadcast(MessageEvent messageEvent) {
		Set<SseDispatcher> disp;
		synchronized (dispatchers) {
			disp = Collections.unmodifiableSet(dispatchers);
		}
		for(SseDispatcher dispatcher : disp) {
			try {
				dispatcher.send(messageEvent);
			} catch (IOException e) {
				// Client disconnected. Removing from dispatchers
				dispatchers.remove(dispatcher);
			}
		}
	}

	/**
	 * Closes all the connections between the broadcaster and the subscribers, and detaches all of them from the
	 * collection of subscribers.
	 */
	public void close() {
		Set<SseDispatcher> disp;
		synchronized (dispatchers) {
			disp = Collections.unmodifiableSet(dispatchers);
			for(SseDispatcher d : disp) {
				try {
					d.close();
				} catch (Exception e) {
					// Uncontrolled exception when closing a dispatcher. Removing anyway and ignoring.
				}
			}
			disp.clear();
		}
	}
}
