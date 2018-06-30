# History of releases

## NEXT

* Removed deprecated `SseBroadcaster` and `SseDispatcher` classes.
* Added timeout to automatically close connections after a time.
* Added `RemoteCompletionListener` class to propagate connection closing status.
* Added `IdMapper` interface to allow user configuring the extraction of the user identifier.

## 0.11.3

- Discards and breaks all the changes from 0.11.2. Sorry for the inconvenience. This is a
  version exactly equal to 0.11.1 to get a proper behaviour while 0.12.0 arrives.

## 0.11.2 (discarded version)

- Enable automatic re-dispatching of lost events based on the last-event-id field (thanks to @arkanovicz)

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