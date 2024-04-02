package com.example.demo.akka;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import akka.actor.UntypedActor;
import scala.concurrent.duration.FiniteDuration;

public class MapperActor extends UntypedActor {

    ActorSystem system = ActorSystem.create();
    private ActorRef reducer1 = system.actorOf(Props.create(ReducerActor.class), "reducer1");
    private ActorRef reducer2 = system.actorOf(Props.create(ReducerActor.class), "reducer2");
    MotCle motCle = new MotCle();

    public MapperActor(ActorRef reducer1, ActorRef reducer2) {
        this.reducer1 = reducer1;
        this.reducer2 = reducer2;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        Inbox inbox = Inbox.create(system);
        int counter = 0;
        Object reply = null;
        String[] mots;
        if (message instanceof RequestMessage rq) {
            mots = rq.msg().split("\\s+");
            motCle.setMot(mots[0]);
            // envoie premier mot aux deux reducers de ligne, c'est un mot qu'on veux le
            // charcher dans une ligne
            // reducer1.tell(new RequestMot(mots[0]), getSelf());
            // reducer2.tell(new RequestMot(mots[0]), getSelf());

            // enleve premier mot aux lists des mots
            mots = Arrays.copyOfRange(mots, 1, mots.length);

            // envoie les mots aux reducers
            for (String mot : mots) {
                // pour envoie chaque mot à chaque reducer on a reducerNumber qui aura 1 ou 2
                int reducerNumber = Math.abs(mot.hashCode()) % 2 + 1;
                if (reducerNumber == 1) {
                    reducer1.tell(new RequestMessage(mot), getSelf());
                } else if (reducerNumber == 2) {
                    reducer2.tell(new RequestMessage(mot), getSelf());
                }
                try {
                    reply = inbox.receive(FiniteDuration.create(30, TimeUnit.SECONDS));
                    if (reply instanceof ReponsMessage rp) {
                        System.out.println(counter);
                        counter += rp.counter();
                    }
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

            }
        }

    }
}
