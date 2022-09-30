package greeting.service;

import com.proto.calculator.AverageRequest;
import com.proto.calculator.AverageResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.MaxRequest;
import com.proto.calculator.MaxResponse;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.PrimeResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import com.proto.greeting.GreetingResponse;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {

    @Override
    public void sum(final SumRequest request, final StreamObserver<SumResponse> responseObserver) {
        final int result = request.getFirstNumber() + request.getSecondNumber();
        responseObserver.onNext(SumResponse.newBuilder().setResult(result).build());
        responseObserver.onCompleted();
    }

    @Override
    public void prime(final PrimeRequest request, final StreamObserver<PrimeResponse> responseObserver) {
        int k = 2;
        int number = request.getNumber();
        while (number > 1) {
            if (number % k == 0) {
                responseObserver.onNext(PrimeResponse.newBuilder().setResult(k).build());
                number /= k;
            } else {
                k++;
            }
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<AverageRequest> average(final StreamObserver<AverageResponse> responseObserver) {
        final int[] sum = {0};
        final int[] count = {0};
        return new StreamObserver<>() {
            @Override
            public void onNext(final AverageRequest value) {
                sum[0] += value.getNumber();
                count[0]++;
            }

            @Override
            public void onError(final Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(AverageResponse.newBuilder().setResult(1.0 * sum[0] / count[0]).build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<MaxRequest> max(final StreamObserver<MaxResponse> responseObserver) {
        final int[] max = {Integer.MIN_VALUE};
        return new StreamObserver<>() {
            @Override
            public void onNext(final MaxRequest value) {
                if (value.getNumber() > max[0]) {
                    max[0] = value.getNumber();
                    responseObserver.onNext(MaxResponse.newBuilder().setResult(max[0]).build());
                }
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
