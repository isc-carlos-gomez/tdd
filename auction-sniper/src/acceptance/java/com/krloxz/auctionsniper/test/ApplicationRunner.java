package com.krloxz.auctionsniper.test;

import com.krloxz.auctionsniper.Main;
import com.krloxz.auctionsniper.domain.SniperSnapshot;
import com.krloxz.auctionsniper.domain.SniperSnapshot.SniperState;
import com.krloxz.auctionsniper.ui.MainWindow;

/**
 * @author Carlos Gomez
 */
public class ApplicationRunner {

  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
  public static final String XMPP_HOSTNAME = "localhost";
  private AuctionSniperDriver driver;

  public void startBiddingIn(final FakeAuctionServer... auctions) {
    startSniper(auctions);

    for (final FakeAuctionServer auction : auctions) {
      final String itemId = auction.getItemId();
      this.driver.startBiddingFor(itemId);
      this.driver.showsSniperState(SniperSnapshot.joining(itemId));
    }
  }

  private void startSniper(final FakeAuctionServer... auctions) {
    final Thread thread = new Thread("Test Application") {

      @Override
      public void run() {
        try {
          Main.main(arguments());
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();

    this.driver = new AuctionSniperDriver(1000);
    this.driver.hasTitle(MainWindow.APPLICATION_TITLE);
    this.driver.hasColumnTitles();
  }

  public void hasShownSniperIsBidding(final FakeAuctionServer auction, final int lastPrice, final int lastBid) {
    this.driver.showsSniperState(new SniperSnapshot(auction.getItemId(), lastPrice, lastBid, SniperState.BIDDING));
  }

  public void showsSniperHasLostAuction(final FakeAuctionServer auction, final int lastPrice, final int lastBid) {
    this.driver.showsSniperState(new SniperSnapshot(auction.getItemId(), lastPrice, lastBid, SniperState.LOST));
  }

  public void hasShownSniperIsWinning(final FakeAuctionServer auction, final int winningBid) {
    this.driver.showsSniperState(new SniperSnapshot(auction.getItemId(), winningBid, winningBid, SniperState.WINNING));
  }

  public void showsSniperHasWonAuction(final FakeAuctionServer auction, final int lastPrice) {
    this.driver.showsSniperState(new SniperSnapshot(auction.getItemId(), lastPrice, lastPrice, SniperState.WON));
  }

  public void stop() {
    if (this.driver != null) {
      this.driver.dispose();
    }
  }

  private static String[] arguments() {
    final String[] arguments = new String[3];
    arguments[0] = XMPP_HOSTNAME;
    arguments[1] = SNIPER_ID;
    arguments[2] = SNIPER_PASSWORD;
    return arguments;
  }

}
