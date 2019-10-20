package com.krloxz.auctionsniper;

import javax.swing.table.AbstractTableModel;

/**
 * @author Carlos Gomez
 *
 */
public class SnipersTableModel extends AbstractTableModel {

  private static final long serialVersionUID = 2685916085280519480L;
  private String statusText = MainWindow.STATUS_JOINING;

  @Override
  public int getColumnCount() {
    return 1;
  }

  @Override
  public int getRowCount() {
    return 1;
  }

  @Override
  public Object getValueAt(final int rowIndex, final int columnIndex) {
    return this.statusText;
  }

  public void setStatusText(final String newStatusText) {
    this.statusText = newStatusText;
    fireTableRowsUpdated(0, 0);
  }

}
