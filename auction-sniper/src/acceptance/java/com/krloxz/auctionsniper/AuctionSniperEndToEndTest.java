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
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
    this.auction.announceClosed();
    this.application.showsSniperHasLostAuction();
  }

  @Test
  void sniperMakesAHigherBidButLoses() throws Exception {
    this.auction.startSellingItem();

    this.application.startBiddingIn(this.auction);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1000, 98, "other bidder");
    this.application.hasShownSniperIsBidding();

    this.auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.announceClosed();
    this.application.showsSniperHasLostAuction();
  }

  @Test
  void sniperWinsAnAuctionByBiddingHigher() throws Exception {
    this.auction.startSellingItem();

    this.application.startBiddingIn(this.auction);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1000, 98, "other bidder");
    this.application.hasShownSniperIsBidding();

    this.auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
    this.application.hasShownSniperIsWinning();

    this.auction.announceClosed();
    this.application.showsSniperHasWonAuction();
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
