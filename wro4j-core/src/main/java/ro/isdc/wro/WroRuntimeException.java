/*
 * Copyright (c) 2008. All rights reserved.
 */
package ro.isdc.wro;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Base Wro Runtime exception. All exceptions will extend this runtime
 * exception.
 *
 * @author Alex Objelean
 * @created Created on Nov 3, 2008
 */
public class WroRuntimeException extends RuntimeException {
  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(WroRuntimeException.class);

  /**
   * @param message
   * @param cause
   */
  public WroRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
    LOG.debug(message);
  }

  /**
   * @param message
   */
  public WroRuntimeException(final String message) {
    this(message, null);
  }

  /**
   * Logs the error of this exception. By default errors are logged with DEBUG level. This method will use ERROR level.
   */
  public WroRuntimeException logError() {
    LOG.error(getMessage());
    return this;
  }
  
  /**
   * Wraps original exception into {@link WroRuntimeException} and throw it.
   * 
   * @param e
   *          the exception to wrap.
   */
  public static WroRuntimeException wrap(final Exception e) {
    LOG.error("Exception occured: " + e.getClass(), e.getCause());
    if (e instanceof WroRuntimeException) {
      return (WroRuntimeException) e;
    }
    return new WroRuntimeException(e.getMessage(), e);
  }
}
