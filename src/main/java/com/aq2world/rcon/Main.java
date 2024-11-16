package com.aq2world.rcon;

import com.aq2world.rcon.connection.RconUDPConnection;

public class Main {

    public static void main(String[] args) {
        try (RconUDPConnection conn = new RconUDPConnection("cem.eixodigital.com", 27910, "actionquakeforever")) {
            conn.send("gamemap urban2");
        } catch (Exception e) {
            // Your throwing logic
        }
    }
}
