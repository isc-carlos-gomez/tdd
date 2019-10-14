package com.krloxz.auctionsniper;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        this.connection.login(String.format(ITEM_ID_AS_LOGIN, this.itemId),
                AUCTION_PASSWORD, AUCTION_RESOURCE);
        this.connection.getChatManager().addChatListener(
                new ChatManagerListener() {

                    @Override
                    public void chatCreated(final Chat chat, final boolean createdLocally) {
                        FakeAuctionServer.this.currentChat = chat;
                        chat.addMessageListener(FakeAuctionServer.this.messageListener);
                    }
                });
    }

    public void reportPrice(int price, int increment, String bidder)
            throws XMPPException {
        currentChat.sendMessage(
                String.format("SOLVersion: 1.1; Event: PRICE; "
                                + "CurrentPrice: %d; Increment: %d; Bidder: %s;",
                        price, increment, bidder));
    }

    public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(String.format(Main.JOIN_COMMAND_FORMAT)));
    }

    public void hasReceivedBid(int bid, String sniperId)
            throws InterruptedException {
        receivesAMessageMatching(sniperId, equalTo(String.format(Main.BID_COMMAND_FORMAT, bid)));
    }

    public void announceClosed() throws XMPPException {
        this.currentChat.sendMessage(new Message());
    }

    public void stop() {
        this.connection.disconnect();
    }

    public String getItemId() {
        return this.itemId;
    }

    private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher)
            throws InterruptedException {
        messageListener.receivesAMessage(messageMatcher);
        assertThat(currentChat.getParticipant(), equalTo(sniperId));
    }

}
