package info.macias.sse.servlet3.example.chat;

import info.macias.sse.EventBroadcast;
import info.macias.sse.EventTarget;
import info.macias.sse.events.MessageEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {

    private static final int PING_MS = 5000;

    private final EventBroadcast broadcaster = new EventBroadcast();

    private Timer pingerTimer;

    private Map<String, String> idNickMap = new HashMap<>();

    public ChatRoom() {
        pingerTimer = new Timer();
        pingerTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        broadcaster.broadcast("ping", "");
                    }
                }, PING_MS, PING_MS);
    }

    private void notifyUserLeft(EventTarget<String> eventTarget) {
        String nick = idNickMap.getOrDefault(eventTarget.getIdentifier(), eventTarget.getIdentifier());
        System.out.println("nick = " + nick);
        broadcaster.broadcast("message", "(" + nick + " left the room)");
    }

    // When somebody asks to be connected to the chat servlet, it welcomes it and adds to the list of subscribers
    public void addNewUser(EventTarget<String> eventTarget) throws IOException {
        broadcaster.addSubscriber(eventTarget,
                new MessageEvent.Builder().setData("*** Welcome to the chat server ***").build());

        eventTarget.onRemoteClose(this::notifyUserLeft);
    }

    public void onMessage(String sessionIdentifier, String messageJson) {
        String[] nameMsg = dirtyJsonParse(messageJson);
        idNickMap.put(sessionIdentifier, nameMsg[0]);
        broadcaster.broadcast("message", nameMsg[0] + " says: " + nameMsg[1]);
    }

    // Dirty aux function
    private static String[] dirtyJsonParse(String json) {
        String senderChunk = "\"sender\":\"";
        String messageChunk = "\"message\":\"";
        String sender = json.substring(
                json.indexOf(senderChunk) + senderChunk.length(),
                json.indexOf("\","+messageChunk));
        String message = json.substring(
                json.indexOf(messageChunk) + messageChunk.length(),
                json.indexOf("\"}"));
        if("".equals(sender.trim())) sender = "Anonymous";
        return new String[]{sender, message};
    }

    public void close() {
        pingerTimer.cancel();
        broadcaster.close();
    }
}
