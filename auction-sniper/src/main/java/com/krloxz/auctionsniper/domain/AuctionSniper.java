package com.krloxz.auctionsniper.domain;

import com.krloxz.auctionsniper.util.Announcer;

/**
 * @author Carlos Gomez
 *
 */
public class AuctionSniper implements AuctionEventListener {

  private final Item item;
  private final Auction auction;
  private SniperSnapshot snapshot;
  private final Announcer<SniperListener> sniperListener;

  public AuctionSniper(final Item item, final Auction auction) {
    this.item = item;
    this.auction = auction;
    this.snapshot = SniperSnapshot.joining(item.identifier);
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
        if (this.item.allowsBid(bid)) {
          this.auction.bid(bid);
          this.snapshot = this.snapshot.bidding(price, bid);
        } else {
          this.snapshot = this.snapshot.losing(price);
        }
        break;
    }
    notifyChange();
  }

  @Override
  public void auctionFailed() {
    this.snapshot = this.snapshot.failed();
    this.sniperListener.announce().sniperStateChanged(this.snapshot);
  }

  public SniperSnapshot getSnapshot() {
    return this.snapshot;
  }

  public void addSniperListener(final SniperListener sniperListener) {
    this.sniperListener.addListener(sniperListener);
  }

  protected boolean isForItem(final String itemId) {
    return this.item.identifier.equals(itemId);
  }

  private void notifyChange() {
    this.sniperListener.announce().sniperStateChanged(this.snapshot);
  }

}
