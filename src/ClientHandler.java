import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.setUserName(br.readLine());
            clients.add(this);

            broadcastMessage("Server: " + this.getUserName() + " Enter the chat");
        } catch (IOException e) {
            closeEveryThings(socket, bw, br);
            e.printStackTrace();
        }
    }
    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clients) {
            try {
                if (!clientHandler.getUserName().equals(userName)) {
                    clientHandler.bw.write(messageToSend);
                    clientHandler.bw.newLine();
                    clientHandler.bw.flush();
                }
            } catch (IOException e) {
                closeEveryThings(socket, bw, br);
                e.printStackTrace();
            }
        }
    }
    @Override
    public void run() {
        String message;
        while (socket.isConnected()) {
            try {
                message = br.readLine();
                if (message == null) {
                    throw new IOException();
                }
                if (message.endsWith("#exit")) {
                    closeEveryThings(socket, bw, br);
                    break;
                }
                broadcastMessage(message);
            } catch (IOException e ) {
                closeEveryThings(socket, bw, br);
                break;
            }
        }
    }

    public void removeClientHandler() {
        clients.remove(this);
        broadcastMessage(this.getUserName() + " left the chat");
    }
    public void closeEveryThings(Socket socket, BufferedWriter bw, BufferedReader br) {
        removeClientHandler();
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
