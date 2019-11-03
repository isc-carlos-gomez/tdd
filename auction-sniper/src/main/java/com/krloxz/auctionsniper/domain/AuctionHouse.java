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
   * @param itemId
   *        item identifier
   * @return an auction for the given item
   */
  Auction auctionFor(String itemId);

}
