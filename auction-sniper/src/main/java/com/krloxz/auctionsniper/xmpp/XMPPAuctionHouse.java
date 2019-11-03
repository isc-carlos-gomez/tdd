package com.krloxz.auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.krloxz.auctionsniper.domain.Auction;
import com.krloxz.auctionsniper.domain.AuctionHouse;

/**
 * Implementation of {@link AuctionHouse} that uses an XMPP Server to create auctions. Should be
 * {@link #connect(String, String, String) connected} before creating any auction and
 * {@link #disconnect() disconnected} when no longer needed.
 *
 * @author Carlos Gomez
 *
 */
public class XMPPAuctionHouse implements AuctionHouse {

  protected static final String AUCTION_RESOURCE = "Auction";
  private final XMPPConnection connection;

  private XMPPAuctionHouse(final XMPPConnection connection) {
    this.connection = connection;
  }

  /**
   * Creates a new instance and connects with the XMPP Server so that auctions can be created.
   *
   * @param hostname
   *        XMPP host
   * @param username
   *        XMPP username
   * @param password
   *        XMPP password
   * @return a new instance of {@link XMPPAuctionHouse}
   * @throws XMPPException
   *         if connection with the XMPP Server cannot be established
   */
  public static XMPPAuctionHouse connect(final String hostname, final String username, final String password)
      throws XMPPException {
    final XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);
    return new XMPPAuctionHouse(connection);
  }

  @Override
  public Auction auctionFor(final String itemId) {
    return new XMPPAuction(this.connection, itemId);
  }

  /**
   * Disconnects this auction house from the XMPP Server.
   */
  public void disconnect() {
    this.connection.disconnect();
  }

}
