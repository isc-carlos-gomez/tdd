package com.krloxz.auctionsniper;

/**
 * @author Carlos Gomez
 */
public class ApplicationRunner {

  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";

  private static final String XMPP_HOSTNAME = "localhost";
  private static final String STATUS_JOINING = "Joining";
  private static final String STATUS_LOST = "Lost";

  private AuctionSniperDriver driver;

  public void startBiddingIn(final FakeAuctionServer auction) {
    Thread thread = new Thread("Test Application") {

      @Override
      public void run() {
        try {
          Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    this.driver = new AuctionSniperDriver(1000);
    this.driver.showsSniperStatus(STATUS_JOINING);
  }

  public void showsSniperHasLostAuction() {
    this.driver.showsSniperStatus(STATUS_LOST);
  }

  public void stop() {
    if (this.driver != null) {
      this.driver.dispose();
    }
  }

}
