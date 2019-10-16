package com.krloxz.auctionsniper;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

/**
 * @author Carlos Gomez
 *
 */
class AuctionSniperTest {

  private final Auction auction = mock(Auction.class);
  private final SniperListener sniperListener = mock(SniperListener.class);
  private final AuctionSniper sniper = new AuctionSniper(this.auction, this.sniperListener);

  @Test
  void reportsLostWhenAuctionCloses() {
    this.sniper.auctionClosed();
    verify(this.sniperListener).sniperLost();
  }

  @Test
  void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
    final int price = 1001;
    final int increment = 25;

    this.sniper.currentPrice(price, increment);

    verify(this.auction).bid(price + increment);
    verify(this.sniperListener, atLeastOnce()).sniperBidding();
  }

}
