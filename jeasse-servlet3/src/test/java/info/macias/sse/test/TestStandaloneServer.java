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

package info.macias.sse.test;

import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Mario Macias (http://github.com/mariomac)
 */
@Ignore
public class TestStandaloneServer {

	static Server server;

	@BeforeClass
	public static void initialize() {
		server = new Server(8080);
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setWelcomeFiles(new String[] {"index.html"});
		resourceHandler.setResourceBase(TestStandaloneServer.class.getResource("/").toExternalForm());
		resourceHandler.setDirectoriesListed(true);

		ContextHandler resourceCtxHandler = new ContextHandler("/f");
		resourceCtxHandler.setHandler(resourceHandler);

		HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.addHandler(resourceCtxHandler);

		// Redirect from /gui to /gui/
		RewriteHandler rewriteHandler = new RewriteHandler();
		RedirectPatternRule rpr = new RedirectPatternRule();
		rpr.setPattern("/f");
		rpr.setLocation("/f/");
		rewriteHandler.addRule(rpr);
		handlerCollection.addHandler(rewriteHandler);

		ServletHandler sh = new ServletHandler();
		sh.addServletWithMapping(TestServlet.class,"/test");
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

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@AfterClass
	public static void end() {
		System.out.println("Stopping jetty");
		try {
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		server.destroy();
	}

	@Test
	@Ignore
	public void testStandaloneServer() {
		while(server.isRunning()) Thread.yield();
	}

}
