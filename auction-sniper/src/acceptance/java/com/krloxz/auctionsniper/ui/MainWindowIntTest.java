package com.krloxz.auctionsniper.ui;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.krloxz.auctionsniper.domain.Item;
import com.krloxz.auctionsniper.domain.SniperPortfolio;
import com.krloxz.auctionsniper.test.AuctionSniperDriver;
import com.objogate.wl.swing.probe.ValueMatcherProbe;

/**
 * Tests {@link MainWindow} in integration with the Swing framework.
 *
 * @author Carlos Gomez
 *
 */
class MainWindowIntTest {

  private final SniperPortfolio sniperPortfolio = new SniperPortfolio();
  private final MainWindow mainWindow = new MainWindow(this.sniperPortfolio);
  private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

  @Test
  void makesUserRequestWhenJoinButtonClicked() {
    final ValueMatcherProbe<Item> itemProbe =
        new ValueMatcherProbe<>(is(new Item("item-123", 789)), "item request");
    this.mainWindow.addUserRequestListener(item -> itemProbe.setReceivedValue(item));
    this.driver.startBiddingFor("item-123", 789);
    this.driver.check(itemProbe);
  }

  @AfterEach
  void disposeDriver() {
    this.driver.dispose();
  }

}
