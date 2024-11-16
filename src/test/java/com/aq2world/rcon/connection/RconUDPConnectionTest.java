package com.aq2world.rcon.connection;

import com.aq2world.rcon.exception.RconException;
import org.junit.jupiter.api.*;

import java.net.DatagramSocket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RconUDPConnection}.
 * This class ensures that the functionality of the RconUDPConnection class
 * works as expected, including sending and receiving RCON commands.
 */
class RconUDPConnectionTest {

    private RconUDPConnection connection;

    /**
     * Sets up the test environment by initializing a mocked UDP RCON connection.
     * The connection uses a local dummy address and port for testing.
     */
    @BeforeEach
    void setUp() throws RconException {
        connection = new RconUDPConnection("127.0.0.1", 12345, "testPassword") {
            @Override
            protected String receiveAll(int timeout) {
                return "Mocked response";
            }
        };
    }

    /**
     * Cleans up resources after each test by closing the connection.
     */
    @AfterEach
    void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Tests the constructor with valid inputs.
     * Ensures that no exception is thrown during connection initialization.
     *
     * @TODO can only test when having a server active in the loopback address
    @Test
    void testValidConstructor() {
        assertDoesNotThrow(() -> new RconUDPConnection("localhost", 12345, "testPassword"));
    }
    */

    /**
     * Tests the constructor with an invalid host.
     * Ensures that an {@link RconException} is thrown for an invalid hostname.
     */
    @Test
    void testInvalidHost() {
        RconException exception = assertThrows(RconException.class, () ->
                new RconUDPConnection("invalid_host", 12345, "testPassword")
        );
        assertTrue(exception.getMessage().contains("Unable to set up the RCON connection"));
    }

    /**
     * Tests the constructor with an invalid port.
     * Ensures that an {@link RconException} is thrown for an invalid port.
     */
    @Test
    void testInvalidPort() {
        RconException exception = assertThrows(RconException.class, () ->
                new RconUDPConnection("127.0.0.1", -1, "testPassword")
        );
        assertEquals("Invalid port number.", exception.getMessage());
    }

    /**
     * Tests the {@link RconUDPConnection#send(String)} method with a valid command.
     * Verifies that the response is returned successfully.
     *
     * @TODO can only test when having a server active in the loopback address
    @Test
    void testSendCommandValid() throws RconException {
        String response = connection.send("status");
        assertEquals("Mocked response", response);
    }
    */

    /**
     * Tests the {@link RconUDPConnection#send(String)} method with a null command.
     * Ensures that an {@link RconException} is thrown for null or empty commands.
     */
    @Test
    void testSendCommandNull() {
        RconException exception = assertThrows(RconException.class, () -> connection.send(null));
        assertEquals("No command supplied", exception.getMessage());
    }

    /**
     * Tests the {@link RconUDPConnection#send(String)} method with an empty command.
     * Ensures that an {@link RconException} is thrown for empty commands.
     */
    @Test
    void testSendCommandEmpty() {
        RconException exception = assertThrows(RconException.class, () -> connection.send(""));
        assertEquals("No command supplied", exception.getMessage());
    }

    /**
     * Tests the {@link RconUDPConnection#receiveAll(int)} method with a timeout.
     * Verifies that the mocked implementation returns the expected response.
     */
    @Test
    void testReceiveAll() throws RconException {
        String response = connection.receiveAll(3000);
        assertEquals("Mocked response", response);
    }

    /**
     * Tests the {@link RconUDPConnection#close()} method.
     * Ensures that the socket is successfully closed without throwing exceptions.
     */
    @Test
    void testCloseConnection() {
        assertDoesNotThrow(() -> connection.close());
    }

    /**
     * Tests sending a command with an unconnected socket.
     * Ensures that an {@link RconException} is thrown if the socket is not properly set up.
     */
    @Test
    void testSendWithSocketException() {
        assertThrows(RconException.class, () -> {
            DatagramSocket socket = new DatagramSocket();
            socket.close(); // Simulate socket being closed prematurely
            new RconUDPConnection("127.0.0.1", 12345, "testPassword") {
                @Override
                protected String receiveAll(int timeout) throws RconException {
                    return "Should not reach here";
                }
            }.send("status");
        });
    }
}
