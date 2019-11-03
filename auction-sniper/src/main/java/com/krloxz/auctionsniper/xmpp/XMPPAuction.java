package com.krloxz.auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.krloxz.auctionsniper.domain.Auction;
import com.krloxz.auctionsniper.domain.AuctionEventListener;
import com.krloxz.auctionsniper.util.Announcer;

public class XMPPAuction implements Auction {

  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_RESOURCE = "Auction";
  private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
  private final Announcer<AuctionEventListener> auctionEventListeners;
  private final Chat chat;
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";

  public XMPPAuction(final XMPPConnection connection, final String itemId) {
    this.auctionEventListeners = Announcer.to(AuctionEventListener.class);

    this.chat = connection.getChatManager()
        .createChat(auctionId(itemId, connection), null);
    this.chat.addMessageListener(
        new AuctionMessageTranslator(
            connection.getUser(),
            this.auctionEventListeners.announce()));
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
