package br.com.socket;

import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class SocketServer {
    static SocketIOServer server;

    public static void main(String[] args) throws Exception {
        System.out.println("Logando como Servidor");

        server();
    }

    public static void server() throws InterruptedException, UnsupportedEncodingException {
        Configuration config = new Configuration();
        config.setHostname("127.0.0.1");
        config.setPort(5003);
        server = new SocketIOServer(config);

        server.addConnectListener(client -> {
            System.out.println("Cliente conectado no servidor " + client.getRemoteAddress());
        });

        server.addDisconnectListener(client -> {
            System.out.println("Cliente desconectado do servidor");
        });

        server.addEventListener("toServer", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                client.sendEvent("toClient", "server recieved " + data);
            }
        });

        server.addEventListener("message", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                client.sendEvent("toClient", "message from server " + data);
            }
        });

        server.addEventListener("print", String.class, (arg0, data, arg2) -> System.err.println(data));

        server.start();

        Thread.sleep(5000);

        server.getAllClients().forEach(client -> {
            client.sendEvent("toClient", "Server started");
        });

        try (final Scanner input = new Scanner(System.in)) {
            String retorno = "";

            while (!retorno.trim().equals("quit") && !retorno.trim().equals("q")) {

                if(retorno.equals("send")) {
                    server.getAllClients().forEach(client -> {
                        client.sendEvent("toClient", "Server sent message");
                    });
                }

                retorno = input.nextLine().trim();
            }
        }
        server.stop();
    }
}
