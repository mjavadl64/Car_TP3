package com.example.demo.akka;

import akka.actor.UntypedActor;

public class ReducerActor extends UntypedActor {

    private String mot, motcherche;
    int counter = 0;

    public void method() {
        MotCle motCle = new MotCle();
        this.motcherche = motCle.getMot();
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof RequestMessage rq) {
            this.mot = rq.msg();
        }

        if (mot.equals(motcherche)) {
            // envoier la reponse à AkkaServiceImpl
            getSender().tell(new ReponsMessage(counter + 1), getSelf());
        } else {
            getSender().tell(new ReponsMessage(counter), getSelf());
        }

    }

}
