package com.krloxz.auctionsniper.test;

import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;

import com.krloxz.auctionsniper.domain.SniperSnapshot;
import com.krloxz.auctionsniper.ui.MainWindow;
import com.krloxz.auctionsniper.ui.SnipersTableModel;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JButtonDriver;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.driver.JTextFieldDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import com.objogate.wl.swing.matcher.IterableComponentsMatcher;

/**
 * @author Carlos Gomez
 */
public class AuctionSniperDriver extends JFrameDriver {

  static {
    System.setProperty("com.objogate.wl.keyboard", "US");
  }

  @SuppressWarnings("unchecked")
  public AuctionSniperDriver(final int timeoutMillis) {
    super(new GesturePerformer(), JFrameDriver.topLevelFrame(named(MainWindow.MAIN_WINDOW_NAME), showingOnScreen()),
        new AWTEventQueueProber(timeoutMillis, 100));
  }

  @SuppressWarnings("unchecked")
  public void showsSniperState(final SniperSnapshot snapshot) {
    final JTableDriver table = new JTableDriver(this);
    table.hasRow(
        IterableComponentsMatcher.matching(
            withLabelText(snapshot.itemId),
            withLabelText(snapshot.lastPrice + ""),
            withLabelText(snapshot.lastBid + ""),
            withLabelText(SnipersTableModel.textFor(snapshot.state))));
  }

  @SuppressWarnings("unchecked")
  public void hasColumnTitles() {
    final JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
    headers.hasHeaders(
        IterableComponentsMatcher.matching(
            withLabelText("Item"), withLabelText("Last Price"),
            withLabelText("Last Bid"), withLabelText("State")));
  }

  public void startBiddingFor(final String itemId) {
    itemIdField().replaceAllText(itemId);
    bidButton().click();
  }

  @SuppressWarnings("unchecked")
  private JTextFieldDriver itemIdField() {
    final JTextFieldDriver newItemId =
        new JTextFieldDriver(this, JTextField.class, named(MainWindow.NEW_ITEM_ID_NAME));
    newItemId.focusWithMouse();
    return newItemId;
  }

  @SuppressWarnings("unchecked")
  private JButtonDriver bidButton() {
    return new JButtonDriver(this, JButton.class, named(MainWindow.JOIN_BUTTON_NAME));
  }

}
