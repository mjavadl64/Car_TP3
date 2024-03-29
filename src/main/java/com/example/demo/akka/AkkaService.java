package com.example.demo.akka;

import java.io.File;

public interface AkkaService {

    void init();

    void submit(File file);

    int occurance(String nom);
}
