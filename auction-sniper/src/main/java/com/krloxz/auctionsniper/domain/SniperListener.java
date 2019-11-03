package com.krloxz.auctionsniper.domain;

import java.util.EventListener;

/**
 * @author Carlos Gomez
 *
 */
public interface SniperListener extends EventListener {

  /**
   * @param sniperSnapshot
   */
  void sniperStateChanged(SniperSnapshot sniperSnapshot);

}
