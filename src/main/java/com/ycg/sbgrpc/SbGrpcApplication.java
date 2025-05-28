package com.ycg.sbgrpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.grpc.client.ImportGrpcClients;

@SpringBootApplication
@ImportGrpcClients(basePackages = {"com.ycg"}, target = "default-channel")
public class SbGrpcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SbGrpcApplication.class, args);
    }

}
