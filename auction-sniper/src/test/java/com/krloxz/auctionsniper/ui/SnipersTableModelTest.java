package com.krloxz.auctionsniper.ui;

import static com.krloxz.auctionsniper.ui.CustomMatchers.hasRowWithSniper;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import com.krloxz.auctionsniper.domain.Auction;
import com.krloxz.auctionsniper.domain.AuctionSniper;
import com.krloxz.auctionsniper.domain.SniperSnapshot;
import com.krloxz.auctionsniper.ui.SnipersTableModel.Column;
import com.krloxz.auctionsniper.util.Defect;

/**
 * Unit tests {@link SnipersTableModel}.
 *
 * @author Carlos Gomez
 *
 */
class SnipersTableModelTest {

  private final TableModelListener listener = mock(TableModelListener.class);
  private final SnipersTableModel model = new SnipersTableModel();

  @BeforeEach
  void attachModelListener() {
    this.model.addTableModelListener(this.listener);
  }

  @Test
  void setsUpColumnHeadings() {
    for (final Column column : Column.values()) {
      assertThat(this.model.getColumnName(column.ordinal()), is(column.name));
    }
  }

  @Test
  void setsSniperValuesInColumns() {
    final AuctionSniper sniper = newSniper("item123");
    final SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);

    this.model.sniperAdded(sniper);
    this.model.sniperStateChanged(bidding);
    assertThat(this.model, hasRowWithSniper(0, bidding));

    verify(this.listener).tableChanged(withAnUpdateAtRow(0));
  }

  @Test
  void notifiesListenersWhenAddingASniper() {
    final AuctionSniper sniper = newSniper("item123");
    assertThat(this.model.getRowCount(), is(0));

    this.model.sniperAdded(sniper);
    assertThat(this.model, hasRowWithSniper(0, sniper.getSnapshot()));

    verify(this.listener).tableChanged(withAnInsertionAtRow(0));
  }

  @Test
  void addsSniperListenerWhenAddingASniper() {
    final AuctionSniper sniper = mock(AuctionSniper.class);
    this.model.sniperAdded(sniper);
    verify(sniper).addSniperListener(notNull());
  }

  @Test
  void holdsSnipersInAdditionOrder() {
    final AuctionSniper sniper1 = newSniper("item 1");
    final AuctionSniper sniper2 = newSniper("item 2");

    this.model.sniperAdded(sniper1);
    this.model.sniperAdded(sniper2);

    assertThat(this.model, hasRowWithSniper(0, sniper1.getSnapshot()));
    assertThat(this.model, hasRowWithSniper(1, sniper2.getSnapshot()));
  }

  @Test
  void updatesCorrectRowForSniper() {
    final AuctionSniper joiningSniper1 = newSniper("item 1");
    final AuctionSniper joiningSniper2 = newSniper("item 2");
    this.model.sniperAdded(joiningSniper1);
    this.model.sniperAdded(joiningSniper2);

    final SniperSnapshot biddingSniper2 = joiningSniper2.getSnapshot().bidding(200, 100);
    assertThat(this.model, hasRowWithSniper(1, joiningSniper2.getSnapshot()));

    this.model.sniperStateChanged(biddingSniper2);
    assertThat(this.model, hasRowWithSniper(1, biddingSniper2));
    verify(this.listener).tableChanged(withAnUpdateAtRow(1));
  }

  @Test
  void throwsDefectIfNoExistingSniperForAnUpdate() {
    final AuctionSniper joiningSniper1 = newSniper("item 1");
    final AuctionSniper joiningSniper2 = newSniper("item 2");
    this.model.sniperAdded(joiningSniper1);
    this.model.sniperAdded(joiningSniper2);

    assertThrows(Defect.class,
        () -> this.model.sniperStateChanged(SniperSnapshot.joining("other item")));
  }

  private AuctionSniper newSniper(final String itemId) {
    return new AuctionSniper(itemId, mock(Auction.class));
  }

  private TableModelEvent withAnUpdateAtRow(final int rowIndex) {
    return argThat(
        new ArgumentMatcher<TableModelEvent>() {

          @Override
          public boolean matches(final TableModelEvent event) {
            return event.getType() == TableModelEvent.UPDATE
                && event.getFirstRow() == rowIndex
                && event.getLastRow() == rowIndex;
          }

          @Override
          public String toString() {
            return "with an update at row " + rowIndex;
          }
        });
  }

  private TableModelEvent withAnInsertionAtRow(final int rowIndex) {
    return argThat(
        new ArgumentMatcher<TableModelEvent>() {

          @Override
          public boolean matches(final TableModelEvent event) {
            return event.getType() == TableModelEvent.INSERT
                && event.getFirstRow() == rowIndex
                && event.getLastRow() == rowIndex;
          }

          @Override
          public String toString() {
            return "with an insertion at row " + rowIndex;
          }
        });
  }

}
