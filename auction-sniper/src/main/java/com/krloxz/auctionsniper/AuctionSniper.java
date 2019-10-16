package com.krloxz.auctionsniper;

/**
 * @author Carlos Gomez
 *
 */
public class AuctionSniper implements AuctionEventListener {

  private final SniperListener sniperListener;
  private final Auction auction;

  public AuctionSniper(final Auction auction, final SniperListener sniperListener) {
    this.auction = auction;
    this.sniperListener = sniperListener;
  }

  @Override
  public void auctionClosed() {
    this.sniperListener.sniperLost();
  }

  @Override
  public void currentPrice(final int price, final int increment) {
    this.auction.bid(price + increment);
    this.sniperListener.sniperBidding();
  }

}
