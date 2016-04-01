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
import info.macias.sse.servlet3.ServletEventTarget;

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

	EventBroadcast broadcaster = new EventBroadcast();

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
        broadcaster.addSubscriber(new ServletEventTarget(req));
    }

	@Override
	public void destroy() {
		broadcaster.close();
		super.destroy();
	}
}
