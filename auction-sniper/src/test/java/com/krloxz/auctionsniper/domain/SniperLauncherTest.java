package com.krloxz.auctionsniper.domain;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

/**
 * Unit tests {@link SniperLauncher}.
 *
 * @author Carlos Gomez
 *
 */
class SniperLauncherTest {

  @Test
  void addsNewSniperToCollectorAndThenJoinsAuction() {
    final Item item = new Item("item 123", 789);

    final AuctionHouse auctionHouse = mock(AuctionHouse.class);
    final Auction auction = mock(Auction.class);
    when(auctionHouse.auctionFor(item))
        .thenReturn(auction);

    final SniperCollector snipers = mock(SniperCollector.class);

    final SniperLauncher launcher = new SniperLauncher(auctionHouse, snipers);
    launcher.joinAuction(item);

    verify(snipers).addSniper(withItemId(item.identifier));
  }

  private AuctionSniper withItemId(final String itemId) {
    return argThat(
        new ArgumentMatcher<AuctionSniper>() {

          @Override
          public boolean matches(final AuctionSniper sniper) {
            return sniper.isForItem(itemId);
          }

          @Override
          public String toString() {
            return "a sniper for the item id " + itemId;
          }
        });
  }

}
