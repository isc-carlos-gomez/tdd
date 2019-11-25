package com.krloxz.auctionsniper.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.krloxz.auctionsniper.domain.Auction;
import com.krloxz.auctionsniper.domain.AuctionHouse;
import com.krloxz.auctionsniper.domain.Item;

/**
 * Implementation of {@link AuctionHouse} that uses an XMPP Server to create auctions. Should be
 * {@link #connect(String, String, String) connected} before creating any auction and
 * {@link #disconnect() disconnected} when no longer needed.
 *
 * @author Carlos Gomez
 *
 */
public class XMPPAuctionHouse implements AuctionHouse {

  public static final String LOG_FILE_NAME = "auction-sniper.log";
  protected static final String AUCTION_RESOURCE = "Auction";
  private static final String LOGGER_NAME = "XMPPAuctionHouse";

  private final XMPPConnection connection;
  private final XMPPFailureReporter failureReporter;

  private XMPPAuctionHouse(final XMPPConnection connection) {
    this.connection = connection;
    this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
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
  public Auction auctionFor(final Item item) {
    return new XMPPAuction(this.connection, item, this.failureReporter);
  }

  /**
   * Disconnects this auction house from the XMPP Server.
   */
  public void disconnect() {
    this.connection.disconnect();
  }

  private Logger makeLogger() throws XMPPAuctionException {
    final Logger logger = Logger.getLogger(LOGGER_NAME);
    logger.setUseParentHandlers(false);
    logger.addHandler(simpleFileHandler());
    return logger;
  }

  private FileHandler simpleFileHandler() throws XMPPAuctionException {
    try {
      final FileHandler handler = new FileHandler(LOG_FILE_NAME);
      handler.setFormatter(new SimpleFormatter());
      return handler;
    } catch (final Exception e) {
      throw new XMPPAuctionException(
          "Could not create logger FileHandler " + LOG_FILE_NAME, e);
    }
  }

}
