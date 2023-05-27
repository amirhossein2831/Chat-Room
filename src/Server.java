import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Server {
    private ServerSocket serverSocket;
    private final static Logger LOGGER =  Logger.getLogger(Server.class.getName());;
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                LOGGER.info(ANSI_GREEN + "A new client connected to Server" + ANSI_RESET);
                System.out.println(ANSI_GREEN + "the port of the connection is : " + socket.getPort() + ANSI_RESET);
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        Server server = new Server(new ServerSocket(8888));
        server.startServer();
    }
}