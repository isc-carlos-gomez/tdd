package com.krloxz.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AuctionMessageTranslatorTest {

    public static final Chat UNUSED_CHAT = null;
    private final AuctionEventListener listener = mock(AuctionEventListener.class);
    private final AuctionMessageTranslator translator = new AuctionMessageTranslator(listener);

    @Test
    void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        translator.processMessage(UNUSED_CHAT, message);

        verify(this.listener).auctionClosed();
    }

    @Test
    void notifiesBidDetailsWhenCurrentPriceMessageReceived() {
        Message message = new Message();
        message.setBody(
                "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;"
        );
        translator.processMessage(UNUSED_CHAT, message);

        verify(this.listener).currentPrice(192, 7);
    }

}
