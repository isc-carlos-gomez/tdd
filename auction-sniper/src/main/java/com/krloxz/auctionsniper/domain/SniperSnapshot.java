package com.krloxz.auctionsniper.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.krloxz.auctionsniper.util.Defect;

/**
 * @author Carlos Gomez
 *
 */
public class SniperSnapshot {

  public final String itemId;
  public final int lastPrice;
  public final int lastBid;
  public final SniperState state;

  public SniperSnapshot(final String itemId, final int lastPrice, final int lastBid, final SniperState state) {
    this.itemId = itemId;
    this.lastPrice = lastPrice;
    this.lastBid = lastBid;
    this.state = state;
  }

  public static SniperSnapshot joining(final String itemId) {
    return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
  }

  public SniperSnapshot bidding(final int newLastPrice, final int newLastBid) {
    return new SniperSnapshot(this.itemId, newLastPrice, newLastBid, SniperState.BIDDING);
  }

  public SniperSnapshot winning(final int newLastPrice) {
    return new SniperSnapshot(this.itemId, newLastPrice, this.lastBid, SniperState.WINNING);
  }

  public SniperSnapshot closed() {
    return new SniperSnapshot(this.itemId, this.lastPrice, this.lastBid, this.state.whenAuctionClosed());
  }

  public boolean isForSameItemAs(final SniperSnapshot sniperSnapshot) {
    return this.itemId.equals(sniperSnapshot.itemId);
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

  public enum SniperState {
    JOINING {
      @Override
      public SniperState whenAuctionClosed() {
        return LOST;
      }
    },
    BIDDING {
      @Override
      public SniperState whenAuctionClosed() {
        return LOST;
      }
    },
    WINNING {
      @Override
      public SniperState whenAuctionClosed() {
        return WON;
      }
    },
    LOST, WON;

    public SniperState whenAuctionClosed() {
      throw new Defect("Auction is already closed");
    }
  }

}
