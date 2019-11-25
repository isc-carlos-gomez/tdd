package com.krloxz.auctionsniper.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

import com.krloxz.auctionsniper.xmpp.XMPPAuctionHouse;

/**
 * @author Carlos Gomez
 *
 */
public class AuctionLogDriver {

  private final File logFile = new File(XMPPAuctionHouse.LOG_FILE_NAME);

  public void hasEntry(final Matcher<String> matcher) throws IOException {
    assertThat(FileUtils.readFileToString(this.logFile, StandardCharsets.UTF_8), matcher);
  }

  public void clearLog() {
    this.logFile.delete();
    LogManager.getLogManager().reset();
  }
}
