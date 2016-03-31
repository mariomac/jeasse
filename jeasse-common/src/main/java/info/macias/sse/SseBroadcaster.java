package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a one-to-many connection for broadcasting messages across multiple subscribers.
 *
 * @param <ConnectionClass> class that contains the information to allow sending back
 * information to the subsbriber (e.g. an <code>HttpServletRequest</code> for servlets or <code>HttpServerRequest</code>
 * for VertX)
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public abstract class SseBroadcaster<ConnectionClass> {

	protected Set<SseDispatcher> dispatchers = Collections.synchronizedSet(new HashSet<>());

	/**
	 * Adds a subscriber from a <code>connectionRequest</code> that contains the information to allow sending back
	 * information to the subsbriber (e.g. an <code>HttpServletRequest</code> for servlets or <code>HttpServerRequest</code>
	 * for VertX)<p/>
	 *
	 * The implementors of this class, must call the {@link SseDispatcher#ok()} and then the {@link SseDispatcher#open()} method
	 *
	 * @param connectionRequest a connection request, as sent by the subscribers
	 * for VertX)
	 * @return the {@link SseDispatcher} implementing object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber
	 */
	public abstract SseDispatcher addSubscriber(ConnectionClass connectionRequest) throws IOException;

	/**
	 * Adds a subscriber to the broadcaster from a <code>connectionRequest</code> reference that contains the information to allow sending back
	 * information to the subsbriber (e.g. an <code>HttpServletRequest</code> for servlets or <code>HttpServerRequest</code>
	 * for VertX).<p/>
	 *
	 * The implementors of this class, must call the {@link SseDispatcher#ok()} and then the {@link SseDispatcher#open()} method.
	 * After the connection has been
	 * successfully established, the broadcaster sends a welcome message exclusively to this subscriber.
	 *
	 *
	 * @param connectionRequest The connection request reference, as sent by the subscribers.
	 * @param welcomeMessage The welcome message
	 * @return the {@link SseDispatcher} implementing object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber, or
	 *         if the subscriber immediately closed the connection before receiving the welcome message
	 */
	public abstract SseDispatcher addSubscriber(ConnectionClass connectionRequest, MessageEvent welcomeMessage) throws IOException;

	/**
	 * <p>Broadcasts a {@link MessageEvent} to all the subscribers, containing only 'event' and 'data' fields.</p>
	 *
	 * <p>This method relies on the {@link SseDispatcher#send(MessageEvent)} method. If this method throws an
	 * {@link IOException}, the broadcaster assumes the subscriber went offline and silently detaches it
	 * from the collection of subscribers.</p>
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
	 * <p>Broadcasts a {@link MessageEvent} to the subscribers.</p>
	 *
	 * <p>This method relies on the {@link SseDispatcher#send(MessageEvent)} method. If this method throws an
	 * {@link IOException}, the broadcaster assumes the subscriber went offline and silently detaches it
	 * from the collection of subscribers.</p>
	 *
	 * @param messageEvent The instance that encapsulates all the desired fields for the {@link MessageEvent}
	 */
	public void broadcast(MessageEvent messageEvent) {
		SseDispatcher[] disp;
		synchronized (dispatchers) {
			disp = dispatchers.toArray(new SseDispatcher[dispatchers.size()]);
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
		SseDispatcher[] disp;
		synchronized (dispatchers) {
			disp = dispatchers.toArray(new SseDispatcher[dispatchers.size()]);

			for(SseDispatcher d : disp) {
				try {
					d.close();
				} catch (Exception e) {
					// Uncontrolled exception when closing a dispatcher. Removing anyway and ignoring.
				}
			}
			dispatchers.clear();
		}
	}
}

