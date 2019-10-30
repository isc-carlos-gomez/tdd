package com.krloxz.auctionsniper;

import static com.krloxz.auctionsniper.CustomMatchers.hasRowWithSniper;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import com.krloxz.auctionsniper.SnipersTableModel.Column;

/**
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
    final SniperSnapshot joining = SniperSnapshot.joining("item123");
    final SniperSnapshot bidding = joining.bidding(555, 666);

    this.model.addSniper(bidding);
    this.model.sniperStateChanged(bidding);
    assertThat(this.model, hasRowWithSniper(0, bidding));

    verify(this.listener).tableChanged(withAnUpdateAtRow(0));
  }

  @Test
  void notifiesListenersWhenAddingASniper() {
    final SniperSnapshot joining = SniperSnapshot.joining("item123");
    assertThat(this.model.getRowCount(), is(0));

    this.model.addSniper(joining);
    assertThat(this.model, hasRowWithSniper(0, joining));

    verify(this.listener).tableChanged(withAnInsertionAtRow(0));
  }

  @Test
  void holdsSnipersInAdditionOrder() {
    final SniperSnapshot sniper1 = SniperSnapshot.joining("item 1");
    final SniperSnapshot sniper2 = SniperSnapshot.joining("item 2");

    this.model.addSniper(sniper1);
    this.model.addSniper(sniper2);

    assertThat(this.model, hasRowWithSniper(0, sniper1));
    assertThat(this.model, hasRowWithSniper(1, sniper2));
  }

  @Test
  void updatesCorrectRowForSniper() {
    final SniperSnapshot joiningSniper1 = SniperSnapshot.joining("item 1");
    final SniperSnapshot joiningSniper2 = SniperSnapshot.joining("item 2");
    this.model.addSniper(joiningSniper1);
    this.model.addSniper(joiningSniper2);

    final SniperSnapshot biddingSniper2 = joiningSniper2.bidding(200, 100);
    assertThat(this.model, hasRowWithSniper(1, joiningSniper2));

    this.model.sniperStateChanged(biddingSniper2);
    assertThat(this.model, hasRowWithSniper(1, biddingSniper2));
    verify(this.listener).tableChanged(withAnUpdateAtRow(1));
  }

  @Test
  void throwsDefectIfNoExistingSniperForAnUpdate() {
    final SniperSnapshot joiningSniper1 = SniperSnapshot.joining("item 1");
    final SniperSnapshot joiningSniper2 = SniperSnapshot.joining("item 2");
    this.model.addSniper(joiningSniper1);
    this.model.addSniper(joiningSniper2);

    assertThrows(Defect.class,
        () -> this.model.sniperStateChanged(SniperSnapshot.joining("other item")));
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
