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

package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class implements a one-to-many connection for broadcasting messages across multiple subscribers.
 *
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class EventBroadcast {

	protected Set<EventTarget> targets = Collections.synchronizedSet(new HashSet<>());

	/**
	 * <p>Adds a subscriber from a <code>connectionRequest</code> that contains the information to allow sending back
	 * information to the subsbriber (e.g. an <code>HttpServletRequest</code> for servlets or <code>HttpServerRequest</code>
	 * for VertX)</p>
	 *
	 * @param eventTarget an event target to be subscribed to the broadcast messages
	 *
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber
	 */
	public void addSubscriber(EventTarget eventTarget) throws IOException {
		synchronized (targets) {
			targets.add(eventTarget.ok().open());
		}
	}

	/**
	 * <p>Adds a subscriber to the broadcaster from a <code>connectionRequest</code> reference that contains the information to allow sending back
	 * information to the subsbriber (e.g. an <code>HttpServletRequest</code> for servlets or <code>HttpServerRequest</code>
	 * for VertX).</p>
	 *
	 *
	 *
	 * @param eventTarget an event target to be subscribed to the broadcast messages
	 * @param welcomeMessage The welcome message
	 * @return the {@link EventTarget} implementing object that will be used to communicate with the recently created subscriber
	 * @throws IOException if there was an error during the acknowledge process between broadcaster and subscriber, or
	 *         if the subscriber immediately closed the connection before receiving the welcome message
	 */
	public void addSubscriber(EventTarget eventTarget, MessageEvent welcomeMessage) throws IOException {
		synchronized (targets) {
			targets.add(eventTarget.ok().open().send(welcomeMessage));
		}
	}

	/**
	 * <p>Broadcasts a {@link MessageEvent} to all the subscribers, containing only 'event' and 'data' fields.</p>
	 *
	 * <p>This method relies on the {@link EventTarget#send(MessageEvent)} method. If this method throws an
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
	 * <p>This method relies on the {@link EventTarget#send(MessageEvent)} method. If this method throws an
	 * {@link IOException}, the broadcaster assumes the subscriber went offline and silently detaches it
	 * from the collection of subscribers.</p>
	 *
	 * @param messageEvent The instance that encapsulates all the desired fields for the {@link MessageEvent}
	 */
	public void broadcast(MessageEvent messageEvent) {
		EventTarget[] disp;
		synchronized (targets) {
			disp = targets.toArray(new EventTarget[targets.size()]);
		}
		for(EventTarget dispatcher : disp) {
			try {
				dispatcher.send(messageEvent);
			} catch (IOException e) {
				// Client disconnected. Removing from targets
				targets.remove(dispatcher);
			}
		}
	}

	/**
	 * Closes all the connections between the broadcaster and the subscribers, and detaches all of them from the
	 * collection of subscribers.
	 */
	public void close() {
		synchronized (targets) {
			for(EventTarget d : targets) {
				try {
					d.close();
				} catch (Exception e) {
					// Uncontrolled exception when closing a dispatcher. Removing anyway and ignoring.
				}
			}
			targets.clear();
		}
	}
}

