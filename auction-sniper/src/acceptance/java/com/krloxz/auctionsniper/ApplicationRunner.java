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
  private String itemId;

  public void startBiddingIn(final FakeAuctionServer auction) {
    final Thread thread = new Thread("Test Application") {

      @Override
      public void run() {
        try {
          Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
        } catch (final Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    this.itemId = auction.getItemId();

    this.driver = new AuctionSniperDriver(1000);
    this.driver.hasTitle(MainWindow.APPLICATION_TITLE);
    this.driver.hasColumnTitles();
    this.driver.showsSniperState(SniperSnapshot.joining(this.itemId));
  }

  public void hasShownSniperIsBidding(final int lastPrice, final int lastBid) {
    this.driver.showsSniperState(new SniperSnapshot(this.itemId, lastPrice, lastBid, SniperState.BIDDING));
  }

  public void showsSniperHasLostAuction(final int lastPrice, final int lastBid) {
    this.driver.showsSniperState(new SniperSnapshot(this.itemId, lastPrice, lastBid, SniperState.LOST));
  }

  public void hasShownSniperIsWinning(final int winningBid) {
    this.driver.showsSniperState(new SniperSnapshot(this.itemId, winningBid, winningBid, SniperState.WINNING));
  }

  public void showsSniperHasWonAuction(final int lastPrice) {
    this.driver.showsSniperState(new SniperSnapshot(this.itemId, lastPrice, lastPrice, SniperState.WON));
  }

  public void stop() {
    if (this.driver != null) {
      this.driver.dispose();
    }
  }

}
