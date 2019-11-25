package com.krloxz.auctionsniper.xmpp;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.krloxz.auctionsniper.domain.Auction;
import com.krloxz.auctionsniper.domain.AuctionEventListener;
import com.krloxz.auctionsniper.domain.Item;
import com.krloxz.auctionsniper.test.ApplicationRunner;
import com.krloxz.auctionsniper.test.FakeAuctionServer;

/**
 * Tests {@link XMPPAuctionHouse} in integration with the XMPP Server.
 *
 * @author Carlos Gomez
 *
 */
class XMPPAuctionHouseIntTest {

  private FakeAuctionServer auctionServer;

  @BeforeEach
  void startAuction() throws XMPPException {
    this.auctionServer = new FakeAuctionServer("item-54321");
    this.auctionServer.startSellingItem();
  }

  @Test
  void receivesEventsFromAuctionServerAfterJoining() throws Exception {
    final CountDownLatch auctionWasClosed = new CountDownLatch(1);

    final XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(
        ApplicationRunner.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
    final Auction auction = auctionHouse.auctionFor(new Item(this.auctionServer.getItemId(), 789));
    auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

    auction.join();
    this.auctionServer.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
    this.auctionServer.announceClosed();

    assertThat("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
  }

  private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
    return new AuctionEventListener() {
      @Override
      public void auctionClosed() {
        auctionWasClosed.countDown();
      }

      @Override
      public void currentPrice(final int price, final int increment, final PriceSource priceSource) {
        // not implemented
      }

      @Override
      public void auctionFailed() {
        // not implemented
      }
    };
  }

}
