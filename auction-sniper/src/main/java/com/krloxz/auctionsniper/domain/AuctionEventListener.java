package com.krloxz.auctionsniper.domain;

public interface AuctionEventListener {

  void auctionClosed();

  void currentPrice(int price, int increment, PriceSource priceSource);

  enum PriceSource {
    FromSniper, FromOtherBidder;
  }

}
