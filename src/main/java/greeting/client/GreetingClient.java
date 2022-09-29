package greeting.client;

import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
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
        System.out.println("Greeting " + response.getResult());
    }

    private static void doSum(final ManagedChannel channel) {
        System.out.println("Enter doSum");
        final CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        final CalculatorResponse response = stub.sum(CalculatorRequest.newBuilder().setFirstNumber(3).setSecondNumber(10).build());
        System.out.println(response.getResult());
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
            case "sum": doSum(channel); break;
            default:
                System.out.println("Keyword invalid" + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
