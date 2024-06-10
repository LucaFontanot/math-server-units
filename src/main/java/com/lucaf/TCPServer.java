package com.lucaf;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server class as a thread
 */
public class TCPServer extends Thread {

    /**
     * Port to listen to
     */
    private final int port;

    /**
     * Constructor
     *
     * @param port port to listen to
     */

    public TCPServer(int port) {
        this.port = port;
    }

    /**
     * Run method to start the server thread
     * It listens to the port and creates a new thread for each connection
     */
    @Override
    public void run() {
        System.out.println("Starting TCP server on port " + port);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("TCP server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler tcpMessagging = new ClientHandler(socket);
                tcpMessagging.start();
            }
        } catch (IOException e) {
            System.err.println("Failed to start TCP server on port " + port);
            System.err.println(e.toString());
            throw new RuntimeException(e);
        }
    }
}
