package com.krloxz.auctionsniper;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.krloxz.auctionsniper.AuctionEventListener.PriceSource;

/**
 * @author Carlos Gomez
 *
 */
class AuctionSniperTest {

  private final Auction auction = mock(Auction.class);
  private final SniperListener sniperListener = mock(SniperListener.class);
  private final AuctionSniper sniper = new AuctionSniper(this.auction, this.sniperListener);

  @Test
  void reportsLostIfAuctionClosesImmediately() {
    this.sniper.auctionClosed();
    verify(this.sniperListener).sniperLost();
  }

  @Test
  void reportsLostIfAuctionClosesWhenBidding() {
    this.sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    this.sniper.auctionClosed();

    final InOrder inOrder = inOrder(this.sniperListener);
    inOrder.verify(this.sniperListener).sniperBidding();
    inOrder.verify(this.sniperListener).sniperLost();
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
    final int price = 1001;
    final int increment = 25;

    this.sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

    verify(this.auction).bid(price + increment);
    verify(this.sniperListener, atLeastOnce()).sniperBidding();
  }

  @Test
  void reportsIsWinningWhenCurrentPriceComesFromSniper() {
    this.sniper.currentPrice(123, 45, PriceSource.FromSniper);
    verify(this.sniperListener).sniperWinning();
  }

  @Test
  void reportsWonIfAuctionClosesWhenWinning() {
    this.sniper.currentPrice(123, 45, PriceSource.FromSniper);
    this.sniper.auctionClosed();

    final InOrder inOrder = inOrder(this.sniperListener);
    inOrder.verify(this.sniperListener).sniperWinning();
    inOrder.verify(this.sniperListener).sniperWon();
    inOrder.verifyNoMoreInteractions();
  }

}
