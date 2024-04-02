package com.example.demo.akka;

import java.io.IOException;

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
    public String init() {
        akkaService.init();
        System.out.println("<Servers sont initialises>");
        return "redirect:/akka/home";

    }

    @PostMapping("/addFichier")
    public String addFichier(@RequestParam("file") MultipartFile file) throws IOException {
        // verifier si le fichier est vide, on fait rien.
        if (file.isEmpty()) {
            return "redirect:/akka/home";
        }
        // cree le chemain pour sauvgarder le fichier
        String filePath = System.getProperty("user.dir");
        akkaService.submitFile(file, filePath);
        return "redirect:/akka/home";
    }

    @PostMapping("/serachMot")
    public String serchMot(@RequestParam String mot) {
        // verifier si le mot est vid, on fait rien.
        if (mot.isEmpty()) {
            return "redirect:/akka/home";
        }
        int count = akkaService.occurance(mot);
        return "redirect:/akka/home";

    }

}
