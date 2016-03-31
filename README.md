# jEaSSE

Java Easy SSE (Server-Sent Events) implementation (at server side).

Features:

* Minimal footprint, with no 3rd-party dependencies.
* Easily integrable with plain HTTP servlets (see below examples)
* Asynchronous servlet operation: don't spend your server threads.
* Broadcasting mode with automatic, thread-safe management of subscribers

## Maven coordinates
	<dependency>
		<groupId>info.macias</groupId>
		<artifactId>jeasse-servlet3</artifactId>
		<version>0.9.1</version>
	</dependency>
	
You also need to include Java Servlet API > 3

	<dependency>
		<groupId>javax.servlet</groupId>
		<artifactId>javax.servlet-api</artifactId>
		<version>[3.0.1,)</version>
	</dependency>

## Usage examples

Basic, one-to-one subscription:

	@WebServlet(asyncSupported = true)
	public class ExampleServlet1 extends HttpServlet {
	
		SseDispatcher dispatcher;;
		
		@Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           dispatcher = new SseDispatcher(req).ok().open();
        }
	
	    public void onGivenEvent(String info) {
	       dispatcher.send("givenEvent",info);
	    }
	}


Subscription to broadcast messages:

	@WebServlet(asyncSupported = true)
	public class ExampleServlet1 extends HttpServlet {
	
		SseBroadcaster broadcaster = new SseBroadcaster();
		
		// Attaches a subscriber
		@Override
    	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            broadcaster.addSubscriber(req);
        }
	
	    // Broadcasts a message to all the subscribers
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			broadcaster.broadcast("message","broadcasting that I received a POST message from somebody");
		}	   
	}

Subscription to broadcast messages, with individial welcome message (MessageEvent API used):

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    MessageEvent welcome = new MessageEvent.Builder().setData("Welcome to the broadcasting service").build();
		broadcaster.addSubscriber(req,welcome);
	}   

## Client-side libraries

At the moment I do not provide any client-side library. You can use other cool libraries:

* [Jersey's EventInput](https://jersey.java.net/documentation/latest/sse.html#d0e11869)
* [Javascript's EventSource](https://html.spec.whatwg.org/multipage/comms.html#server-sent-events) (Enabled by default in all browsers excepting Internet Explorer/MS Edge)
	- Implementations for Internet Explorer (https://github.com/remy/polyfills/blob/master/EventSource.js) (https://github.com/amvtek/EventSource) (https://github.com/remy/polyfills/blob/master/EventSource.js)
