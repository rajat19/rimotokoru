package greeting.server.impl;

import com.proto.calculator.CalculatorRequest;
import com.proto.calculator.CalculatorResponse;
import com.proto.calculator.CalculatorServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(final CalculatorRequest request, final StreamObserver<CalculatorResponse> responseObserver) {
        final int result = request.getFirstNumber() + request.getSecondNumber();
        responseObserver.onNext(CalculatorResponse.newBuilder().setResult("The result is :: "+ result).build());
        responseObserver.onCompleted();
    }
}
