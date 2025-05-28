package com.ycg.sbgrpc.controller;

import com.ycg.sbgrpc.igrpc.client.MyCanalAsyncClientService;
import com.ycg.sbgrpc.igrpc.client.UserAsyncClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/grpc/client")
@Slf4j
@RequiredArgsConstructor
public class GrpcClientController {

    private final UserAsyncClientService userAsyncClientService;

    private final MyCanalAsyncClientService myCanalAsyncClientService;

    @GetMapping("/callService")
    public ResponseEntity<String> callGrpcService() {
        userAsyncClientService.callAsyncBidirectional();
        return ResponseEntity.ok("gRPC service called successfully");
    }

    @GetMapping("/callCanal")
    public ResponseEntity<String> callCanalGrpcService() {
        myCanalAsyncClientService.callAsyncBidirectional();
        return ResponseEntity.ok("gRPC service called successfully");
    }
}
