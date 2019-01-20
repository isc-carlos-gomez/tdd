package com.krloxz.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

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

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        this.messageListener.receivesAMessage();
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

}
