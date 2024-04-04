package com.example.demo.akka;

import java.util.HashMap;
import java.util.Map;

import akka.actor.UntypedActor;

public class ReducerActor extends UntypedActor {

    private Map<String, Integer> motOccurances = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof RequestMessage rq) {
            String mot = rq.msg();
            // Metre à jour le mapper pour chaque mot qui viens.
            motOccurances.put(mot, motOccurances.getOrDefault(mot, 0) + 1);
        }

        if (message instanceof RequestMot rqm) {
            String mot = rqm.mot();
            // envoier la reponse à AkkaServiceImpl
            if (motOccurances.containsKey(mot)) {
                int i = motOccurances.get(mot);
                // System.out.println(i);
                getSender().tell(new ReponsMessage(motOccurances.get(mot)), getSelf());
            } else {
                getSender().tell(new ReponsMessage(0), getSelf());
            }
        }

    }

}
