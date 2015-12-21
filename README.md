# jeasse
Java Easy SSE (Server-Side Events) implementation

Features:

* Minimal footprint
* Easily integrable with plain HTTP servlets
	- Asynchronous servlet support through Servlet 3.0
* Broadcaster that automatically detaches subscriptors
	- Thread-safe management of subscriptors


Usage example:

	public class TestServlet extends HttpServlet {
	
		SseBroadcaster broadcaster = new SseBroadcaster();
		
		// Attaches a listener
		@Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          broadcaster.addSubscriptor(req);
      }
	
	   // Broadcasts a message to all the listeners
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			broadcaster.broadcast("message","received a post message from somebody");
		}	   
	}

