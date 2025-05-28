package com.ycg.sbgrpc.igrpc.client;

import com.ycg.sbgrpc.grpc.user.GetUserInfoRequest;
import com.ycg.sbgrpc.grpc.user.GetUserInfoResponse;
import com.ycg.sbgrpc.grpc.user.UserGrpc;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAsyncClientService {

    @Resource
    private UserGrpc.UserStub userStub;

    public void callAsyncBidirectional() {
        var responseObserver = new StreamObserver<GetUserInfoResponse>() {
            @Override
            public void onNext(GetUserInfoResponse value) {
                log.info("client-异步next-接收: {}", value);
            }

            @Override
            public void onError(Throwable t) {
                log.error("client-错误: {}", t.getMessage());
            }

            @Override
            public void onCompleted() {
                log.info("client-异步完成");
            }
        };

        StreamObserver<GetUserInfoRequest> requestObserver = userStub.getUserInfoBidirectional(responseObserver);
        for (int i = 0; i < 10; i++) {
            requestObserver.onNext(GetUserInfoRequest.newBuilder().setUserId(2).build());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 结束流
        requestObserver.onCompleted();

        log.info("一元异步完成: {}", requestObserver);
    }


}
