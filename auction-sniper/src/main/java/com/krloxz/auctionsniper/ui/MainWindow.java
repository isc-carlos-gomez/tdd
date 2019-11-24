package com.krloxz.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.krloxz.auctionsniper.domain.Item;
import com.krloxz.auctionsniper.domain.SniperPortfolio;
import com.krloxz.auctionsniper.domain.UserRequestListener;
import com.krloxz.auctionsniper.util.Announcer;

/**
 * @author Carlos Gomez
 */
public class MainWindow extends JFrame {

  public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
  public static final String SNIPERS_TABLE_NAME = "SnipersTable";
  public static final String APPLICATION_TITLE = "Auction Sniper";
  public static final String NEW_ITEM_ID_NAME = "NewItemId";
  public static final String NEW_ITEM_STOP_PRICE_NAME = "NewItemStopPrice";
  public static final String JOIN_BUTTON_NAME = "JoinButton";

  public static final String STATUS_JOINING = "Joining";
  public static final String STATUS_LOST = "Lost";
  public static final String STATUS_BIDDING = "Bidding";
  public static final String STATUS_WINNING = "Winning";
  public static final String STATUS_WON = "Won";

  private static final long serialVersionUID = -6760751900182013662L;
  private final Announcer<UserRequestListener> userRequests;

  public MainWindow(final SniperPortfolio portfolio) {
    super(APPLICATION_TITLE);
    this.userRequests = Announcer.to(UserRequestListener.class);

    setName(MAIN_WINDOW_NAME);
    fillContentPane(makeSnipersTable(portfolio), makeControls());
    pack();
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public void addUserRequestListener(final UserRequestListener userRequestListener) {
    this.userRequests.addListener(userRequestListener);
  }

  private void fillContentPane(final JTable snipersTable, final JPanel controls) {
    final Container contentPane = getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(controls, BorderLayout.PAGE_START);
    contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
  }

  private JTable makeSnipersTable(final SniperPortfolio portfolio) {
    final SnipersTableModel model = new SnipersTableModel();
    portfolio.addPortfolioListener(model);
    final JTable snipersTable = new JTable(model);
    snipersTable.setName(SNIPERS_TABLE_NAME);
    return snipersTable;
  }

  private JPanel makeControls() {
    final JPanel controls = new JPanel(new FlowLayout());

    final JLabel itemIdLabel = new JLabel("Item:");
    controls.add(itemIdLabel);
    final JTextField itemIdField = new JTextField();
    itemIdField.setColumns(25);
    itemIdField.setName(NEW_ITEM_ID_NAME);
    controls.add(itemIdField);

    final JLabel stopPriceLabel = new JLabel("Stop price:");
    controls.add(stopPriceLabel);
    final JFormattedTextField stopPriceField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    stopPriceField.setColumns(25);
    stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
    controls.add(stopPriceField);

    final JButton joinAuctionButton = new JButton("Join Auction");
    joinAuctionButton.setName(JOIN_BUTTON_NAME);
    joinAuctionButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        MainWindow.this.userRequests.announce().joinAuction(new Item(itemId(), stopPrice()));
      }

      private String itemId() {
        return itemIdField.getText();
      }

      private int stopPrice() {
        return ((Number) stopPriceField.getValue()).intValue();
      }
    });
    controls.add(joinAuctionButton);
    return controls;
  }

}
