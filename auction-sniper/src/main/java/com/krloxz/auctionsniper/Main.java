package com.krloxz.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
  public static final String JOIN_COMMAND_FORMAT = "";
  public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;
  private static final int ARG_ITEM_ID = 3;

  private MainWindow ui;

  @SuppressWarnings("unused")
  private Chat notToBeGCd;

  public Main() throws Exception {
    startUserInterface();
  }

  public static void main(final String... args) throws Exception {
    final Main main = new Main();
    main.joinAuction(connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
  }

  private static XMPPConnection connection(final String hostname, final String username, final String password)
      throws XMPPException {
    final XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);
    return connection;
  }

  private void joinAuction(final XMPPConnection connection, final String itemId) throws XMPPException {
    disconnectWhenUICloses(connection);

    final Chat chat =
        connection.getChatManager().createChat(auctionId(itemId, connection), null);
    this.notToBeGCd = chat;

    final Auction auction = new XMPPAuction(chat);

    chat.addMessageListener(
        new AuctionMessageTranslator(
            connection.getUser(),
            new AuctionSniper(auction, new SniperStateDisplayer())));
    auction.join();
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
        Main.this.ui = new MainWindow();
      }
    });
  }

  public class SniperStateDisplayer implements SniperListener {

    @Override
    public void sniperBidding() {
      showStatus(MainWindow.STATUS_BIDDING);
    }

    @Override
    public void sniperLost() {
      showStatus(MainWindow.STATUS_LOST);
    }

    @Override
    public void sniperWinning() {
      showStatus(MainWindow.STATUS_WINNING);
    }

    @Override
    public void sniperWon() {
      showStatus(MainWindow.STATUS_WON);
    }

    private void showStatus(final String status) {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          Main.this.ui.showStatusText(status);
        }
      });
    }
  }

}
