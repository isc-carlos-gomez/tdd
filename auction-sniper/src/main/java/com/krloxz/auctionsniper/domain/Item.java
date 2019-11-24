package com.krloxz.auctionsniper.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Value object that represents an item the Sniper is bidding for.
 *
 * @author Carlos Gomez
 *
 */
public class Item {

  public final String identifier;
  public final int stopPrice;

  /**
   * Creates a new item.
   *
   * @param identifier
   *        item identifier
   * @param stopPrice
   *        maximum limit for bidding
   */
  public Item(final String identifier, final int stopPrice) {
    this.identifier = identifier;
    this.stopPrice = stopPrice;
  }

  /**
   *
   * @param bid
   *        the bid to enquire
   * @return whether the stop price of this item allows for the bid
   */
  public boolean allowsBid(final int bid) {
    return bid <= this.stopPrice;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
