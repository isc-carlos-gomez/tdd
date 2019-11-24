package com.krloxz.auctionsniper.domain;

/**
 * A factory that arranges auctions.
 *
 * @author Carlos Gomez
 *
 */
public interface AuctionHouse {

  /**
   * Creates an auction for the given item.
   *
   * @param item
   *        the item
   * @return an auction for the given item
   */
  Auction auctionFor(Item item);

}
