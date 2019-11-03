package com.krloxz.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import com.krloxz.auctionsniper.domain.AuctionHouse;
import com.krloxz.auctionsniper.domain.SniperLauncher;
import com.krloxz.auctionsniper.domain.SniperPortfolio;
import com.krloxz.auctionsniper.ui.MainWindow;
import com.krloxz.auctionsniper.xmpp.XMPPAuctionHouse;

/**
 * @author Carlos Gomez
 */
public class Main {

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;

  private MainWindow ui;
  private final SniperPortfolio portfolio;

  public Main() throws Exception {
    this.portfolio = new SniperPortfolio();
    startUserInterface();
  }

  public static void main(final String... args) throws Exception {
    final Main main = new Main();
    final XMPPAuctionHouse auctionHouse =
        XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
    main.disconnectWhenUICloses(auctionHouse);
    main.addUserRequestListenerFor(auctionHouse);
  }

  private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
    this.ui.addUserRequestListener(new SniperLauncher(auctionHouse, this.portfolio));
  }

  private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
    this.ui.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(final WindowEvent e) {
        auctionHouse.disconnect();
      }
    });
  }

  private void startUserInterface() throws Exception {
    SwingUtilities.invokeAndWait(new Runnable() {

      @Override
      public void run() {
        Main.this.ui = new MainWindow(Main.this.portfolio);
      }
    });
  }

}
