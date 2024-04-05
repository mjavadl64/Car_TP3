package com.example.demo.akka;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.UntypedActor;

public class MapperActor extends UntypedActor {

    
    ActorSystem system = ActorSystem.create();
    Inbox inbox = Inbox.create(system);
    private List<ActorRef> reducers ;

    public MapperActor(){
        this.reducers = new ArrayList<ActorRef>();
    }


    @Override
    public void onReceive(Object message) throws Throwable {

        if ( message instanceof ActorRef reducer){
            // verifier reducer s'il n'est pas null, j'ajout dans la list de reducers
            if (reducer != null)
                this.reducers.add(reducer);
            }        
        

        if (message instanceof RequestMessage rq) {
            //decouper la ligne par mots
            String[] mots = rq.msg().split("\\s+");
            // envoie les mots aux reducers en fonction hashCode qui est 1 ou 2
            for (String mot : mots) {
                int reducerNumber = Math.abs(mot.hashCode()) % reducers.size() ;
                reducers.get(reducerNumber).tell(new RequestMessage(mot.toLowerCase()), getSelf());
            }

          }

    }
}
