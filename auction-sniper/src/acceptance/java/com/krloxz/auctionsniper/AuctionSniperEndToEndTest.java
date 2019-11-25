package com.krloxz.auctionsniper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.krloxz.auctionsniper.test.ApplicationRunner;
import com.krloxz.auctionsniper.test.FakeAuctionServer;

/**
 * Acceptance test cases for the Auction Sniper application.
 *
 * @author Carlos Gomez
 */
public class AuctionSniperEndToEndTest {

  private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
  private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
  private final ApplicationRunner application = new ApplicationRunner();

  @Test
  public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
    this.auction.startSellingItem();

    this.application.startBiddingIn(this.auction);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.announceClosed();
    this.application.showsSniperHasLostAuction(this.auction, 0, 0);
  }

  @Test
  void sniperMakesAHigherBidButLoses() throws Exception {
    this.auction.startSellingItem();

    this.application.startBiddingIn(this.auction);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1000, 98, "other bidder");
    this.application.hasShownSniperIsBidding(this.auction, 1000, 1098);

    this.auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.announceClosed();
    this.application.showsSniperHasLostAuction(this.auction, 1000, 1098);
  }

  @Test
  void sniperWinsAnAuctionByBiddingHigher() throws Exception {
    this.auction.startSellingItem();

    this.application.startBiddingIn(this.auction);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1000, 98, "other bidder");
    this.application.hasShownSniperIsBidding(this.auction, 1000, 1098);// last price, last bid

    this.auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
    this.application.hasShownSniperIsWinning(this.auction, 1098);// winning bid

    this.auction.announceClosed();
    this.application.showsSniperHasWonAuction(this.auction, 1098);// last price
  }

  @Test
  void sniperBidsForMultipleItems() throws Exception {
    this.auction.startSellingItem();
    this.auction2.startSellingItem();

    this.application.startBiddingIn(this.auction, this.auction2);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
    this.auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1000, 98, "other bidder");
    this.auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction2.reportPrice(500, 21, "other bidder");
    this.auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
    this.auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);
    this.application.hasShownSniperIsWinning(this.auction, 1098);
    this.application.hasShownSniperIsWinning(this.auction2, 521);

    this.auction.announceClosed();
    this.auction2.announceClosed();
    this.application.showsSniperHasWonAuction(this.auction, 1098);
    this.application.showsSniperHasWonAuction(this.auction2, 521);
  }

  @Test
  void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
    this.auction.startSellingItem();

    this.application.startBiddingWithStopPrice(this.auction, 1100);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1000, 98, "other bidder");
    this.application.hasShownSniperIsBidding(this.auction, 1000, 1098);

    this.auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(1197, 10, "third party");
    this.application.hasShownSniperIsLosing(this.auction, 1197, 1098);

    this.auction.reportPrice(1207, 10, "fourth party");
    this.application.hasShownSniperIsLosing(this.auction, 1207, 1098);

    this.auction.announceClosed();
    this.application.showsSniperHasLostAuction(this.auction, 1207, 1098);
  }

  @Test
  void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception {
    final String brokenMessage = "a broken message";
    this.auction.startSellingItem();
    this.auction2.startSellingItem();

    this.application.startBiddingIn(this.auction, this.auction2);
    this.auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.reportPrice(500, 20, "other bidder");
    this.auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);

    this.auction.sendInvalidMessageContaining(brokenMessage);
    this.application.hasShownSniperFailed(this.auction);

    this.auction.reportPrice(520, 21, "other bidder");
    waitForAnotherAuctionEvent();
    this.application.reportsInvalidMessage(this.auction, brokenMessage);
    this.application.hasShownSniperFailed(this.auction);
  }

  private void waitForAnotherAuctionEvent() throws Exception {
    this.auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
    this.auction2.reportPrice(600, 6, "other bidder");
    this.application.hasShownSniperIsBidding(this.auction2, 600, 606);
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
