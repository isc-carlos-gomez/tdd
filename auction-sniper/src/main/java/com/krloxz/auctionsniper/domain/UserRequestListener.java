package com.krloxz.auctionsniper.domain;

import java.util.EventListener;

/**
 * {@link EventListener} to listen for user requests.
 *
 * @author Carlos Gomez
 *
 */
public interface UserRequestListener extends EventListener {

  /**
   * Requests to join to the auction of the given item.
   * 
   * @param itemId
   *        item identifier
   */
  void joinAuction(String itemId);

}
