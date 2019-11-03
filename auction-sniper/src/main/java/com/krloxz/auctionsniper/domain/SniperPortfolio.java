package com.krloxz.auctionsniper.domain;

import java.util.ArrayList;
import java.util.List;

import com.krloxz.auctionsniper.util.Announcer;

/**
 * A portfolio of snipers useful to keep a record of all the snipers added to the system and fire a
 * notification event every time a sniper is added.
 *
 * @author Carlos Gomez
 *
 */
public class SniperPortfolio implements SniperCollector {

  private final Announcer<PortfolioListener> listeners = Announcer.to(PortfolioListener.class);
  private final List<AuctionSniper> snipers = new ArrayList<>();

  @Override
  public void addSniper(final AuctionSniper sniper) {
    this.snipers.add(sniper);
    this.listeners.announce().sniperAdded(sniper);
  }

  /**
   * Adds a listener for events of this portfolio.
   *
   * @param listener
   *        the listener to add
   */
  public void addPortfolioListener(final PortfolioListener listener) {
    this.listeners.addListener(listener);
  }

}
