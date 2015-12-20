package info.macias.test;

import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.AbstractSessionManager;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
public class Main {
    public static void main(String[] args)
    {
        final Server server = new Server(8080);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});
        resourceHandler.setResourceBase(Main.class.getResource("/").toExternalForm());
        resourceHandler.setDirectoriesListed(true);

        ContextHandler resourceCtxHandler = new ContextHandler("/f");
        resourceCtxHandler.setHandler(resourceHandler);

        HandlerCollection handlerCollection = new HandlerCollection();
//        handlerCollection.addHandler(restContexthandler);
        handlerCollection.addHandler(resourceCtxHandler);

        // Redirect from /gui to /gui/
        RewriteHandler rewriteHandler = new RewriteHandler();
        RedirectPatternRule rpr = new RedirectPatternRule();
        rpr.setPattern("/f");
        rpr.setLocation("/f/");
        rewriteHandler.addRule(rpr);
        handlerCollection.addHandler(rewriteHandler);

        ServletHandler sh = new ServletHandler();
        sh.addServletWithMapping(Test.class,"/test");
        handlerCollection.addHandler(sh);

        server.setHandler(handlerCollection);

//        for(Connector conn : server.getConnectors()) {
//            if(conn instanceof ServerConnector) {
//                ((ServerConnector)conn).setIdleTimeout(Integer.MAX_VALUE);
//                ((ServerConnector)conn).setStopTimeout(Integer.MAX_VALUE);
//                ((ServerConnector)conn).setSoLingerTime(Integer.MAX_VALUE);
//                System.out.println("jarl");
//            } else {
//                System.out.println("---");
//            }
//        }



//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    System.err.println("idle threads = " + server.getThreadPool().getIdleThreads() + "\tTotal: " + server.getThreadPool().getThreads());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        },2000,2000);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Stopping jetty");
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                server.destroy();
            }
        }));
    }
}
