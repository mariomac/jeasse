# jEaSSE

Java Easy SSE (Server-Side Events) implementation

Features:

* Minimal footprint
* Easily integrable with plain HTTP servlets
	- Asynchronous servlet support through Servlet 3.0
* Broadcaster that automatically detaches subscribers
	- Thread-safe management of subscribers

## Maven coordinates
	<dependency>
		<groupId>info.macias</groupId>
		<artifactId>jeasse</artifactId>
		<version>0.9.0</version>
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
