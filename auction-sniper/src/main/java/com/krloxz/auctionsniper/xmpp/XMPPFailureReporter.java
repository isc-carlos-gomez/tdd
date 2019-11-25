package com.krloxz.auctionsniper.xmpp;

/**
 * @author Carlos Gomez
 *
 */
public interface XMPPFailureReporter {

  void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception);

}
