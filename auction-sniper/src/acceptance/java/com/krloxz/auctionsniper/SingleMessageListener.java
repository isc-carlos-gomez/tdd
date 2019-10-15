package com.krloxz.auctionsniper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Matcher;
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

  public void receivesAMessage(final Matcher<? super String> messageMatcher) throws InterruptedException {
    final Message message = this.messages.poll(5, TimeUnit.SECONDS);
    assertThat("Message", message, is(notNullValue()));
    assertThat(message.getBody(), messageMatcher);
  }

}
