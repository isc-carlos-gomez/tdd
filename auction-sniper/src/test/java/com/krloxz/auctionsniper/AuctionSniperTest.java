package com.krloxz.auctionsniper;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import com.krloxz.auctionsniper.AuctionEventListener.PriceSource;
import com.krloxz.auctionsniper.SniperSnapshot.SniperState;

/**
 * @author Carlos Gomez
 *
 */
class AuctionSniperTest {

  private static final String ITEM_ID = "itemId";
  private final Auction auction = mock(Auction.class);
  private final SniperListener sniperListener = mock(SniperListener.class);
  private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, this.auction, this.sniperListener);

  @Test
  void reportsLostIfAuctionClosesImmediately() {
    this.sniper.auctionClosed();
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOST));
  }

  @Test
  void reportsLostIfAuctionClosesWhenBidding() {
    this.sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    this.sniper.auctionClosed();

    final InOrder inOrder = inOrder(this.sniperListener);
    inOrder.verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
    inOrder.verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOST));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
    final int price = 1001;
    final int increment = 25;
    final int bid = price + increment;

    this.sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

    verify(this.auction).bid(bid);
    verify(this.sniperListener, atLeastOnce()).sniperStateChanged(
        new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
  }

  @Test
  void reportsIsWinningWhenCurrentPriceComesFromSniper() {
    this.sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
    this.sniper.currentPrice(135, 45, PriceSource.FromSniper);
    verify(this.sniperListener).sniperStateChanged(
        new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
  }

  @Test
  void reportsWonIfAuctionClosesWhenWinning() {
    this.sniper.currentPrice(123, 45, PriceSource.FromSniper);
    this.sniper.auctionClosed();

    final InOrder inOrder = inOrder(this.sniperListener);
    inOrder.verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.WON));
    inOrder.verifyNoMoreInteractions();
  }

  @Test
  void skipsBiddingWhenCurrentPriceComesFromSniper() {
    this.sniper.currentPrice(123, 45, PriceSource.FromSniper);
    verifyNoInteractions(this.auction);
  }

  @Test
  void reportsBiddingWhenCurrentPriceComesFromOtherBidder() {
    this.sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
  }

  @Test
  void reportsBiddingWhenWinningAndCurrentPriceComesFromOtherBidder() {
    this.sniper.currentPrice(123, 45, PriceSource.FromSniper);
    this.sniper.currentPrice(168, 45, PriceSource.FromOtherBidder);

    final InOrder inOrder = inOrder(this.sniperListener);
    inOrder.verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
    inOrder.verifyNoMoreInteractions();
  }

  private SniperSnapshot aSniperThatIs(final SniperState expected) {
    return argThat(new ArgumentMatcher<SniperSnapshot>() {

      @Override
      public boolean matches(final SniperSnapshot actual) {
        return expected.equals(actual.state);
      }

      @Override
      public String toString() {
        return "a sniper that is " + expected;
      }
    });
  }

}
