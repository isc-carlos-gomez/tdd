package com.krloxz.auctionsniper.xmpp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.Test;

import com.krloxz.auctionsniper.domain.AuctionEventListener;
import com.krloxz.auctionsniper.domain.AuctionEventListener.PriceSource;

class AuctionMessageTranslatorTest {

  private static final Chat UNUSED_CHAT = null;
  private static final String SNIPER_ID = "aSnipperId";

  private final AuctionEventListener listener = mock(AuctionEventListener.class);
  private final XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(
      SNIPER_ID, this.listener, this.failureReporter);

  @Test
  void notifiesAuctionClosedWhenCloseMessageReceived() {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: CLOSE;");
    this.translator.processMessage(UNUSED_CHAT, message);

    verify(this.listener).auctionClosed();
  }

  @Test
  void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");
    this.translator.processMessage(UNUSED_CHAT, message);

    verify(this.listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
  }

  @Test
  void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; Bidder: "
        + SNIPER_ID + ";");
    this.translator.processMessage(UNUSED_CHAT, message);

    verify(this.listener).currentPrice(234, 5, PriceSource.FromSniper);
  }

  @Test
  void notifiesAuctionFailedWhenBadMessageReceived() {
    final Message message = new Message();
    message.setBody("a bad message");
    this.translator.processMessage(UNUSED_CHAT, message);

    verify(this.listener).auctionFailed();
    verify(this.failureReporter).cannotTranslateMessage(
        eq(SNIPER_ID), eq(message.getBody()), any(Exception.class));
  }

  @Test
  void notifiesAuctionFailedWhenEventTypeMissing() {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; CurrentPrice: 234; Increment: 5; Bidder: "
        + SNIPER_ID + ";");
    this.translator.processMessage(UNUSED_CHAT, message);

    verify(this.listener).auctionFailed();
    verify(this.failureReporter).cannotTranslateMessage(
        eq(SNIPER_ID), eq(message.getBody()), any(Exception.class));
  }

  @Test
  void notifiesAuctionFailedWhenBidderMissing() {
    final Message message = new Message();
    message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5;");
    this.translator.processMessage(UNUSED_CHAT, message);

    verify(this.listener).auctionFailed();
    verify(this.failureReporter).cannotTranslateMessage(
        eq(SNIPER_ID), eq(message.getBody()), any(Exception.class));
  }

}
