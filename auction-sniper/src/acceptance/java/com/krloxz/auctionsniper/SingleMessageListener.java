package com.krloxz.auctionsniper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * @author Carlos Gomez
 */
public class SingleMessageListener implements MessageListener {

    private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<>(1);

    @Override
    public void processMessage(final Chat chat, final Message message) {
        this.messages.add(message);
    }

    public void receivesAMessage() throws InterruptedException {
        assertThat("Message", this.messages.poll(5, TimeUnit.SECONDS), is(notNullValue()));
    }

}
