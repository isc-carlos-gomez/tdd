package com.krloxz.auctionsniper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Acceptance test cases for the Auction Sniper application.
 *
 * @author Carlos Gomez
 */
public class AuctionSniperEndToEndTest {

  private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
  private final ApplicationRunner application = new ApplicationRunner();

  @Test
  public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
    this.auction.startSellingItem();
    this.application.startBiddingIn(this.auction);
    this.auction.hasReceivedJoinRequestFromSniper();
    this.auction.announceClosed();
    this.application.showsSniperHasLostAuction();
  }

  @AfterEach
  public void stopAuction() {
    this.auction.stop();
  }

  @AfterEach
  public void stopApplication() {
    this.application.stop();
  }

}
