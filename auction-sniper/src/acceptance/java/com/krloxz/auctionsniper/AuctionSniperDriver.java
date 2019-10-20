package com.krloxz.auctionsniper;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static org.hamcrest.Matchers.equalTo;

import javax.swing.table.JTableHeader;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import com.objogate.wl.swing.matcher.IterableComponentsMatcher;

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

  @SuppressWarnings("unchecked")
  public void showsSniperState(final SniperSnapshot snapshot) {
    final JTableDriver table = new JTableDriver(this);
    table.cellRenderedWithText(0, 0, equalTo(snapshot.itemId));
    table.cellRenderedWithText(0, 1, equalTo("" + snapshot.lastPrice));
    table.cellRenderedWithText(0, 2, equalTo("" + snapshot.lastBid));
    table.cellRenderedWithText(0, 3, equalTo(SnipersTableModel.textFor(snapshot.state)));
  }

  @SuppressWarnings("unchecked")
  public void hasColumnTitles() {
    final JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
    headers.hasHeaders(
        IterableComponentsMatcher.matching(
            withLabelText("Item"), withLabelText("Last Price"),
            withLabelText("Last Bid"), withLabelText("State")));
  }

}
