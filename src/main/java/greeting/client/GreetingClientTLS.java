package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ChannelCredentials;
import io.grpc.Deadline;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.TlsChannelCredentials;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClientTLS {

    private static void doGreet(final ManagedChannel channel) {
        System.out.println("Enter doGreet");
        final GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        final GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Rajat").build());
        System.out.println("Greeting ::" + response.getResult());
    }

    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        final ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .trustManager(new File("ssl/ca.crt"))
                .build();
        final ManagedChannel channel = Grpc
                .newChannelBuilderForAddress("localhost", 50051, credentials)
                .build();

        switch (args[0]) {
            case "greet": doGreet(channel); break;
            default:
                System.out.println("Keyword invalid" + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
