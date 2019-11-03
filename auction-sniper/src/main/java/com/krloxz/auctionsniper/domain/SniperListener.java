package com.krloxz.auctionsniper.domain;

/**
 * @author Carlos Gomez
 *
 */
public interface SniperListener {

  /**
   * @param sniperSnapshot
   */
  void sniperStateChanged(SniperSnapshot sniperSnapshot);

}
