package com.krloxz.auctionsniper;

import com.krloxz.auctionsniper.SniperSnapshot.SniperState;

/**
 * @author Carlos Gomez
 */
public class ApplicationRunner {

  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

  private static final String XMPP_HOSTNAME = "localhost";
  private AuctionSniperDriver driver;

  public void startBiddingIn(final FakeAuctionServer... auctions) {
    final Thread thread = new Thread("Test Application") {

      @Override
      public void run() {
        try {
          Main.main(arguments(auctions));
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

    for (final FakeAuctionServer auction : auctions) {
      this.driver.showsSniperState(SniperSnapshot.joining(auction.getItemId()));
    }
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

  private static String[] arguments(final FakeAuctionServer... auctions) {
    final String[] arguments = new String[auctions.length + 3];
    arguments[0] = XMPP_HOSTNAME;
    arguments[1] = SNIPER_ID;
    arguments[2] = SNIPER_PASSWORD;
    for (int i = 0; i < auctions.length; i++) {
      arguments[i + 3] = auctions[i].getItemId();
    }
    return arguments;
  }

}
