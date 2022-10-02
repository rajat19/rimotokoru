package calculator.client;

import com.proto.calculator.AverageRequest;
import com.proto.calculator.AverageResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.MaxRequest;
import com.proto.calculator.MaxResponse;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.SqrtRequest;
import com.proto.calculator.SqrtResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private static void doAverage(final ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doAverage");
        final CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);

        final List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
        final CountDownLatch latch = new CountDownLatch(1);
        final StreamObserver<AverageRequest> stream = stub.average(new StreamObserver<>() {
            @Override
            public void onNext(final AverageResponse value) {
                System.out.println(value.getResult());
            }

            @Override
            public void onError(final Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        for(final int number: numbers) {
            stream.onNext(AverageRequest.newBuilder().setNumber(number).build());
        }

        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doMax(final ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doMax");
        final CalculatorServiceGrpc.CalculatorServiceStub stub = CalculatorServiceGrpc.newStub(channel);
        final CountDownLatch latch = new CountDownLatch(1);

        final StreamObserver<MaxRequest> stream = stub.max(new StreamObserver<>() {
            @Override
            public void onNext(final MaxResponse value) {
                System.out.println(value.getResult());
            }

            @Override
            public void onError(final Throwable t) {

            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        Arrays.asList(1,5,3,6,2,20).forEach(number -> {
            stream.onNext(MaxRequest.newBuilder().setNumber(number).build());
        });
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doSqrt(final ManagedChannel channel) {
        System.out.println("Enter doSqrt");
        final CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);

        SqrtResponse response = stub.sqrt(SqrtRequest.newBuilder().setNumber(25).build());
        System.out.println("Sqrt 25:: "+ response.getResult());

        try {
            response = stub.sqrt(SqrtRequest.newBuilder().setNumber(-1).build());
            System.out.println("Sqrt -1 :: "+response.getResult());
        } catch (RuntimeException e) {
            System.out.println("Got an exception");
            e.printStackTrace();
        }
    }

    public static void main(final String[] args) throws InterruptedException {
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
            case "average": doAverage(channel); break;
            case "max": doMax(channel); break;
            case "sqrt": doSqrt(channel); break;
            default:
                System.out.println("Keyword invalid" + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
