package br.com.socket;

import java.net.URISyntaxException;
import java.util.Scanner;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

public class SocketCliente {

    static private io.socket.client.Socket socket;

    public static void main(String[] args) throws Exception {
        new SocketCliente().client(1);
        //Thread.sleep(1000);
        //new SocketCliente().client(2);
    }

    public void client(int i) throws URISyntaxException, InterruptedException {
        System.out.println("Logando como Cliente " + i);

        socket = IO.socket("http://localhost:" + 5003);
        socket.on(io.socket.client.Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("toServer", "connected");
                socket.send("test");
            }
        });

        socket.on("toClient", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Client recievd : " + args[0]);

            }
        });

        socket.on("teste", arg0 -> {
            System.out.println("teste ->" + arg0);
        });

        socket.connect();

        try (final Scanner input = new Scanner(System.in)) {
            String retorno = "";

            while (!retorno.trim().equals("quit") && !retorno.trim().equals("q")) {

                if(!retorno.isEmpty()) {
                    socket.emit("print", retorno);
                }

                retorno = input.nextLine().trim();
            }
        }

        socket.disconnect();
    }
}
