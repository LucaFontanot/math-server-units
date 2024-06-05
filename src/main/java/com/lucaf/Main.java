package com.lucaf;

/**
 * Main class to start the server
 */
public class Main {

    /**
     * Main method to start the server
     *
     * @param args [port]
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            Config.port = Integer.parseInt(args[0]);
        }
        TCPServer tcpServer = new TCPServer(Config.port);
        tcpServer.start();
    }
}