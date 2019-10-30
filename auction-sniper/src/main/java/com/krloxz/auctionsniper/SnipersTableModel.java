package com.krloxz.auctionsniper;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.krloxz.auctionsniper.SniperSnapshot.SniperState;

/**
 * @author Carlos Gomez
 *
 */
public class SnipersTableModel extends AbstractTableModel implements SniperListener {

  private static final long serialVersionUID = 2685916085280519480L;
  private static final String[] STATUS_TEXT = {
      "Joining", "Bidding", "Winning", "Lost", "Won"
  };
  private final List<SniperSnapshot> snapshots = new ArrayList<>();

  public void addSniper(final SniperSnapshot snapshot) {
    this.snapshots.add(snapshot);
    fireTableRowsInserted(this.snapshots.size() - 1, this.snapshots.size() - 1);
  }

  @Override
  public String getColumnName(final int column) {
    return Column.at(column).name;
  }

  @Override
  public int getColumnCount() {
    return Column.values().length;
  }

  @Override
  public int getRowCount() {
    return this.snapshots.size();
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    return Column.at(columnIndex).valueIn(this.snapshots.get(rowIndex));
  }

  @Override
  public void sniperStateChanged(final SniperSnapshot newSnapshot) {
    final int row = rowMatching(newSnapshot);
    this.snapshots.set(row, newSnapshot);
    fireTableRowsUpdated(row, row);
  }

  private int rowMatching(final SniperSnapshot newSnapshot) {
    for (int i = 0; i < this.snapshots.size(); i++) {
      if (newSnapshot.isForSameItemAs(this.snapshots.get(i))) {
        return i;
      }
    }
    throw new Defect("Cannot find match for " + newSnapshot);
  }

  public static String textFor(final SniperState state) {
    return STATUS_TEXT[state.ordinal()];
  }

  public enum Column {
    ITEM_IDENTIFIER("Item") {
      @Override
      public Object valueIn(final SniperSnapshot snapshot) {
        return snapshot.itemId;
      }
    },
    LAST_PRICE("Last Price") {
      @Override
      public Object valueIn(final SniperSnapshot snapshot) {
        return snapshot.lastPrice;
      }
    },
    LAST_BID("Last Bid") {
      @Override
      public Object valueIn(final SniperSnapshot snapshot) {
        return snapshot.lastBid;
      }
    },
    SNIPER_STATE("State") {
      @Override
      public Object valueIn(final SniperSnapshot snapshot) {
        return SnipersTableModel.textFor(snapshot.state);
      }
    };

    public final String name;

    private Column(final String name) {
      this.name = name;
    }

    abstract public Object valueIn(SniperSnapshot snapshot);

    public static Column at(final int offset) {
      return values()[offset];
    }
  }

}
