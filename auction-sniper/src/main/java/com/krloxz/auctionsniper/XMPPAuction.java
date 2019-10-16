package com.krloxz.auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {

  private final Chat chat;

  public XMPPAuction(final Chat chat) {
    this.chat = chat;
  }

  @Override
  public void bid(final int amount) {
    sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
  }

  @Override
  public void join() {
    sendMessage(Main.JOIN_COMMAND_FORMAT);
  }

  private void sendMessage(final String message) {
    try {
      this.chat.sendMessage(message);
    } catch (final XMPPException e) {
      e.printStackTrace();
    }
  }

}