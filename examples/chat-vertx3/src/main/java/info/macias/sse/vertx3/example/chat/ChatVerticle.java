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

package info.macias.sse.vertx3.example.chat;

import info.macias.sse.EventBroadcast;
import info.macias.sse.events.MessageEvent;
import info.macias.sse.vertx3.VertxEventTarget;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.launcher.commands.ClasspathHandler;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Scanner;

/**
 * @author Mario Macias (http://github.com/mariomac)
 */
public class ChatVerticle extends AbstractVerticle {
	private EventBroadcast broadcaster;

	@Override
	public void start() throws Exception {
		broadcaster = new EventBroadcast();

		Router router = Router.router(vertx);

		// Allows getting body in POST methods
		router.route().handler(BodyHandler.create());

		// Subscription request
		router.get("/send").handler(ctx -> {
			try {
				broadcaster.addSubscriber(new VertxEventTarget(ctx.request()),
						new MessageEvent.Builder().setData("*** Welcome to the chat server ***").build()
						);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Message send request
		router.post("/send").handler(ctx -> {
			Scanner scanner = new Scanner(ctx.getBodyAsString());
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNextLine()) {
				sb.append(scanner.nextLine());
			}
			broadcaster.broadcast("message",dirtyJsonParse(sb.toString()));
			ctx.response().end();
		});


		router.route("/*").method(HttpMethod.GET).handler(StaticHandler.create("static"));
		
		// Instantiate HTTP server
		vertx.createHttpServer()
				.requestHandler(router::accept)
				.listen(8080);

	}

	@Override
	public void stop() throws Exception {
		broadcaster.close();
	}

	// Dirty aux function
	private static String dirtyJsonParse(String json) {
		String senderChunk = "\"sender\":\"";
		String messageChunk = "\"message\":\"";
		String sender = json.substring(json.indexOf(senderChunk) + senderChunk.length(),
				json.indexOf("\","+messageChunk));
		String message = json.substring(json.indexOf(messageChunk) + messageChunk.length(),
				json.indexOf("\"}"));
		if("".equals(sender.trim())) sender = "Anonymous";
		return sender + " says: " + message;
	}
}
