package com.aq2world.rcon.connection;

import com.aq2world.rcon.exception.RconException;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a UDP-based RCON (Remote Console) connection.
 * This class extends {@link RconConnection} to provide functionality for sending and receiving
 * commands via the RCON protocol using UDP. It manages the creation and configuration
 * of a {@link DatagramSocket} for communication with the specified server.
 *
 * <p>Thread safety is ensured by using a {@link Lock} to synchronize critical operations.
 *
 * @author Rezet
 */
public class RconUDPConnection extends RconConnection {

    /**
     * A lock used to ensure thread-safe access to the connection operations.
     */
    private final Lock lock;

    /**
     * The datagram socket used for sending and receiving UDP packets.
     */
    private final DatagramSocket socket;

    /**
     * Creates a new UDP-based RCON connection to a specified host and port.
     *
     * @param host     the hostname or IP address of the server to connect to
     * @param port     the port number on which the RCON service is running
     * @param password the password for authenticating the RCON connection
     * @throws RconException if the connection could not be established due to socket issues
     *                       or an invalid hostname
     */
    public RconUDPConnection(String host, int port, String password) throws RconException {
        super(host, port, password);

        this.lock = new ReentrantLock();

        try {
            this.socket = new DatagramSocket();
            this.socket.connect(InetAddress.getByName(host), port);
            test();
        } catch (SocketException | UnknownHostException e) {
            throw new RconException("Unable to set up the RCON connection: " + e.getMessage());
        }
    }

    /**
     * @see RconConnection#send(String)
     */
    @Override
    public String send(String command) throws RconException {
        if (command == null || command.isEmpty()) {
            throw new RconException("No command supplied");
        }

        try {
            lock.lock();

            // Build the RCON command packet
            byte[] commandBytes = buildCommandBytes(password, command);
            byte[] rconPacket = new byte[RCON_SEND_PREFIX.length + commandBytes.length];

            System.arraycopy(RCON_SEND_PREFIX, 0, rconPacket, 0, RCON_SEND_PREFIX.length);
            System.arraycopy(commandBytes, 0, rconPacket, RCON_SEND_PREFIX.length, commandBytes.length);

            DatagramPacket sendPacket = new DatagramPacket(rconPacket, rconPacket.length, InetAddress.getByName(host), port);
            socket.send(sendPacket);
        } catch (IOException e) {
            throw new RconException("Unable to send RCON command: " + e.getMessage());
        } finally {
            lock.unlock();
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

        try {
            socket.setSoTimeout(timeout);

            boolean dataReceived = false;

            while (System.currentTimeMillis() - startTime < timeout) {
                byte[] receiveData = new byte[4096];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try {
                    // Collect the data from the socket
                    socket.receive(receivePacket);

                    // Parse the received response as a string
                    String packetResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    if (!packetResponse.isEmpty()) {
                        // Append without the REPLY header
                        response.append(packetResponse.replace(new String(RCON_REPLY), ""));
                        dataReceived = true;
                        //Reset the timeout on successful reception
                        startTime = System.currentTimeMillis();
                    }
                } catch (SocketTimeoutException e) {
                    if (dataReceived) {
                        break; // Exit if data's been already received
                    }
                    throw new RconException("Socket timeout: " + e.getMessage());
                } catch (IOException e) {
                    throw new RconException("Error receiving data: " + e.getMessage());
                }
            }

            if (!dataReceived) {
                throw new RconException("No data received within the timeout period.");
            }
        } catch (SocketException | IllegalArgumentException e) {
            throw new RconException("Unable to set UDP socket connection: " + e.getMessage());
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
                socket.close();
            }
        } catch (Exception e) {
            throw new Exception("Error closing UDP scoket: " + e.getMessage());
        }
    }
}
