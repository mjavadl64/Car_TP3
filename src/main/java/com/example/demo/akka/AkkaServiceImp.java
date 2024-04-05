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

    ActorSystem systemMapper = ActorSystem.create("systemMapper");
    ActorSystem systemReducer = ActorSystem.create("systemReducer");

    private List<ActorRef> mappers = new ArrayList<>();
    private List<ActorRef> reducers = new ArrayList<>();
    

   
    @Override
    public void init() {

        for (int i = 1; i<3 ; i++){
            ActorRef reducer = systemReducer.actorOf(Props.create(ReducerActor.class),"reducer"+i);
            this.reducers.add(reducer);
        }

        for (int i = 1; i<4; i++){
            ActorRef mapper = systemMapper.actorOf(Props.create(MapperActor.class),"mapper"+i);
            mapper.tell(reducers.get(0),ActorRef.noSender());
            mapper.tell(reducers.get(1),ActorRef.noSender());
            this.mappers.add(mapper);
        }
    

    }

    @Override
    public void submitFile(MultipartFile file) throws IOException {
        Inbox inbox = Inbox.create(systemMapper);
        if (file != null) {
            // cree un reader d'optenir les ligne du fichier
            try (InputStream fileIStream = file.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fileIStream))) {
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    // envoye chaque ligne à un mapper
                    inbox.send(mappers.get(i), new RequestMessage(line));
                    i = (i + 1) % mappers.size();
                }
            }
        }
    }

    @Override
    public int occurance(String mot) {

        int counter = 0;
        Inbox inbox = Inbox.create(systemReducer);
        //reducerNumber pour savoir à quel reducer je vais demander le mot
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
