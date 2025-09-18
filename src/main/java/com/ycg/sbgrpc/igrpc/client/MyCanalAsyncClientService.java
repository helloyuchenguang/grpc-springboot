package com.ycg.sbgrpc.igrpc.client;

import com.ycg.sbgrpc.grpc.mycanal.EventTableRowReply;
import com.ycg.sbgrpc.grpc.mycanal.MyCanalServiceGrpc;
import com.ycg.sbgrpc.grpc.mycanal.SubscribeTableRequest;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyCanalAsyncClientService {

    @Resource
    private MyCanalServiceGrpc.MyCanalServiceStub myCanalServiceStub;

    public void callAsyncBidirectional() {
        // 原子long
        var request = SubscribeTableRequest.newBuilder()
                .build();
        var responseObserver = new StreamObserver<EventTableRowReply>() {
            long count = 0;

            @Override
            public void onNext(EventTableRowReply value) {
                log.info(" {} client-异步next-接收: {}", ++count, LocalDateTime.now());
//                System.out.println("client-异步next-接收: " + LocalDateTime.now() + " - " +  ++count );
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

        myCanalServiceStub.subscribeRegexTable(request, responseObserver);
    }


}
