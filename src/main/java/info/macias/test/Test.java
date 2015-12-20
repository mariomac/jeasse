package info.macias.test;

import info.macias.sse.MessageDispatcher;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
@WebServlet(asyncSupported = true)
public class Test extends HttpServlet {

    //http://cjihrig.com/blog/the-server-side-of-server-sent-events/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final MessageDispatcher md = new MessageDispatcher(req).ok().open();

        Timer t = new Timer();
        TT tt = new TT(md,t);
        t.schedule(tt, 1000,1000);
    }

    public class TT extends TimerTask {
        int i = 0;
        MessageDispatcher md;
        boolean closed = false;

        Timer t;
        public TT(MessageDispatcher md, Timer t) {
            this.md = md;
            this.t = t;
        }

        @Override
        public void run() {
            try {
                i++;
                md.send("message","Mensaje " + i);
                // TO DO: cuando se recibe una EOF exception, es porque el cliente ha cerrado
            } catch (IOException e) {
                System.out.println("Client went out. Closing...");
                closed = true;
                t.cancel();
                md.close();
            }
        }
    }
}
