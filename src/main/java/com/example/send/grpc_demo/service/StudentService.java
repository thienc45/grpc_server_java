package com.example.send.grpc_demo.service;





import com.example.send.grpc_student.Empty;
import com.example.send.grpc_student.PointRequest;
import com.example.send.grpc_student.PointResponse;
import com.example.send.grpc_student.Student;
import com.example.send.grpc_student.StudetnPointGrpc;
import com.example.send.grpc_student.UserList;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@GrpcService
public class StudentService extends  StudetnPointGrpc.StudetnPointImplBase {


    private final List<Student> students = new ArrayList<>();

    public StudentService() {
        // Tạo danh sách các đối tượng Student ảo
        students.add(Student.newBuilder().setId(1).setPoint(85.0).build());
        students.add(Student.newBuilder().setId(2).setPoint(90.0).build());
        students.add(Student.newBuilder().setId(3).setPoint(78.0).build());
    }

    @Override
    public void getUsers(Empty request, StreamObserver<UserList> responseObserver) {
        // Xây dựng phản hồi UserList
        UserList.Builder responseBuilder = UserList.newBuilder();
        responseBuilder.addAllStudent(students);

        // Gửi phản hồi lại cho client
        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void addPoints(PointRequest request, StreamObserver<PointResponse> responseObserver) {
        double id = request.getId();
        Optional<Student> optionalStudent = students.stream().filter(student -> student.getId() == id).findFirst();

        PointResponse.Builder responseBuilder = PointResponse.newBuilder();
        if (optionalStudent.isPresent()) {
            // Nếu tìm thấy student, cập nhật điểm của student đó
            Student student = optionalStudent.get();
            Student updatedStudent = Student.newBuilder(student).setPoint(student.getPoint() + request.getPoint()).build();
            students.set(students.indexOf(student), updatedStudent);
            responseBuilder.setId(updatedStudent.getId()).setPoint(updatedStudent.getPoint());
        } else {
            // Nếu không tìm thấy student, tạo mới một student và thêm vào danh sách
            Student newStudent = Student.newBuilder().setId(id).setPoint(request.getPoint()).build();
            students.add(newStudent);
            responseBuilder.setId(newStudent.getId()).setPoint(newStudent.getPoint());
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }


    @Override
    public void subtractPoints(PointRequest request, StreamObserver<PointResponse> responseObserver) {
        double id = request.getId();
        Optional<Student> optionalStudent = students.stream().filter(student -> student.getId() == id).findFirst();

        PointResponse.Builder responseBuilder = PointResponse.newBuilder();
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            double newPoint = student.getPoint() - request.getPoint();
            if (newPoint < 0) newPoint = 0;
            Student updatedStudent = Student.newBuilder(student).setPoint(newPoint).build();
            students.set(students.indexOf(student), updatedStudent);
            responseBuilder.setId(updatedStudent.getId()).setPoint(updatedStudent.getPoint()).setMessage("Points subtracted successfully.");
        } else {
            responseBuilder.setMessage("Student not found.");
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

}
