package com.krloxz.auctionsniper;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import com.krloxz.auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

  private final AuctionEventListener listener;
  private final String sniperId;

  public AuctionMessageTranslator(final String sniperId, final AuctionEventListener listener) {
    this.sniperId = sniperId;
    this.listener = listener;
  }

  @Override
  public void processMessage(final Chat chat, final Message message) {
    final AuctionEvent event = AuctionEvent.from(message.getBody());
    final String eventType = event.type();
    if ("CLOSE".equals(eventType)) {
      this.listener.auctionClosed();
    }
    if ("PRICE".equals(eventType)) {
      this.listener.currentPrice(event.currentPrice(), event.increment(), event.isFrom(this.sniperId));
    }
  }

  private static class AuctionEvent {

    private final Map<String, String> fields = new HashMap<>();

    public String type() {
      return get("Event");
    }

    public int currentPrice() {
      return getInt("CurrentPrice");
    }

    public int increment() {
      return getInt("Increment");
    }

    public PriceSource isFrom(final String sniperId) {
      return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
    }

    private String bidder() {
      return get("Bidder");
    }

    private int getInt(final String fieldName) {
      return Integer.parseInt(get(fieldName));
    }

    private String get(final String fieldName) {
      return this.fields.get(fieldName);
    }

    private void addField(final String field) {
      final String[] pair = field.split(":");
      this.fields.put(pair[0].trim(), pair[1].trim());
    }

    static AuctionEvent from(final String messageBody) {
      final AuctionEvent event = new AuctionEvent();
      for (final String field : fieldsIn(messageBody)) {
        event.addField(field);
      }
      return event;
    }

    static String[] fieldsIn(final String messageBody) {
      return messageBody.split(";");
    }

  }

}
