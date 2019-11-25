package com.krloxz.auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.krloxz.auctionsniper.domain.Auction;
import com.krloxz.auctionsniper.domain.AuctionEventListener;
import com.krloxz.auctionsniper.domain.Item;
import com.krloxz.auctionsniper.util.Announcer;

public class XMPPAuction implements Auction {

  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_RESOURCE = "Auction";
  private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

  private final Announcer<AuctionEventListener> auctionEventListeners;
  private final Chat chat;
  private final XMPPFailureReporter failureReporter;

  public XMPPAuction(final XMPPConnection connection, final Item item, final XMPPFailureReporter failureReporter) {
    this.auctionEventListeners = Announcer.to(AuctionEventListener.class);
    this.failureReporter = failureReporter;

    final AuctionMessageTranslator translator = translatorFor(connection);
    this.chat = connection.getChatManager()
        .createChat(auctionId(item.identifier, connection), translator);
    addAuctionEventListener(chatDisconnectorFor(translator));
  }

  @Override
  public void bid(final int amount) {
    sendMessage(String.format(XMPPAuction.BID_COMMAND_FORMAT, amount));
  }

  @Override
  public void join() {
    sendMessage(XMPPAuction.JOIN_COMMAND_FORMAT);
  }

  @Override
  public void addAuctionEventListener(final AuctionEventListener listener) {
    this.auctionEventListeners.addListener(listener);
  }

  private AuctionMessageTranslator translatorFor(final XMPPConnection connection) {
    return new AuctionMessageTranslator(
        connection.getUser(), this.auctionEventListeners.announce(), this.failureReporter);
  }

  private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
    return new AuctionEventListener() {
      @Override
      public void auctionFailed() {
        XMPPAuction.this.chat.removeMessageListener(translator);
      }

      @Override
      public void auctionClosed() {
        // empty method
      }

      @Override
      public void currentPrice(final int price, final int increment, final PriceSource priceSource) {
        // empty method
      }
    };
  }

  private static String auctionId(final String itemId, final XMPPConnection connection) {
    return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
  }

  private void sendMessage(final String message) {
    try {
      this.chat.sendMessage(message);
    } catch (final XMPPException e) {
      e.printStackTrace();
    }
  }

}
