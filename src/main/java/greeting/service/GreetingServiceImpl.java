package greeting.service;

import com.proto.greeting.GreetingRequest;
import com.proto.greeting.GreetingResponse;
import com.proto.greeting.GreetingServiceGrpc;
import io.grpc.stub.StreamObserver;

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {

    @Override
    public void greet(final GreetingRequest request, final StreamObserver<GreetingResponse> responseObserver) {
        responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(final GreetingRequest request, final StreamObserver<GreetingResponse> responseObserver) {
        final GreetingResponse response = GreetingResponse.newBuilder().setResult("Hello " + request.getFirstName()).build();
        for(int i = 0; i< 10; i++) {
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GreetingRequest> longGreet(final StreamObserver<GreetingResponse> responseObserver) {
        final StringBuilder sb = new StringBuilder();
        return new StreamObserver<>() {
            @Override
            public void onNext(final GreetingRequest value) {
                sb.append("Hello ")
                        .append(value.getFirstName())
                        .append("!\n");
            }

            @Override
            public void onError(final Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult(sb.toString()).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<GreetingRequest> greetEveryone(final StreamObserver<GreetingResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(final GreetingRequest value) {
                responseObserver.onNext(GreetingResponse.newBuilder().setResult("Hello "+value.getFirstName()).build());
            }

            @Override
            public void onError(final Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
