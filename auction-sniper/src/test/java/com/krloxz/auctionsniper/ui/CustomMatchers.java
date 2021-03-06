package com.krloxz.auctionsniper.ui;

import org.hamcrest.Matcher;

import com.krloxz.auctionsniper.domain.SniperSnapshot;

/**
 * Collection of custom Hamcrest matchers.
 *
 * @author Carlos Gomez
 *
 */
public abstract class CustomMatchers {

  /**
   * @see HasRowWithSniper#hasRowWithSniper(int, SniperSnapshot)
   */
  public static HasRowWithSniper hasRowWithSniper(final int rowIndex, final SniperSnapshot sniper) {
    return HasRowWithSniper.hasRowWithSniper(rowIndex, sniper);
  }

  /**
   * @see HasCellWithValue#hasCellWithValue(int, int, Matcher)
   */
  public static HasCellWithValue hasCellWithValue(final int rowIndex, final int columnIndex,
      final Matcher<?> valueMatcher) {
    return HasCellWithValue.hasCellWithValue(rowIndex, columnIndex, valueMatcher);
  }

}
