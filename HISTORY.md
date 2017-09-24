# History of releases

## 0.11.1

- Improved multithread performance of event-broadcaster by using ConcurrentLinkedQueue.
- All fields of MessageEvent set as final to enforce immutability.
- Tested with Vert.x 3.4.2.
- Tested with servlet 4.0.0

## 0.11.0

- `EventBroadcast.getSubscribersCount()` and `hasSubscribers()` methods.

## 0.10.0

- Support for Vert.x 3
- Deprecated `SseBroadcaster` and `SseDispatcher` for Servlet 3.0
- Change project and artifacts structure to enforce modularity.

## 0.9.0

- Initial Release of JEaSSE with Servlet 3.0 support.