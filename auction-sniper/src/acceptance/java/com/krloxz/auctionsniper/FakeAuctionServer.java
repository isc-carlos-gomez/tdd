package com.krloxz.auctionsniper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * @author Carlos Gomez
 */
public class FakeAuctionServer {

  private static final String XMPP_HOSTNAME = "localhost";
  private static final String ITEM_ID_AS_LOGIN = "auction-%s";
  private static final String AUCTION_RESOURCE = "Auction";
  private static final String AUCTION_PASSWORD = "auction";

  private final String itemId;
  private final XMPPConnection connection;
  private final SingleMessageListener messageListener;
  private Chat currentChat;

  public FakeAuctionServer(final String itemId) {
    this.itemId = itemId;
    this.connection = new XMPPConnection(XMPP_HOSTNAME);
    this.messageListener = new SingleMessageListener();
  }

  public void startSellingItem() throws XMPPException {
    this.connection.connect();
    this.connection.login(String.format(ITEM_ID_AS_LOGIN, this.itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
    this.connection.getChatManager().addChatListener(new ChatManagerListener() {

      @Override
      public void chatCreated(final Chat chat, final boolean createdLocally) {
        FakeAuctionServer.this.currentChat = chat;
        chat.addMessageListener(FakeAuctionServer.this.messageListener);
      }
    });
  }

  public void reportPrice(final int price, final int increment, final String bidder) throws XMPPException {
    this.currentChat.sendMessage(String.format(
        "SOLVersion: 1.1; Event: PRICE; " + "CurrentPrice: %d; Increment: %d; Bidder: %s;", price, increment, bidder));
  }

  public void hasReceivedJoinRequestFrom(final String sniperId) throws InterruptedException {
    receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
  }

  public void hasReceivedBid(final int bid, final String sniperId) throws InterruptedException {
    receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
  }

  public void announceClosed() throws XMPPException {
    this.currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
  }

  public void stop() {
    this.connection.disconnect();
  }

  public String getItemId() {
    return this.itemId;
  }

  private void receivesAMessageMatching(final String sniperId, final Matcher<? super String> messageMatcher)
      throws InterruptedException {
    this.messageListener.receivesAMessage(messageMatcher);
    assertThat(this.currentChat.getParticipant(), equalTo(sniperId));
  }

}
