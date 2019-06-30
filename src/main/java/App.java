import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class App {

    static Vertx v;

    public static void main(String[] args) {

        v = Vertx.vertx();

        deployVerticle(VertxEventBusBridge.class.getCanonicalName(), new DeploymentOptions());

    }
    private static void deployVerticle(String name, DeploymentOptions options) {
        v.deployVerticle(name, options, status -> {
            if (status.failed()) {
                System.out.println("ERROR: " + name);
                status.cause().printStackTrace();
            }
        });
    }

}
