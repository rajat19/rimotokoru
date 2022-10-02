package greeting.client;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

    private static void doLongGreet(final ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doLongGreet");
        final GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);

        final List<String> names = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);

        Collections.addAll(names, "Rajat", "Abhinav", "Prashant");

        final StreamObserver<GreetingRequest> streamObserver = stub.longGreet(new StreamObserver<>() {
            @Override
            public void onNext(final GreetingResponse value) {
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

        for(final String name: names) {
            streamObserver.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        }

        streamObserver.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doGreetEveryone(final ManagedChannel channel) throws InterruptedException {
        System.out.println("Enter doGreetEveryone");
        final GreetingServiceGrpc.GreetingServiceStub stub = GreetingServiceGrpc.newStub(channel);
        final CountDownLatch latch = new CountDownLatch(1);

        final StreamObserver<GreetingRequest> stream = stub.greetEveryone(new StreamObserver<GreetingResponse>() {
            @Override
            public void onNext(final GreetingResponse value) {
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

        Arrays.asList("Rajat", "Abhinav", "Prashant").forEach(name -> {
            stream.onNext(GreetingRequest.newBuilder().setFirstName(name).build());
        });
        stream.onCompleted();
        latch.await(3, TimeUnit.SECONDS);
    }

    private static void doGreetWithDeadline(final ManagedChannel channel) {
        System.out.println("Enter doGreetWithDeadline");
        final GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingResponse response = stub
                .withDeadline(Deadline.after(3, TimeUnit.SECONDS))
                .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Rajat").build());

        System.out.println("Greeting within deadline:: "+response.getResult());

        try {
            response = stub
                    .withDeadline(Deadline.after(100, TimeUnit.MILLISECONDS))
                    .greetWithDeadline(GreetingRequest.newBuilder().setFirstName("Rajat").build());
            System.out.println("Greeting deadline exceeded"+response.getResult());
        } catch (final StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                System.out.println("Deadline has exceeded");
            } else {
                System.out.println("Got an exception in greetWithDeadline");
                e.printStackTrace();
            }
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
            case "greet": doGreet(channel); break;
            case "greetManyTimes": doGreetManyTimes(channel); break;
            case "longGreet": doLongGreet(channel); break;
            case "greetEveryone": doGreetEveryone(channel); break;
            case "greetWithDeadline": doGreetWithDeadline(channel); break;
            default:
                System.out.println("Keyword invalid" + args[0]);
        }

        System.out.println("Shutting down");
        channel.shutdown();
    }
}
