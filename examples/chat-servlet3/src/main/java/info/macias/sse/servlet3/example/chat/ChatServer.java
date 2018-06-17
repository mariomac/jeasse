/*
Copyright 2016 - Mario Macias Lloret

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package info.macias.sse.servlet3.example.chat;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.resource.Resource;

/**
 * @author Mario Macías (http://github.com/mariomac)
 */
public class ChatServer {
    public static void main(String[] args) throws Exception {
        // Prepare jetty to serve static files and setup the Chat Servlet

        final Server server = new Server(8080);
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[] {"index.html"});

        resourceHandler.setResourceBase(Resource.newClassPathResource("/static").getURI().toURL().toExternalForm());
        resourceHandler.setDirectoriesListed(true);

        ContextHandler resourceCtxHandler = new ContextHandler("/");
        resourceCtxHandler.setHandler(resourceHandler);

        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.addHandler(resourceCtxHandler);

        ServletHandler sh = new ServletHandler();
        sh.addServletWithMapping(ChatServlet.class,"/send");
        handlerCollection.addHandler(sh);

        server.setHandler(handlerCollection);
        new Thread(() -> {
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                server.destroy();
            }
        });


    }
}
