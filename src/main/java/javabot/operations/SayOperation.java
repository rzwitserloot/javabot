package javabot.operations;

import java.util.ArrayList;
import java.util.List;

import javabot.BotEvent;
import javabot.Message;
import javabot.Javabot;

/**
 * @author ricky_clarkson
 */
public class SayOperation extends BotOperation {
    public SayOperation(final Javabot javabot) {
        super(javabot);
    }

    /**
     * @see BotOperation#handleMessage(BotEvent)
     */
    @Override
    public List<Message> handleMessage(final BotEvent event) {
        final List<Message> messages = new ArrayList<Message>();
        String message = event.getMessage();
        final String channel = event.getChannel();
        if(!message.startsWith("say ")) {
            return messages;
        }
        message = message.substring("say ".length());
        messages.add(new Message(channel, event, message));
        return messages;
    }
}