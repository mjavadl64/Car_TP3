package com.example.demo.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class MapperActor extends UntypedActor {

    ActorSystem system = ActorSystem.create();
    Inbox inbox = Inbox.create(system);
    private ActorRef reducer1 = system.actorOf(Props.create(ReducerActor.class), "reducer1");
    private ActorRef reducer2 = system.actorOf(Props.create(ReducerActor.class), "reducer2");

    public MapperActor(ActorRef reducer1, ActorRef reducer2) {
        this.reducer1 = reducer1;
        this.reducer2 = reducer2;
    }

    @Override
    public void onReceive(Object message) throws Throwable {

        if (message instanceof RequestMessage rq) {
            String[] mots = rq.msg().split("\\s+");

            // envoie premier mot aux deux reducers de ligne, c'est un mot qu'on veux le
            // envoie les mots aux reducers
            for (String mot : mots) {
                // pour envoie chaque mot à chaque reducer on cree reducerNumber en fonction de
                // hashCode qui est 1 ou 2
                int reducerNumber = Math.abs(mot.hashCode()) % 2 + 1;
                if (reducerNumber == 1) {
                    reducer1.tell(new RequestMessage(mot), getSelf());
                } else if (reducerNumber == 2) {
                    reducer2.tell(new RequestMessage(mot), getSelf());
                }

            }
        }

        if (message instanceof RequestMot rqm) {
            String mot = rqm.mot();
            int reducerNumber = Math.abs(mot.hashCode()) % 2 + 1;
            if (reducerNumber == 1) {
                reducer1.tell(new RequestMot(mot), getSelf());
            } else if (reducerNumber == 2) {
                reducer2.tell(new RequestMot(mot), getSelf());
            }
        }

    }
}
