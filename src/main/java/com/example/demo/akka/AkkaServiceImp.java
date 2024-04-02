package com.example.demo.akka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import scala.concurrent.duration.FiniteDuration;

@Service
public class AkkaServiceImp implements AkkaService {

    ActorSystem system = ActorSystem.create("Mysystem");

    private List<ActorRef> mappers;
    private List<ActorRef> reducers;
    private String filePath;

    @Override
    public void init() {

        mappers = new ArrayList<>();
        reducers = new ArrayList<>();

        // Creez 2 reduceres et ajouter dans la liste reducers
        ActorRef reducer1 = system.actorOf(Props.create(ReducerActor.class), "reducer1");
        ActorRef reducer2 = system.actorOf(Props.create(ReducerActor.class), "reducer2");
        reducers.add(reducer1);
        reducers.add(reducer2);

        // Creez 3 mapper en liant avec deux reducers et ajouter dans la liste mappers
        ActorRef mapper1 = system.actorOf(Props.create(MapperActor.class, reducer1, reducer2), "mapper1");
        ActorRef mapper2 = system.actorOf(Props.create(MapperActor.class, reducer1, reducer2), "mapper2");
        ActorRef mapper3 = system.actorOf(Props.create(MapperActor.class, reducer1, reducer2), "mapper3");
        mappers.add(mapper1);
        mappers.add(mapper2);
        mappers.add(mapper3);

    }

    @Override
    public void submitFile(MultipartFile file, String uploadDir) {

        // Dans cette methode, on sauvgarde le fichier pour l'utiliser dans la methode
        // occurance
        // Obtient le nom du fichier d'origine
        String originalFileName = file.getOriginalFilename();

        // Crée le chemin pour le fichier sur le serveur
        filePath = uploadDir + File.separator + originalFileName;

        // Transfère le contenu du fichier multipart vers le fichier de destination
        try {
            file.transferTo(new File(filePath));
        } catch (IllegalStateException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int occurance(String mot) {

        Inbox inbox = Inbox.create(system);
        int counter = 0;
        // Object reply = null;
        // cree un copy de fichier pour envoyer aux mappers
        File file = new File(filePath);
        if (file != null) {
            // cree un reader d'optenir les ligne du fichier
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    // envoye chque ligne à un mapper
                    inbox.send(mappers.get(i), new RequestMessage(mot + " " + line));
                    i = (i + 1) % mappers.size();
                    // recupere la reponse
                    // try {
                    // reply = inbox.receive(FiniteDuration.create(30, TimeUnit.SECONDS));
                    //////////// }

                }

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        System.out.println(counter);
        return counter;
    }

}
