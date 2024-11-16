package com.aq2world.rcon.connection;

import com.aq2world.rcon.exception.RconException;

import java.io.*;
import java.net.Socket;

/**
 * Remote Console (RCON) connection UDP implementation, specific for Quake II engine
 *
 * @author Rezet
 */
public class RconTCPConnection extends RconConnection {

    private final Socket socket;

    private final BufferedReader reader;

    private final OutputStream outputStream;

    public RconTCPConnection(String host, int port, String password) throws RconException {
        super(host, port, password);

        throw new RconException("Not supported yet");
        /**
        try {
            this.socket = new Socket(host, port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    /**
     * @see RconConnection#send(String)
     */
    @Override
    public String send(String command) throws RconException {
        if (command == null || command.isEmpty()) {
            throw new RconException("No command supplied");
        }
        // Build the RCON command packet
        byte[] commandBytes = buildCommandBytes(password, command);
        byte[] rconPacket = new byte[RCON_SEND_PREFIX.length + commandBytes.length];

        System.arraycopy(RCON_SEND_PREFIX, 0, rconPacket, 0, RCON_SEND_PREFIX.length);
        System.arraycopy(commandBytes, 0, rconPacket, RCON_SEND_PREFIX.length, commandBytes.length);

        try {
            // Send the command over the TCP connection
            outputStream.write(rconPacket);
            outputStream.flush();  // Ensure the data is sent immediately

        } catch (IOException e) {
            throw new RconException("Unable to send RCON command: " + e.getMessage());
        }
        return receiveAll(TIMEOUT);
    }

    /**
     * @see RconConnection#receiveAll(int) 
     */
    @Override
    protected String receiveAll(int timeout) throws RconException {
        StringBuilder response = new StringBuilder();
        long startTime = System.currentTimeMillis();

        // Loop to check for data until timeout expires
        while (System.currentTimeMillis() - startTime < timeout * 1000L) {
            try {
                if (reader.ready()) {  // Check if data is available to read
                    char[] buffer = new char[4096];
                    int bytesRead = reader.read(buffer);
                    if (bytesRead != -1) {
                        response.append(new String(buffer, 0, bytesRead));
                        // Reset timeout after receiving data
                        startTime = System.currentTimeMillis();
                    }
                }
            } catch (IOException e) {
                throw new RconException("Unable to receive: " + e.getMessage());
            }

            // Check for timeout
            if ((System.currentTimeMillis() - startTime) > timeout) {
                break;
            }
        }

        // Handle timeout or no data received
        if (response.length() == 0) {
            throw new RconException("Socket timeout or no response received.");
        }

        return response.toString().trim();
    }

    /**
     * @see AutoCloseable#close()
     */
    @Override
    public void close() throws Exception {
        try {
            if (socket != null && !socket.isClosed()) {
                reader.close();
                outputStream.close();
                socket.close();
                System.out.println("Socket closed successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
}
