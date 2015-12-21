package info.macias.sse.test;

import info.macias.sse.SseBroadcaster;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
@WebServlet(asyncSupported = true)
public class TestServlet extends HttpServlet {

	SseBroadcaster broadcaster = new SseBroadcaster();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Scanner scanner = new Scanner(req.getInputStream());
		StringBuilder sb = new StringBuilder();
		while(scanner.hasNextLine()) {
			sb.append(scanner.nextLine());
		}
		System.out.println("sb = " + sb);
		broadcaster.broadcast("message",sb.toString());
	}

	//http://cjihrig.com/blog/the-server-side-of-server-sent-events/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        broadcaster.addSubscriber(req);
    }

	@Override
	public void destroy() {
		broadcaster.close();
		super.destroy();
	}
}
