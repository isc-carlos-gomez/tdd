package com.krloxz.auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.krloxz.auctionsniper.domain.AuctionSniper;
import com.krloxz.auctionsniper.domain.PortfolioListener;
import com.krloxz.auctionsniper.domain.SniperListener;
import com.krloxz.auctionsniper.domain.SniperSnapshot;
import com.krloxz.auctionsniper.domain.SniperSnapshot.SniperState;
import com.krloxz.auctionsniper.util.Defect;

/**
 * @author Carlos Gomez
 *
 */
public class SnipersTableModel extends AbstractTableModel
    implements SniperListener, PortfolioListener {

  private static final long serialVersionUID = 2685916085280519480L;
  private static final String[] STATUS_TEXT = {
      "Joining", "Bidding", "Winning", "Losing", "Lost", "Won", "Failed"
  };

  private final List<SniperSnapshot> snapshots = new ArrayList<>();

  @Override
  public void sniperAdded(final AuctionSniper sniper) {
    sniper.addSniperListener(this);
    addSniper(sniper.getSnapshot());
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

  private void addSniper(final SniperSnapshot snapshot) {
    this.snapshots.add(snapshot);
    final int row = this.snapshots.size() - 1;
    fireTableRowsInserted(row, row);
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
