package com.example.send.grpc_demo;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreeterClient {
    public static void main(String[] args) {
        // Tạo kênh giao tiếp đến server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()  // Sử dụng kênh văn bản đơn giản để giao tiếp (không mã hóa)
                .build();

        // Tạo client stub từ kênh
        GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);

        // Tạo yêu cầu
        HelloRequest request = HelloRequest.newBuilder().setName("YourName").build();

        // Gọi phương thức từ client
        HelloReply reply = stub.sayHello(request);

        // In kết quả từ server
        System.out.println("Response from server: " + reply.getMessage());

        // Đóng kênh giao tiếp
        channel.shutdown();
    }
}
