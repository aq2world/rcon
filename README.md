# AQ2World Java-based Remote Console (RCON) connection

This project contains implementations of the Quake II RCON using Java language

## Usage

1. Make sure you have the correct Java version installed. Refer to the file `pom.xml`

2. Install the library locally using Maven:

```bash
mvn clean install
```

3. Import the library in your project.


3. Example code

```java
try (RconUDPConnection conn = new RconUDPConnection("host", port, "password")) {
    conn.send("command");
} catch (Exception e) {
    // Your throwing logic    
}
```

## TODO list

* Test the TCP rcon. Current AQ2 client does not expose administration ports via TCP, only UDP.
