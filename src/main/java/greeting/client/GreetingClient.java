package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    private static void doGreet(final ManagedChannel channel) {
        System.out.println("Enter doGreet");
        final GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        final GreetingResponse response = stub.greet(GreetingRequest.newBuilder().setFirstName("Rajat").build());
        System.out.println("Greeting ::" + response.getResult());
    }

    private static void doGreetManyTimes(final ManagedChannel channel) {
        System.out.println("Enter doGreetManyTimes");
        final GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        stub.greetManyTimes(GreetingRequest.newBuilder().setFirstName("Rajat").build())
                .forEachRemaining(greetingResponse -> {
                    System.out.println("Greeting ::" + greetingResponse.getResult());
                });
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Need one argument to work");
            return;
        }

        final ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        switch (args[0]) {
            case "greet": doGreet(channel); break;
            case "greetManyTimes": doGreetManyTimes(channel); break;
            default:
                System.out.println("Keyword invalid" + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
