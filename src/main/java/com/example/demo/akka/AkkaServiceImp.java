package com.example.demo.akka;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

@Service
public class AkkaServiceImp implements AkkaService {


    private List<ActorRef> actors;
    private List<ActorRef> reducers;
    
    

    @Override
    public void init() {
      
        
        ActorSystem system = ActorSystem.create("Mysystem");
        actors = new ArrayList<ActorRef>();
        reducers = new ArrayList<ActorRef>();

        //Creez 3 mapper et ajouter dans la liste actors
        ActorRef mapper1 = system.actorOf(Props.create(MapperActor.class),"mapper1");
        actors.add(mapper1);
        ActorRef mapper2 = system.actorOf(Props.create(MapperActor.class),"mapper2");
        actors.add(mapper2);
        ActorRef mapper3 = system.actorOf(Props.create(MapperActor.class),"mapper3");
        actors.add(mapper3);

        //Creez 2 reduceres et ajouter dans la liste reducers
        ActorRef reducer1 = system.actorOf(Props.create(MapperActor.class),"reducer1");
        reducers.add(reducer1);
        ActorRef reducer2 = system.actorOf(Props.create(MapperActor.class),"reducer2");
        reducers.add(reducer2);

        //Liee les mappers aux redicers
        mapper1.tell(reducer1,ActorRef.noSender());
        mapper1.tell(reducer2,ActorRef.noSender());
        mapper2.tell(reducer1,ActorRef.noSender());
        mapper2.tell(reducer2,ActorRef.noSender());
        mapper3.tell(reducer1,ActorRef.noSender());
        mapper3.tell(reducer2,ActorRef.noSender());  
       
    }

    @Override
    public void submit(File file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'submit'");
    }

    @Override
    public int occurance(String mot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'occurance'");
    }
    

}
