package com.ycg.sbgrpc.igrpc.server;

import com.ycg.sbgrpc.grpc.user.GetUserInfoRequest;
import com.ycg.sbgrpc.grpc.user.GetUserInfoResponse;
import com.ycg.sbgrpc.grpc.user.UserGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;

@GrpcService
@Slf4j
public class GreeterServiceImpl extends UserGrpc.UserImplBase {


    @Override
    public void getUserInfo(GetUserInfoRequest request, StreamObserver<GetUserInfoResponse> responseObserver) {
        GetUserInfoResponse response = GetUserInfoResponse.newBuilder()
                .setUserId(request.getUserId())
                .setUserName("User_" + request.getUserId())
                .setEmail("user" + request.getUserId() + "@example.com")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GetUserInfoRequest> getUserInfoStreamClient(StreamObserver<GetUserInfoResponse> responseObserver) {
        return new StreamObserver<>() {
            List<GetUserInfoRequest> requests = new ArrayList<>();

            @Override
            public void onNext(GetUserInfoRequest value) {
                requests.add(value);
            }

            @Override
            public void onCompleted() {
                // 简单示例：返回第一个用户作为响应
                GetUserInfoRequest first = requests.get(0);
                responseObserver.onNext(GetUserInfoResponse.newBuilder()
                        .setUserId(first.getUserId())
                        .setUserName("Batch_User_" + first.getUserId())
                        .setEmail("batch_user" + first.getUserId() + "@example.com")
                        .build());
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }
        };
    }

    @Override
    public void getUserInfoStreamServer(GetUserInfoRequest request, StreamObserver<GetUserInfoResponse> responseObserver) {
        for (int i = 0; i < 3; i++) {
            responseObserver.onNext(GetUserInfoResponse.newBuilder()
                    .setUserId(request.getUserId())
                    .setUserName("Streamed_User_" + request.getUserId() + "_Part_" + i)
                    .setEmail("streamed" + i + "@example.com")
                    .build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<GetUserInfoRequest> getUserInfoBidirectional(StreamObserver<GetUserInfoResponse> responseObserver) {
        return new StreamObserver<>() {
            @Override
            public void onNext(GetUserInfoRequest request) {
                log.info("server-接收到: {}", request.getUserId());
                GetUserInfoResponse response = GetUserInfoResponse.newBuilder()
                        .setUserId(request.getUserId())
                        .setUserName("BiUser_" + request.getUserId())
                        .setEmail("server@example.com")
                        .build();
                // 发送响应
                responseObserver.onNext(response);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }
        };
    }
}
