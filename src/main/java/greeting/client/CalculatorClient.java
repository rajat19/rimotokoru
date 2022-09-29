package greeting.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {

    public static final int PRIME_VALUE = 120;

    private static void doSum(final ManagedChannel channel) {
        System.out.println("Enter doSum");
        final CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        final SumResponse response = stub.sum(SumRequest.newBuilder().setFirstNumber(3).setSecondNumber(10).build());
        System.out.println(response.getResult());
    }

    private static void doPrime(final ManagedChannel channel) {
        System.out.println("Enter doPrime");
        final CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        stub.prime(PrimeRequest.newBuilder().setNumber(PRIME_VALUE).build())
                .forEachRemaining(primeResponse -> {
                    System.out.println(primeResponse.getResult());
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
            case "sum": doSum(channel); break;
            case "prime": doPrime(channel); break;
            default:
                System.out.println("Keyword invalid" + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
