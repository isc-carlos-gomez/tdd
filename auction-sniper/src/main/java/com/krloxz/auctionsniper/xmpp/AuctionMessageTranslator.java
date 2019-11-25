package com.krloxz.auctionsniper.xmpp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import com.krloxz.auctionsniper.domain.AuctionEventListener;
import com.krloxz.auctionsniper.domain.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

  private final String sniperId;
  private final AuctionEventListener listener;
  private final XMPPFailureReporter failureReporter;

  public AuctionMessageTranslator(final String sniperId, final AuctionEventListener listener,
      final XMPPFailureReporter failureReporter) {
    this.sniperId = sniperId;
    this.listener = listener;
    this.failureReporter = failureReporter;
  }

  @Override
  public void processMessage(final Chat chat, final Message message) {
    try {
      translate(message.getBody());
    } catch (final Exception exception) {
      this.failureReporter.cannotTranslateMessage(this.sniperId, message.getBody(), exception);
      this.listener.auctionFailed();
    }
  }

  private void translate(final String message) {
    final AuctionEvent event = AuctionEvent.from(message);
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
      final String value = this.fields.get(fieldName);
      if (null == value) {
        throw new MissingValueException(fieldName);
      }
      return value;
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

  private static class MissingValueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MissingValueException(final String message) {
      super(message);
    }

  }

}
