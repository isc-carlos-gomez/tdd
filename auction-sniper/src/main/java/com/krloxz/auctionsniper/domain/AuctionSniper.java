package com.krloxz.auctionsniper.domain;

import com.krloxz.auctionsniper.util.Announcer;

/**
 * @author Carlos Gomez
 *
 */
public class AuctionSniper implements AuctionEventListener {

  private final String itemId;
  private final Auction auction;
  private SniperSnapshot snapshot;
  private final Announcer<SniperListener> sniperListener;

  public AuctionSniper(final String itemId, final Auction auction) {
    this.itemId = itemId;
    this.auction = auction;
    this.snapshot = SniperSnapshot.joining(itemId);
    this.sniperListener = new Announcer<>(SniperListener.class);
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

  public SniperSnapshot getSnapshot() {
    return this.snapshot;
  }

  public void addSniperListener(final SniperListener sniperListener) {
    this.sniperListener.addListener(sniperListener);
  }

  protected boolean isForItem(final String itemId) {
    return this.itemId.equals(itemId);
  }

  private void notifyChange() {
    this.sniperListener.announce().sniperStateChanged(this.snapshot);
  }

}
