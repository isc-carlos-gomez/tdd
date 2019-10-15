package com.krloxz.auctionsniper;

/**
 * @author Carlos Gomez
 */
public class ApplicationRunner {

  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";
  public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

  private static final String XMPP_HOSTNAME = "localhost";

  private AuctionSniperDriver driver;

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
    this.driver = new AuctionSniperDriver(1000);
    this.driver.showsSniperStatus(MainWindow.STATUS_JOINING);
  }

  public void hasShownSniperIsBidding() {
    this.driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
  }

  public void showsSniperHasLostAuction() {
    this.driver.showsSniperStatus(MainWindow.STATUS_LOST);
  }

  public void stop() {
    if (this.driver != null) {
      this.driver.dispose();
    }
  }

}
