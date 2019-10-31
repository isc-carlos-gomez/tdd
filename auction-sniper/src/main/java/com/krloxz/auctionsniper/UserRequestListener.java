package com.krloxz.auctionsniper;

import java.util.EventListener;

/**
 * @author Carlos Gomez
 *
 */
public interface UserRequestListener extends EventListener {

  void joinAuction(String itemId);

}
