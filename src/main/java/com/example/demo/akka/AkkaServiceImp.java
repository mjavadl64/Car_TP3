package com.example.demo.akka;

import java.io.BufferedReader;
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
        ActorRef mapper1 = system.actorOf(Props.create(MapperActor.class), "mapper1");
        mapper1.tell(reducer1, ActorRef.noSender());
        mapper1.tell(reducer2, ActorRef.noSender());
        ActorRef mapper2 = system.actorOf(Props.create(MapperActor.class), "mapper2");
        mapper2.tell(reducer1, ActorRef.noSender());
        mapper2.tell(reducer2, ActorRef.noSender());
        ActorRef mapper3 = system.actorOf(Props.create(MapperActor.class), "mapper3");
        mapper3.tell(reducer1, ActorRef.noSender());
        mapper3.tell(reducer2, ActorRef.noSender());
        mappers.add(mapper1);
        mappers.add(mapper2);
        mappers.add(mapper3);

    }

    @Override
    public void submitFile(MultipartFile file) throws IOException {
        Inbox inbox = Inbox.create(system);
        if (file != null) {
            // cree un reader d'optenir les ligne du fichier
            try (InputStream fileIStream = file.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fileIStream))) {
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    // envoye chque ligne à un mapper
                    inbox.send(mappers.get(i), new RequestMessage(line));
                    i = (i + 1) % mappers.size();
                }
            }
        }
    }

    @Override
    public int occurance(String mot) {

        int counter = 0;
        Inbox inbox = Inbox.create(system);
        int reducerNumber = Math.abs(mot.hashCode()) % reducers.size() ;
        inbox.send(reducers.get(reducerNumber), new RequestMot(mot.toLowerCase()));
        Object reply = null;
        // recupere la reponse
        try {
            reply = inbox.receive(FiniteDuration.create(30, TimeUnit.SECONDS));
            if (reply instanceof ReponsMessage rpm) {
                counter = rpm.counter();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return counter;
    }

}
