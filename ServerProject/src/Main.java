import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public void init() throws IOException {
        var server = new ServerSocket(8080);

        var isAlive = true;

        while (isAlive) {

            System.out.println("Servidor Escuchando");

            var socket = server.accept();

            System.out.println("Cliente Conectado");

            dispatchWorcker(socket);

        }
    }

    private void dispatchWorcker(Socket socket) {
        new Thread(
                () -> {
                    try {
                        handleRequest(socket);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();
    }

    private void handleRequest(Socket socket) throws IOException {

        var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {

            if (line.startsWith("GET")) {
                var resource = line.split(" ")[1].replace("/", "");
                System.out.println("El cliente esta pidiendo " + resource);

                // Enviar la response

                sendResponse(socket, resource);
            }

        }

    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        return "text/plain";
    }

    private void sendResponse(Socket socket, String resources) throws IOException {
        // Obtener la ruta absoluta del directorio del proyecto
        var projectDir = new File(System.getProperty("user.dir"));
        System.out.println("Directorio actual: " + projectDir.getAbsolutePath());

        // Construir la ruta al archivo
        var res = new File(projectDir, "../resources/" + resources);
        System.out.println("Intentando acceder al archivo: " + res.getAbsolutePath());
        System.out.println("¿El archivo existe? " + res.exists());
        System.out.println("¿Es un archivo? " + res.isFile());
        System.out.println("¿Se puede leer? " + res.canRead());
        System.out.println(res.getAbsolutePath());

        var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        if (res.exists()) {
            // Para archivos binarios (imágenes) usamos FileInputStream directamente
            var fis = new FileInputStream(res);
            byte[] fileContent;

            if (resources.endsWith(".jpg") || resources.endsWith(".jpeg") || resources.endsWith(".gif")) {
                // Para imágenes, leemos los bytes directamente
                fileContent = fis.readAllBytes();
            } else {
                // Para HTML y otros archivos de texto
                var br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                fileContent = response.toString().getBytes();
            }

            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Content-Type: " + getContentType(resources) + "\r\n");
            writer.write("Content-Length: " + fileContent.length + "\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.flush();

            // Escribimos el contenido del archivo directamente al OutputStream
            socket.getOutputStream().write(fileContent);
            socket.getOutputStream().flush();
        } else {
            // Manejo del error 404
            String errorMessage = "<html><body><h1>Error 404</h1><p>Recurso no encontrado: " + resources
                    + "</p></body></html>";
            writer.write("HTTP/1.0 404 Not Found\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: " + errorMessage.length() + "\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.write(errorMessage);
        }

        writer.close();
        socket.close();

    }

    public static void main(String[] args) throws IOException {
        // Imprimir el directorio de trabajo actual
        System.out.println("Directorio de trabajo al inicio: " + System.getProperty("user.dir"));

        // Asegurarnos de que estamos en el directorio correcto
        File workingDir = new File(System.getProperty("user.dir"));
        if (!workingDir.getName().equals("ServerProject")) {
            // Si no estamos en ServerProject, intentamos cambiar al directorio correcto
            File serverProjectDir = new File(workingDir, "ServerProject");
            if (serverProjectDir.exists() && serverProjectDir.isDirectory()) {
                System.setProperty("user.dir", serverProjectDir.getAbsolutePath());
                System.out.println("Cambiado al directorio: " + System.getProperty("user.dir"));
            }
        }

        Main main = new Main();
        main.init();

        /*
         * var isAlive = true;
         * 
         * while(isAlive){
         * 
         * //Respuesta
         * 
         * var os = socket.getOutputStream();
         * var in = socket.getInputStream();
         * 
         * 
         * }
         * server.close();
         * 
         * 
         */

    }

}