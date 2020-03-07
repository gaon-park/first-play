package repository;

import akka.actor.ActorSystem;
import play.libs.concurrent.CustomExecutionContext;

import javax.inject.Inject;

public class DatabaseExecutionContext extends CustomExecutionContext {

    private static final String name = "database.dispatcher";

    public DatabaseExecutionContext(ActorSystem actorSystem, String name) {
        super(actorSystem, name);
    }

    @Inject
    public DatabaseExecutionContext(ActorSystem actorSystem) {
        super(actorSystem, "database.dispatcher");
    }
}