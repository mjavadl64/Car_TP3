package com.example.demo.akka;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/akka")
public class AkkaController {

    @Autowired
    private AkkaService akkaService;

    @GetMapping("/home")
    public String home() {
        return "akka/home";

    }

    @PostMapping("/init")
    public String init(){
        akkaService.init();
        System.out.println("<Servers sont initialises>");
        return "redirect:/akka/home";
    
    }

    @PostMapping("/addFichier")
    public String addFichier(@RequestParam("file") MultipartFile file){
        //verifier si le file est vide.
        if (file.isEmpty()){
            return "redirect:/akka/home";
        }


        return "redirect:/akka/home";
    }
    



}
