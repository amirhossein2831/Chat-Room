import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedWriter bw;
    private BufferedReader br;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = userName;
        } catch (IOException e) {
            closeEveryThings(socket, br, bw);
        }
    }

    public void sendMessage() {
        try {
            bw.write(this.getUserName());
            bw.newLine();
            bw.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String message = scanner.nextLine();
                bw.write(this.getUserName() + ": " + message);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            closeEveryThings(socket, br, bw);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = br.readLine();
                    if (messageFromServer == null) {
                        System.exit(0);
                    }
                    System.out.println(messageFromServer);
                } catch (IOException e) {
                    closeEveryThings(socket, br, bw);
                }
            }
        }).start();
    }

    public void closeEveryThings(Socket socket, BufferedReader br, BufferedWriter bw) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter your userName for the grout chat: ");
        System.out.println("If you want to quit Enter the EXIT");
        String userName = scanner.nextLine();
        if(userName.equals("EXIT"))
            return;
        Socket socketHold = new Socket("127.0.0.1", 8888);
        Client client = new Client(socketHold,userName);

        client.listenForMessage();
        client.sendMessage();

    }
}
