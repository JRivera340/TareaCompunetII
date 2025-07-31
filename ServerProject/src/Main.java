import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        var server = new ServerSocket(8080);
        var isAlive = true;

        System.out.println("Servidor iniciado en puerto 8080");

        while (isAlive) {
            System.out.println("Esperando nuevos clientes...");
            Socket clientSocket = server.accept();
            System.out.println("Nuevo cliente conectado desde: " + clientSocket.getInetAddress());

            // Crear un nuevo hilo para manejar al cliente
            ClientHandler clientHandler = new ClientHandler(clientSocket);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
        server.close();
    }
}