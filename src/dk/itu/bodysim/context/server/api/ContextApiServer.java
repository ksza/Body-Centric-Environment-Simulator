package dk.itu.bodysim.context.server.api;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ContextApiServer extends Application {

    /**
     * Creates a root Restlet that will receive all incoming calls.
     */
    @Override
    public synchronized Restlet createInboundRoot() {
        // Create a router Restlet that routes each call to a
        // new instance of ApiContextResource.
        Router router = new Router(getContext());

        // Defines only one route
        router.attach("/set", ApiContextResource.class);

        return router;
    }
}
