package com.krloxz.auctionsniper.domain;

import java.util.EventListener;

/**
 * @author Carlos Gomez
 *
 */
public interface UserRequestListener extends EventListener {

  void joinAuction(String itemId);

}
