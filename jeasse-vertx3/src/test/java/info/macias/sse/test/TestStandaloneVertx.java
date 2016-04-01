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

import info.macias.sse.EventBroadcast;
import info.macias.sse.vertx3.VertxEventTarget;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Scanner;

/**
 * @author Mario Macias (http://github.com/mariomac)
 */
@Ignore
public class TestStandaloneVertx {

	@Ignore
	@Test
	public void startVertx() {
		Vertx vertx = Vertx.vertx();
		EventBroadcast broadcaster = new EventBroadcast();

		Router router = Router.router(vertx);

		// Allows getting body in POST methods
		router.route().handler(BodyHandler.create());

		router.get("/test").handler(ctx -> {
			try {
				broadcaster.addSubscriber(new VertxEventTarget(ctx.request()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		router.post("/test").handler(ctx -> {
			Scanner scanner = new Scanner(ctx.getBodyAsString());
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
			System.out.println("sb = " + sb);
			broadcaster.broadcast("message",sb.toString());
			ctx.response().end();
		});

		router.route("/*").method(HttpMethod.GET).handler(StaticHandler.create());
		
		// Instantiate HTTP server
		HttpServer server = vertx.createHttpServer()
				.requestHandler(router::accept);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				broadcaster.close();
			}
		});
		server.listen(8080);


		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
