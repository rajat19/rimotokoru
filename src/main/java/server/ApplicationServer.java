package server;

import calculator.service.CalculatorServiceImpl;
import greeting.service.GreetingServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class ApplicationServer {

    public static void main(final String[] args) throws IOException, InterruptedException {
        final int port = 50051;

        final Server server = ServerBuilder
                .forPort(port)
                .addService(new GreetingServiceImpl())
                .addService(new CalculatorServiceImpl())
                .addService(ProtoReflectionService.newInstance())
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
