# jeasse
Java Easy SSE (Server-Side Events) implementation

Features:

* Minimal footprint
* Easily integrable with plain HTTP servlets
	- Asynchronous servlet support through Servlet 3.0
* Broadcaster that automatically detaches subscribers
	- Thread-safe management of subscribers


Usage example:

	public class TestServlet extends HttpServlet {
	
		SseBroadcaster broadcaster = new SseBroadcaster();
		
		// Attaches a subscriber
		@Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          broadcaster.addSubscriber(req);
      }
	
	   // Broadcasts a message to all the subscribers
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			broadcaster.broadcast("message","received a post message from somebody");
		}	   
	}

