package com.krloxz.auctionsniper;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

/**
 * @author Carlos Gomez
 */
public class AuctionSniperDriver extends JFrameDriver {

  @SuppressWarnings("unchecked")
  public AuctionSniperDriver(final int timeoutMillis) {
    super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
        new AWTEventQueueProber(timeoutMillis, 100));
  }

  @SuppressWarnings("unchecked")
  public void showsSniperStatus(final String statusText) {
    new JTableDriver(this).hasCell(withLabelText(equalTo(statusText)));
  }

}
