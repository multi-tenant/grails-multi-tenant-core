package grails.plugin.multitenant.core

/**
 * This exception is thrown if an invalid tenant request is encountered like a HttpRequest that does not resolve to a tenant.
 */
class InvalidTenantException extends RuntimeException
{
  /**
   * This is a constructor that takes a message to display.
   * @param inMessage - The message to display.
   * @return The exception object.
   */
  InvalidTenantException(String inMessage)
  {
    super(inMessage)
  }
  /**
   * This is a constructor that takes a message to display and a cause.
   * @param inMessage - The message to display.
   * @param inCause - The exception that caused this exception.
   * @return The exception object.
   */
  InvalidTenantException(String inMessage, Throwable inCause)
  {
    super(inMessage, inCause)
  }
}
