package com.krloxz.auctionsniper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;

import com.krloxz.auctionsniper.SniperSnapshot.SniperState;
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
    this.model.sniperStateChanged(new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));

    assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
    assertColumnEquals(Column.LAST_PRICE, 555);
    assertColumnEquals(Column.LAST_BID, 666);
    assertColumnEquals(Column.SNIPER_STATE, MainWindow.STATUS_BIDDING);

    verify(this.listener).tableChanged(argThat(isRowChangedEvent()));
  }

  private void assertColumnEquals(final Column column, final Object expected) {
    final int rowIndex = 0;
    final int columnIndex = column.ordinal();

    assertThat(this.model.getValueAt(rowIndex, columnIndex), is(expected));
  }

  private ArgumentMatcher<TableModelEvent> isRowChangedEvent() {
    return new HamcrestArgumentMatcher<>(samePropertyValuesAs(new TableModelEvent(this.model, 0)));
  }

}
