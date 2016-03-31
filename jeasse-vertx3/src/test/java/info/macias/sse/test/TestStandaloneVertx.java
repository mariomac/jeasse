package info.macias.sse.test;

import info.macias.sse.VertxSseBroadcaster;
import io.netty.channel.ChannelFlushPromiseNotifier;
import io.vertx.core.Launcher;
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
		VertxSseBroadcaster broadcaster = new VertxSseBroadcaster();

		Router router = Router.router(vertx);

		// Allows getting body in POST methods
		router.route().handler(BodyHandler.create());

		router.get("/test").handler(ctx -> {
			try {
				broadcaster.addSubscriber(ctx.request());
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
