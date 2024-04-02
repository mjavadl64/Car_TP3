package com.example.demo.akka;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AkkaService {

    void init();

    void submitFile(MultipartFile file, String uploadDir) throws IOException;

    int occurance(String nom);
}
