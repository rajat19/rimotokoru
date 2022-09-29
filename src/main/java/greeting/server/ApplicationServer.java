package greeting.server;

import greeting.server.impl.CalculatorServiceImpl;
import greeting.server.impl.GreetingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class ApplicationServer {

    public static void main(final String[] args) throws IOException, InterruptedException {
        final int port = 50051;

        final Server server = ServerBuilder
                .forPort(port)
                .addService(new GreetingServiceImpl())
                .addService(new CalculatorServiceImpl())
                .build();
        server.start();

        System.out.println("Server started");
        System.out.println("Listening on port :: "+port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received shutdown request");
            server.shutdown();
            System.out.println("Server stopped");
        }));

        server.awaitTermination();
    }
}
