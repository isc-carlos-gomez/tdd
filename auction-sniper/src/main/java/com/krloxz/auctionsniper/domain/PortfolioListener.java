package com.krloxz.auctionsniper.domain;

import java.util.EventListener;

/**
 * Listener of portfolio events.
 * 
 * @author Carlos Gomez
 *
 */
public interface PortfolioListener extends EventListener {

  /**
   * Called when a sniper has been added to the portfolio.
   * 
   * @param sniper
   *        the sniper
   */
  void sniperAdded(AuctionSniper sniper);

}
