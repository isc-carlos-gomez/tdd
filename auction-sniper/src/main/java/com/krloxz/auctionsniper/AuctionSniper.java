package com.krloxz.auctionsniper;

/**
 * @author Carlos Gomez
 *
 */
public class AuctionSniper implements AuctionEventListener {

  private final SniperListener sniperListener;
  private final Auction auction;
  private boolean isWinning = false;

  public AuctionSniper(final Auction auction, final SniperListener sniperListener) {
    this.auction = auction;
    this.sniperListener = sniperListener;
  }

  @Override
  public void auctionClosed() {
    if (this.isWinning) {
      this.sniperListener.sniperWon();
    } else {
      this.sniperListener.sniperLost();
    }
  }

  @Override
  public void currentPrice(final int price, final int increment, final PriceSource priceSource) {
    this.isWinning = priceSource == PriceSource.FromSniper;
    if (this.isWinning) {
      this.sniperListener.sniperWinning();
    } else {
      this.auction.bid(price + increment);
      this.sniperListener.sniperBidding();
    }
  }

}
