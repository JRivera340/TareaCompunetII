import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            // Obtener los streams de entrada y salida
            var os = clientSocket.getOutputStream();
            var in = clientSocket.getInputStream();

            var writer = new BufferedWriter(new OutputStreamWriter(os));
            var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Leer la solicitud HTTP
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                System.out.println("[Cliente " + clientSocket.getInetAddress() + "]: " + line);
            }

            // Enviar respuesta HTTP
            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: 162\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.write("<html><body><h1>Bienvenido al Servidor Multihilos</h1>" +
                    "<p>Conectado desde: " + clientSocket.getInetAddress() + "</p>" +
                    "<p>Thread ID: " + Thread.currentThread().getId() + "</p></body></html>");

            writer.flush();
            writer.close();
            clientSocket.close();

            System.out.println("Cliente desconectado: " + clientSocket.getInetAddress());

        } catch (IOException e) {
            System.err.println("Error manejando cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando el socket: " + e.getMessage());
            }
        }
    }
}