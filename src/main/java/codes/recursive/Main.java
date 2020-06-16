package codes.recursive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    private static ServerSocket socketServer;

    public static void main(String[] args) throws IOException {
        Logger logger = LoggerFactory.getLogger(Main.class);
        int port = Integer.parseInt( System.getProperty("port", "30000") );
        socketServer = new ServerSocket(port);

        logger.info("Listening on localhost:{}...", port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Server shutting down. Goodbye...");
            try {
                socketServer.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }));

        //noinspection InfiniteLoopStatement
        while(true) {
            Socket socket = socketServer.accept();
            Runnable messageHandler = new MessageHandler(socket);
            new Thread(messageHandler).start();
        }
    }

}