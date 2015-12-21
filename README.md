# jeasse
Java Easy SSE (Server-Side Events) implementation

Features:

* Minimal footprint
* Easily integrable with plain HTTP servlets
* Broadcaster that automatically detaches listeners
* Asynchronous servlet support through Servlet 3.0

Usage example:

	public class TestServlet extends HttpServlet {
	
		SseBroadcaster broadcaster = new SseBroadcaster();
		
		// Attaches a listener
		@Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          broadcaster.addListener(req);
      }
	
	   // Broadcasts a message to all the listeners
		@Override
		protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			broadcaster.broadcast("message","received a post message from somebody");
		}	   
	}

