package com.krloxz.auctionsniper.domain;

/**
 * {@link UserRequestListener} that launches an {@link AuctionSniper} every time a request to join
 * an auction is received.
 *
 * @author Carlos Gomez
 *
 */
public class SniperLauncher implements UserRequestListener {

  private final AuctionHouse auctionHouse;
  private final SniperCollector collector;

  /**
   * Creates a new instance.
   *
   * @param auctionHouse
   *        auction house used to create new auctions
   * @param collector
   *        service that accepts new Snipers into the system
   */
  public SniperLauncher(final AuctionHouse auctionHouse, final SniperCollector collector) {
    this.auctionHouse = auctionHouse;
    this.collector = collector;
  }

  @Override
  public void joinAuction(final Item item) {
    final Auction auction = this.auctionHouse.auctionFor(item);
    auction.join();

    final AuctionSniper sniper = new AuctionSniper(item, auction);
    auction.addAuctionEventListener(sniper);
    this.collector.addSniper(sniper);
  }
}
