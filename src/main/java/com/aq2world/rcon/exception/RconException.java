/**
 * Represents a custom exception used for RCON (Remote Console) operations.
 * This exception extends {@link RuntimeException} and is designed to handle
 * errors specific to RCON functionalities.
 */
package com.aq2world.rcon.exception;

/**
 * A runtime exception that indicates an error occurred during RCON operations.
 *
 * @author Rezet
 */
public class RconException extends RuntimeException {

    /**
     * Constructs a new RconException with the specified detail message.
     *
     * @param message the detail message providing additional information about the exception.
     */
    public RconException(String message) {
        super(message);
    }

    /**
     * Constructs a new RconException with the specified detail message and cause.
     *
     * @param message the detail message providing additional information about the exception.
     * @param cause   the cause of the exception (a {@code Throwable} object).
     */
    public RconException(String message, Throwable cause) {
        super(message, cause);
    }
}
