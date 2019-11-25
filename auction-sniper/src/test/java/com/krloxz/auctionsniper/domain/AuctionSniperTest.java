package com.krloxz.auctionsniper.domain;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import com.krloxz.auctionsniper.domain.AuctionEventListener.PriceSource;
import com.krloxz.auctionsniper.domain.SniperSnapshot.SniperState;

/**
 * Unit tests {@link AuctionSniper}.
 *
 * @author Carlos Gomez
 *
 */
class AuctionSniperTest {

  private static final String ITEM_ID = "itemId";
  private Auction auction;
  private SniperListener sniperListener;
  private AuctionSniper sniper;

  @BeforeEach
  void setUp() {
    this.auction = mock(Auction.class);
    this.sniperListener = mock(SniperListener.class);
    this.sniper = new AuctionSniper(new Item(ITEM_ID, 1100), this.auction);
    this.sniper.addSniperListener(this.sniperListener);
  }

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
    verify(this.sniperListener, atLeastOnce()).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
  }

  @Test
  void reportsIsWinningWhenCurrentPriceComesFromSniper() {
    this.sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
    this.sniper.currentPrice(135, 45, PriceSource.FromSniper);
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.WINNING));
  }

  @Test
  void reportsWonIfAuctionClosesWhenWinning() {
    this.sniper.currentPrice(123, 45, PriceSource.FromSniper);
    this.sniper.auctionClosed();

    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.WON));
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

    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
  }

  @Test
  void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
    this.sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    this.sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);

    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOSING));
  }

  @Test
  void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
    this.sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOSING));
  }

  @Test
  void reportsLostIfAuctionClosesWhenLosing() {
    this.sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    this.sniper.auctionClosed();

    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOSING));
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOST));
  }

  @Test
  void continuesToBeLosingOnceStopPriceHasBeenReached() {
    this.sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    this.sniper.currentPrice(3456, 25, PriceSource.FromOtherBidder);

    verify(this.sniperListener, times(2)).sniperStateChanged(aSniperThatIs(SniperState.LOSING));
  }

  @Test
  void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
    this.sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
    this.sniper.currentPrice(135, 45, PriceSource.FromSniper);
    this.sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);

    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.WINNING));
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.LOSING));
  }

  @Test
  void reportsFailedIfAuctionFailsWhenBidding() {
    this.sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
    this.sniper.auctionFailed();

    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.BIDDING));
    verify(this.sniperListener).sniperStateChanged(aSniperThatIs(SniperState.FAILED));
  }

  private SniperSnapshot aSniperThatIs(
      final SniperState expected) {
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
