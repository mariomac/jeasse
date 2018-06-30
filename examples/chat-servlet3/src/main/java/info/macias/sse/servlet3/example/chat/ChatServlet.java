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

import info.macias.sse.servlet3.ServletEventTarget;

import javax.servlet.AsyncContext;
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
public class ChatServlet extends HttpServlet {
    private ChatRoom room = new ChatRoom();

    private static String mapId(AsyncContext context) {
        return mapId((HttpServletRequest)context.getRequest());
    }

    private static String mapId(HttpServletRequest req) {
        return req.getParameter("uuid");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        room.addNewUser(ServletEventTarget.create(req, ChatServlet::mapId));
    }

    // When somebody posts a message, it broadcasts it to all its subscribers
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Scanner scanner = new Scanner(req.getInputStream());
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        room.onMessage(ChatServlet.mapId(req), sb.toString());
    }

    // When the servlet is destroyed, it closes all the broadcast subscribers
    @Override
    public void destroy() {
        room.close();
        super.destroy();
    }

}

