package com.aq2world.rcon.connection;

import com.aq2world.rcon.exception.RconException;

import java.util.Arrays;
import java.util.List;

/**
 * Remote Console (RCON) connection abstract class, specific for Quake II engine
 *
 * @author Rezet
 */
public abstract class RconConnection implements AutoCloseable {

    /**
     *  The ip/domain to send RCON commands
     */
    protected String host;

    /**
     * The port to send RCON commands
     */
    protected int port;

    /**
     * The RCON password
     */
    protected String password;

    /**
     * Default timeout for acknowledging commands, in milliseconds
     */
    protected final int TIMEOUT = 1000;

    protected final String RCON_COMMAND_STRING = "rcon %s %s";

    /**
     * List of bad rcon replies
     */
    private final List<String> BAD_RCON_REPLIES = Arrays.asList("Bad rcon_password.", "Invalid password.");

    /**
     * Hexadecimal byte representations for RCON command header
     */
    protected final byte[] RCON_SEND_PREFIX = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    /**
     * Hexadecimal byte representations for RCON response header
     */
    protected final byte[] RCON_REPLY = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 'p', (byte) 'r', (byte) 'i', (byte) 'n', (byte) 't', (byte) '\n'};

    /**
     * Default constuctor to be called by the respective implementations via super
     *
     * @param host the ip/domand
     * @param port the port
     * @param password the rcon password
     *
     * @throws RconException if invalid settings are provided
     */
    public RconConnection(String host, int port, String password) throws RconException {
        if (host == null || host.isEmpty()) {
            throw new RconException("Invalid hostname.");
        }

        if (port <= 0 || port > 65536) {
            throw new RconException("Invalid port number.");
        }

        this.host = host;
        this.port = port;
        this.password = password;
    }

    /**
     * Sends a RCON command via the connected socket
     *
     * @param command the command to execute
     * @return the output from the RCON
     *
     * @throws RconException if a command is not correctly supplied,
     * if the password is wrong or if the request times out
     */
    public abstract String send(String command) throws RconException;

    /**
     * Invoked via the {@link #send(String)} function
     *
     * @param timeout the timeout in milliseconds
     * @return the RCON output for the given command sent
     *
     * @throws RconException if a command is not correctly supplied,
     * if the password is wrong or if the request times out
     */
    protected abstract String receiveAll(int timeout) throws RconException;

    /**
     * Tests the RCON connection by sending a "status" command
     */
    protected void test() throws RconException {
        String response = send("status");
        System.out.println(response);
        if (BAD_RCON_REPLIES.contains(response)) {
            throw new RconException("Bad rcon password supplied");
        }
    }

    /**
     * Constructs the byte array representation of an RCON command using the provided password
     * and command string. This method formats the command according to the predefined
     * RCON command structure.
     *
     * @param password the password required for authenticating the RCON connection
     * @param command  the command to be executed on the RCON server
     * @return a byte array representing the formatted RCON command
     */
    protected byte[] buildCommandBytes(String password, String command) {
        return String.format(RCON_COMMAND_STRING, password, command).getBytes();
    }

}
