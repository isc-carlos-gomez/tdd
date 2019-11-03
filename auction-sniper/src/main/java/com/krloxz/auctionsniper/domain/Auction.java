package com.krloxz.auctionsniper.domain;

/**
 * @author Carlos Gomez
 *
 */
public interface Auction {

  void bid(int amount);

  void join();

  void addAuctionEventListener(AuctionEventListener listener);

}
