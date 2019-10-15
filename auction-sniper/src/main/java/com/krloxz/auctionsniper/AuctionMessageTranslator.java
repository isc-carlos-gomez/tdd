package com.krloxz.auctionsniper;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener {

  private final AuctionEventListener listener;

  public AuctionMessageTranslator(final AuctionEventListener listener) {
    this.listener = listener;
  }

  @Override
  public void processMessage(final Chat chat, final Message message) {
    final HashMap<String, String> event = unpackEventFrom(message);
    final String type = event.get("Event");
    if ("CLOSE".equals(type)) {
      this.listener.auctionClosed();
    } else if ("PRICE".equals(type)) {
      this.listener.currentPrice(Integer.parseInt(event.get("CurrentPrice")), Integer.parseInt(event.get("Increment")));
    }
  }

  private HashMap<String, String> unpackEventFrom(final Message message) {
    final HashMap<String, String> event = new HashMap<>();
    for (final String element : message.getBody().split(";")) {
      final String[] pair = element.split(":");
      event.put(pair[0].trim(), pair[1].trim());
    }
    return event;
  }
}
