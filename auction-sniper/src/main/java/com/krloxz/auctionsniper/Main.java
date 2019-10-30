package com.krloxz.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

/**
 * @author Carlos Gomez
 */
public class Main {

  public static final String AUCTION_RESOURCE = "Auction";
  public static final String ITEM_ID_AS_LOGIN = "auction-%s";
  public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
  public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;

  private final SnipersTableModel snipers = new SnipersTableModel();
  private MainWindow ui;

  private final List<Chat> notToBeGCd = new ArrayList<>();

  public Main() throws Exception {
    startUserInterface();
  }

  public static void main(final String... args) throws Exception {
    final Main main = new Main();
    final XMPPConnection connection = connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
    main.disconnectWhenUICloses(connection);
    for (int i = 3; i < args.length; i++) {
      main.joinAuction(connection, args[i]);
    }
  }

  private static XMPPConnection connection(final String hostname, final String username, final String password)
      throws XMPPException {
    final XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);
    return connection;
  }

  private void joinAuction(final XMPPConnection connection, final String itemId) throws Exception {
    final Chat chat = connection.getChatManager()
        .createChat(auctionId(itemId, connection), null);
    this.notToBeGCd.add(chat);

    final Auction auction = new XMPPAuction(chat);
    chat.addMessageListener(
        new AuctionMessageTranslator(
            connection.getUser(),
            new AuctionSniper(itemId, auction, new SwingThreadSniperListener(this.snipers))));
    auction.join();
    safelyAddItemToModel(itemId);
  }

  private void disconnectWhenUICloses(final XMPPConnection connection) {
    this.ui.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(final WindowEvent e) {
        connection.disconnect();
      }
    });
  }

  private static String auctionId(final String itemId, final XMPPConnection connection) {
    return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
  }

  private void startUserInterface() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {

      @Override
      public void run() {
        Main.this.ui = new MainWindow(Main.this.snipers);
      }
    });
  }

  private void safelyAddItemToModel(final String itemId) throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {
      @Override
      public void run() {
        Main.this.snipers.addSniper(SniperSnapshot.joining(itemId));
      }
    });
  }

  private class SwingThreadSniperListener implements SniperListener {
    private final SnipersTableModel snipers;

    public SwingThreadSniperListener(final SnipersTableModel snipers) {
      this.snipers = snipers;
    }

    @Override
    public void sniperStateChanged(final SniperSnapshot sniperSnapshot) {
      this.snipers.sniperStateChanged(sniperSnapshot);
    }
  }

}
