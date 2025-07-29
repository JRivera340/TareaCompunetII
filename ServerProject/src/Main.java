import java.io.*;
import java.net.ServerSocket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) throws IOException {
        //192.168.131.37


        var server = new ServerSocket(8080);

        var isAlive = true;

        while(isAlive){
            System.out.println("Servidor Escuchando");

            var socket = server.accept();

            System.out.println("Cliente Conectado");

            //Respuesta

            var os = socket.getOutputStream();
            var in = socket.getInputStream();

            var writer = new BufferedWriter(new OutputStreamWriter(os));

            var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()){
                System.out.println(line);

            }

            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: 34\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.write("<html><body>Hola Mundo</body></html>");

            writer.flush();

            writer.close();
            socket.close();
        }
        server.close();

    }

    //Terminar servidor multihilos para el jueves, implementar hilos.
}