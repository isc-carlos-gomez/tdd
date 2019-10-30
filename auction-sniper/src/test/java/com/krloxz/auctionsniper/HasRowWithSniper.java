package com.krloxz.auctionsniper;

import static com.krloxz.auctionsniper.CustomMatchers.hasCellWithValue;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.krloxz.auctionsniper.SnipersTableModel.Column;

public class HasRowWithSniper extends TypeSafeDiagnosingMatcher<SnipersTableModel> {

  private final List<HasCellWithValue> columnMatchers;

  private HasRowWithSniper(final int rowIndex, final SniperSnapshot sniper) {
    this.columnMatchers = Arrays.asList(
        hasCellWithValue(rowIndex, Column.ITEM_IDENTIFIER.ordinal(), is(sniper.itemId)),
        hasCellWithValue(rowIndex, Column.LAST_PRICE.ordinal(), is(sniper.lastPrice)),
        hasCellWithValue(rowIndex, Column.LAST_BID.ordinal(), is(sniper.lastBid)),
        hasCellWithValue(rowIndex, Column.SNIPER_STATE.ordinal(), is(SnipersTableModel.textFor(sniper.state))));
  }

  @Override
  protected boolean matchesSafely(final SnipersTableModel tableModel, final Description mismatchDescription) {
    for (final HasCellWithValue matcher : this.columnMatchers) {
      if (!matcher.matches(tableModel)) {
        matcher.describeMismatch(tableModel, mismatchDescription);
        return false;
      }
    }
    return true;
  }

  @Override
  public void describeTo(final Description description) {
    description.appendValueList("snipers table model\n", "\n", "", this.columnMatchers);
  }

  /**
   * Creates a matcher that matches if the examined {@link SnipersTableModel} has the given
   * {@link SniperSnapshot} in the given row.
   *
   * @param rowIndex
   *        the row where the snapshot is located at
   * @param sniper
   *        the sniper snapshot to match
   * @return the new matcher
   */
  @Factory
  public static HasRowWithSniper hasRowWithSniper(final int rowIndex, final SniperSnapshot sniper) {
    return new HasRowWithSniper(rowIndex, sniper);
  }

}
