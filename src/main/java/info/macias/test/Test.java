package info.macias.test;

import info.macias.sse.MessageDispatcher;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
public class Test extends HttpServlet {
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        final MessageDispatcher md = new MessageDispatcher((HttpServletResponse)res).ok().open();

        TT tt = new TT(md);
        new Timer().schedule(tt, 1000,1000);

        // TO DO:
        while(!tt.closed) Thread.yield();
    }
    public class TT extends TimerTask {
        int i = 0;
        MessageDispatcher md;
        boolean closed = false;
        public TT(MessageDispatcher md) {
            this.md = md;
        }

        @Override
        public void run() {
            try {
                md.send("message","Mensaje " + (++i));
                // TO DO: cuando se recibe una EOF exception, es porque el cliente ha cerrado
            } catch (IOException e) {
                e.printStackTrace();
                closed = true;
            }
        }
    }
}
