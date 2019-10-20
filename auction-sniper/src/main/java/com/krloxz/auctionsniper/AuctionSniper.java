package com.krloxz.auctionsniper;

/**
 * @author Carlos Gomez
 *
 */
public class AuctionSniper implements AuctionEventListener {

  private final SniperListener sniperListener;
  private final Auction auction;
  private SniperSnapshot snapshot;

  public AuctionSniper(final Auction auction, final SniperListener sniperListener, final String itemId) {
    this.auction = auction;
    this.sniperListener = sniperListener;
    this.snapshot = SniperSnapshot.joining(itemId);
    notifyChange();
  }

  @Override
  public void auctionClosed() {
    this.snapshot = this.snapshot.closed();
    notifyChange();
  }

  @Override
  public void currentPrice(final int price, final int increment, final PriceSource priceSource) {
    switch (priceSource) {
      case FromSniper:
        this.snapshot = this.snapshot.winning(price);
        break;
      case FromOtherBidder:
        final int bid = price + increment;
        this.auction.bid(bid);
        this.snapshot = this.snapshot.bidding(price, bid);
        break;
    }
    notifyChange();
  }

  private void notifyChange() {
    this.sniperListener.sniperStateChanged(this.snapshot);
  }

}
