package com.lucaf;

import com.lucaf.datatypes.ComputeRequest;
import com.lucaf.datatypes.Request;
import com.lucaf.datatypes.Response;
import com.lucaf.datatypes.StatRequest;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for handling the communication with the client
 */
public class ClientHandler extends Thread {

    /**
     * Socket to communicate with the client
     */
    private final Socket socket;

    private final ExecutorService executorService;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    /**
     * Constructor, sets the socket
     *
     * @param socket socket to communicate with the client
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.executorService = Executors.newSingleThreadExecutor();

    }

    /**
     * Method to send a message to the client through the socket
     *
     * @param message message to send
     */
    private void sendMessage(String message) {
        synchronized (bufferedWriter) {
            try {
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                System.err.println("Failed to send message to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                System.err.println(e.toString());
            }
        }
    }

    /**
     * Method to close the socket
     */
    private void close() {
        try {
            System.out.println("TCP connection closed with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            bufferedWriter.close();
            bufferedReader.close();
            executorService.close();
        } catch (IOException e) {
            System.err.println("Failed to close TCP connection with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            System.err.println(e.toString());
        }
    }

    /**
     * Run method to start the message thread
     */
    @Override
    public void run() {
        try {
            socket.setSoTimeout(1000 * 60 * 5);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("TCP connection established with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            while (true) {
                try {
                    if (socket.isClosed() || !socket.isConnected() || socket.isInputShutdown() || socket.isOutputShutdown()) {
                        close();
                        break;
                    }
                    String message = bufferedReader.readLine();
                    if (message == null) {
                        continue;
                    }
                    long millis = System.currentTimeMillis();
                    System.out.println("Received message from " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort() + ": " + message);
                    Request request = new Request(message);
                    Request.RequestType requestType = request.getRequestType();
                    if (requestType == null) {
                        System.out.println("Unknown request type: " + message);
                        Response response = new Response(new String[]{"(IllegalCommand) Unknown request type"});
                        sendMessage(response.toString());
                    } else if (requestType == Request.RequestType.QuitRequest) {
                        Response response = new Response(true);
                        sendMessage(response.toString());
                        socket.close();
                    } else if (requestType == Request.RequestType.ComputationRequest) {
                        ComputeRequest computeRequest = request.getComputeRequest();
                        Computation computation = new Computation(computeRequest);
                        ComputationAsync computationAsync = getComputationAsync(computation, millis);
                        executorService.submit(computationAsync);
                    } else if (requestType == Request.RequestType.StatRequest) {
                        StatRequest statRequest = request.getStatRequest();
                        Stats stats = new Stats(statRequest);
                        StatsAsync statsAsync = getStatsAsync(stats, millis);
                        executorService.submit(statsAsync);
                    }
                } catch (IOException e) {
                    close();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to establish TCP connection with " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            System.err.println(e.toString());
            throw new RuntimeException(e);
        }

    }

    /**
     * Method to get the computation async object from the request
     *
     * @param computation computation to run
     * @param millis      system time in milliseconds when the request was received
     * @return computation async object
     */
    private ComputationAsync getComputationAsync(Computation computation, long millis) {
        return new ComputationAsync(computation, new ComputationListener() {
            @Override
            public void onComputationAsyncComplete(Response response) {
                if (response.isOk()) {
                    Stats.addRequest(response.getTime());
                }
                sendMessage(response.toString());
            }

            @Override
            public void onComputationAsyncError(Exception e) {
                Response response = new Response(new String[]{"(Exception) " + e.toString()});
                sendMessage(response.toString());
            }
        });
    }

    /**
     * Method to get the stats async object from the request
     *
     * @param stats  stats to run
     * @param millis system time in milliseconds when the request was received
     * @return stats async object@
     */
    private StatsAsync getStatsAsync(Stats stats, long millis) {
        return new StatsAsync(stats, new StatsListener() {
            @Override
            public void onStatsAsyncComplete(Response response) {
                response.setTime(System.currentTimeMillis() - millis);
                sendMessage(response.toString());
            }

            @Override
            public void onStatsAsyncError(Exception e) {
                Response response = new Response(new String[]{"(Exception) " + e.toString()});
                sendMessage(response.toString());
            }
        });
    }
}
