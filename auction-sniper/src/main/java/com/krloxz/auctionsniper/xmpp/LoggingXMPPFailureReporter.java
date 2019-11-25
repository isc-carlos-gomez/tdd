package com.krloxz.auctionsniper.xmpp;

import java.util.logging.Logger;

/**
 * @author Carlos Gomez
 *
 */
public class LoggingXMPPFailureReporter implements XMPPFailureReporter {

  private final Logger logger;

  public LoggingXMPPFailureReporter(final Logger logger) {
    this.logger = logger;
  }

  @Override
  public void cannotTranslateMessage(final String auctionId, final String failedMessage, final Exception exception) {
    this.logger.severe(
        String.format(
            "<%s> Could not translate message \"%s\" because \"%s: %s\"",
            auctionId, failedMessage, exception.getClass().getName(), exception.getMessage()));
  }

}
