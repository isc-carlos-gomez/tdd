package com.krloxz.auctionsniper;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class HasCellWithValue extends TypeSafeDiagnosingMatcher<SnipersTableModel> {

  private final int rowIndex;
  private final int columnIndex;
  private final Matcher<?> valueMatcher;

  private HasCellWithValue(final int rowIndex, final int columnIndex, final Matcher<?> valueMatcher) {
    this.rowIndex = rowIndex;
    this.columnIndex = columnIndex;
    this.valueMatcher = valueMatcher;
  }

  @Override
  protected boolean matchesSafely(final SnipersTableModel tableModel, final Description mismatchDescription) {
    if (tableModel.getRowCount() <= this.rowIndex) {
      mismatchDescription.appendText("there are only ")
          .appendValue(tableModel.getRowCount())
          .appendText(" rows");
      return false;
    }
    if (tableModel.getColumnCount() <= this.columnIndex) {
      mismatchDescription.appendText("there are only ")
          .appendValue(tableModel.getColumnCount())
          .appendText(" columns");
      return false;
    }
    if (!this.valueMatcher.matches(tableModel.getValueAt(this.rowIndex, this.columnIndex))) {
      mismatchDescription.appendText("cell value at (")
          .appendText(this.rowIndex + "")
          .appendText(",")
          .appendText(this.columnIndex + "")
          .appendText(") ");
      this.valueMatcher.describeMismatch(tableModel.getValueAt(this.rowIndex, this.columnIndex), mismatchDescription);
      return false;
    }
    return true;
  }

  @Override
  public void describeTo(final Description description) {
    description.appendText("has cell value at (")
        .appendText(this.rowIndex + "")
        .appendText(",")
        .appendText(this.columnIndex + "")
        .appendText(") that ")
        .appendDescriptionOf(this.valueMatcher);
  }

  /**
   * Creates a matcher that matches if the examined {@link SnipersTableModel} has a value at the
   * specified row and column that matches the given matcher.
   *
   * @param rowIndex
   *        the row where the value is located at
   * @param columnIndex
   *        the column where the value is located at
   * @param valueMatcher
   *        the value matcher
   * @return the new matcher
   */
  @Factory
  public static HasCellWithValue hasCellWithValue(final int rowIndex, final int columnIndex,
      final Matcher<?> valueMatcher) {
    return new HasCellWithValue(rowIndex, columnIndex, valueMatcher);
  }

}
