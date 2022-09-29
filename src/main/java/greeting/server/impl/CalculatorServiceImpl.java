package greeting.server.impl;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeRequest;
import com.proto.calculator.PrimeResponse;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
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
}
