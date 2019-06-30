import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.Date;

public class VertxEventBusBridge extends AbstractVerticle  {

    public void start() {

        Vertx v = this.vertx;
        Router router = Router.router(this.vertx);
        EventBus e = this.vertx.eventBus();

        BridgeOptions opts = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex("in.*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("out.*"));



        SockJSHandler ebHandler = SockJSHandler.create(this.vertx).bridge(opts);


        router.route("/eb/*").handler(ebHandler);
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(10062);


        ///sends a message to everywhere every second.
        v.setPeriodic(1000, tick -> {
           e.publish("out.periodicMessage", new JsonObject().put("date", new Date().getTime()));
           //System.out.println("published messsage");
        });

        ///replies to an incoming message sent to in.replyToMe
        e.consumer("in.replyToMe", message -> {
           message.reply(new JsonObject().put("replyingTo", message.body()));
           System.out.println("replied to messsage");
        });

    }
}
