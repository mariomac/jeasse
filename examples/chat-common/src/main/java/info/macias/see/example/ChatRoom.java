package info.macias.see.example;

import info.macias.sse.EventBroadcast;
import info.macias.sse.EventTarget;
import info.macias.sse.events.MessageEvent;

import java.io.IOException;

public class ChatRoom {

    private final EventBroadcast broadcaster = new EventBroadcast();

    private void notifyUserLeft(EventTarget<String> eventTarget) {
        broadcaster.broadcast("message", "(" + eventTarget.getIdentifier() + " left the room)");
    }

    // When somebody asks to be connected to the chat servlet, it welcomes it and adds to the list of subscribers
    public void addNewUser(EventTarget<String> eventTarget) throws IOException {
        broadcaster.addSubscriber(eventTarget,
                new MessageEvent.Builder().setData("*** Welcome to the chat server ***").build());

        eventTarget.onRemoteClose(this::notifyUserLeft);
    }

    public void onMessage(String messageJson) {
        broadcaster.broadcast("message", dirtyJsonParse(messageJson));
    }

    // Dirty aux function
    private static String dirtyJsonParse(String json) {
        String senderChunk = "\"sender\":\"";
        String messageChunk = "\"message\":\"";
        String sender = json.substring(
                json.indexOf(senderChunk) + senderChunk.length(),
                json.indexOf("\","+messageChunk));
        String message = json.substring(
                json.indexOf(messageChunk) + messageChunk.length(),
                json.indexOf("\"}"));
        if("".equals(sender.trim())) sender = "Anonymous";
        return sender + " says: " + message;
    }

    public void close() {
        broadcaster.close();
    }
}
