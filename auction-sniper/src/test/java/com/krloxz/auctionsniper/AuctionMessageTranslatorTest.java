package com.krloxz.auctionsniper;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.Test;

import com.krloxz.auctionsniper.AuctionEventListener.PriceSource;

class AuctionMessageTranslatorTest {

  private static final Chat UNUSED_CHAT = null;
  private static final String SNIPER_ID = "aSnipperId";

  private final AuctionEventListener listener = mock(AuctionEventListener.class);
  private final AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, this.listener);

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

}
