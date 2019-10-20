package com.krloxz.auctionsniper;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * @author Carlos Gomez
 */
public class MainWindow extends JFrame {

  public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
  public static final String SNIPERS_TABLE_NAME = "SnipersTable";
  public static final String APPLICATION_TITLE = "Auction Sniper";

  public static final String STATUS_JOINING = "Joining";
  public static final String STATUS_LOST = "Lost";
  public static final String STATUS_BIDDING = "Bidding";
  public static final String STATUS_WINNING = "Winning";
  public static final String STATUS_WON = "Won";

  private static final long serialVersionUID = -6760751900182013662L;
  private final SnipersTableModel snipers;

  public MainWindow(final SnipersTableModel snipers) {
    super(APPLICATION_TITLE);
    setName(MAIN_WINDOW_NAME);
    this.snipers = snipers;
    fillContentPane(makeSnipersTable());
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  private void fillContentPane(final JTable snipersTable) {
    final Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
  }

  private JTable makeSnipersTable() {
    final JTable snipersTable = new JTable(this.snipers);
    snipersTable.setName(SNIPERS_TABLE_NAME);
    return snipersTable;
  }

  public void sniperStateChanged(final SniperSnapshot snapshot) {
    this.snipers.sniperStateChanged(snapshot);
  }

}
