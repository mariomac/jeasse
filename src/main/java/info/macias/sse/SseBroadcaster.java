package info.macias.sse;

import info.macias.sse.events.MessageEvent;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author <a href="http://github.com/mariomac">Mario Macías</a>
 */
public class SseBroadcaster {
	private Set<SseDispatcher> dispatchers = new HashSet<>();

	public void addListener(HttpServletRequest req) throws IOException {
		dispatchers.add(new SseDispatcher(req).ok().open());
	}

	public void broadcast(String event, String data) {
		broadcast(new MessageEvent.Builder()
				.setEvent(event)
				.setData(data)
				.build());
	}

	public void broadcast(MessageEvent me) {
		System.out.println("dispatchers.size() = " + dispatchers.size());
		for(SseDispatcher dispatcher : Collections.unmodifiableSet(dispatchers)) {
			try {
				System.out.println("dispatcher = " + dispatcher);
				dispatcher.send(me);
			} catch (IOException e) {
				// Client disconnected. Removing from dispatchers
				dispatchers.remove(dispatcher);
			}
		}
	}
}
