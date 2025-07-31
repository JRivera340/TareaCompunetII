import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public void  init() throws IOException {
        var server = new ServerSocket(8080);

        var isAlive = true;

        while(isAlive){

            System.out.println("Servidor Escuchando");

            var socket = server.accept();

            System.out.println("Cliente Conectado");

            dispatchWorcker(socket);

        }
    }

    private void dispatchWorcker(Socket socket) {
        new Thread(
                ()->{
                    try {
                        handleRequest(socket);


                    } catch (IOException e){
                        e.printStackTrace();
                    }

                }
        ).start();
    }

    private void handleRequest(Socket socket) throws IOException {


        var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()){

            if (line.startsWith("GET")) {
                var resource = line.split(" ")[1].replace("/","");
                System.out.println("El cliente esta pidiendo "+ resource);

                //Enviar la response

                sendResponse(socket, resource);
            }

        }

    }

    private void sendResponse(Socket socket, String resources) throws IOException {

        var file = new File("");
        System.out.println(file.getAbsolutePath());
        var res = new File("resources/"+resources);
        System.out.println(res.getAbsolutePath());

        if (res.exists()) {
            var fis = new FileInputStream(res);
            var br = new BufferedReader(new InputStreamReader(fis));
            String line;
            StringBuilder response = new StringBuilder();
            while((line = br.readLine()) != null){
                response.append(line);
            }

            var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //Copy

            writer.write("HTTP/1.0 200 OK\r\n");
            writer.write("Content-Type: text/html\r\n");
            writer.write("Content-Length: "+response.length()+"\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.write(response.toString());

            writer.close();
            socket.close();
            //Paste
        } else {
            System.out.println("No se encuentra el archivo");
        }


    }

    public static void main(String[] args) throws IOException {
        //192.168.131.37

        Main main = new Main();
        main.init();

        /*
        var isAlive = true;

        while(isAlive){

            //Respuesta

            var os = socket.getOutputStream();
            var in = socket.getInputStream();


        }
        server.close();


         */

    }


}