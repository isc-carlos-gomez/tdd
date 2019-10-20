package com.krloxz.auctionsniper;

/**
 * Thrown when the code reaches a condition that could only be caused by a programming error.
 *
 * @author Carlos Gomez
 *
 */
public class Defect extends RuntimeException {

  private static final long serialVersionUID = 1303248197276576603L;

  public Defect(final String message) {
    super(message);
  }

}
