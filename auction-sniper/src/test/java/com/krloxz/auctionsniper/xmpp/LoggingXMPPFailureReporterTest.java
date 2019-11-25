package com.krloxz.auctionsniper.xmpp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

/**
 * @author Carlos Gomez
 *
 */
public class LoggingXMPPFailureReporterTest {

  final Logger logger = mock(Logger.class);
  final LoggingXMPPFailureReporter reporter = new LoggingXMPPFailureReporter(this.logger);

  @AfterAll
  static void resetLogging() {
    LogManager.getLogManager().reset();
  }

  @Test
  void writesMessageTranslationFailureToLog() {
    this.reporter.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
    verify(this.logger).severe("<auction id> "
        + "Could not translate message \"bad message\" "
        + "because \"java.lang.Exception: bad\"");
  }

}
