package com.krloxz.auctionsniper.domain;

/**
 * Domain service that accepts new snipers into the system.
 *
 * @author Carlos Gomez
 *
 */
public interface SniperCollector {

  /**
   * Adds a new sniper into the system.
   * 
   * @param sniper
   *        the sniper to add
   */
  void addSniper(AuctionSniper sniper);

}
