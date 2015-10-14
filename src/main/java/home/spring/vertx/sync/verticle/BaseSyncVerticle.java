package home.spring.vertx.sync.verticle;

import co.paralleluniverse.fibers.Suspendable;
import home.spring.vertx.sync.endpoint.direct.Endpoint;
import home.spring.vertx.sync.endpoint.direct.VertxEndpoint;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.sync.SyncVerticle;

import java.util.Map;

import static io.vertx.ext.sync.Sync.fiberHandler;

/**
 * Created by alex on 9/28/2015.
 */
public class BaseSyncVerticle extends SyncVerticle {

    private Map<String, Object> handlers;

    public BaseSyncVerticle(Map<String, Object> handlers){
        this.handlers = handlers;
    }

    @Override
    @Suspendable
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);

    }

    @Override
    @Suspendable
    public void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture);
    }

    @Override
    @Suspendable
    public void start() throws Exception {
        super.start();
        EventBus eventBus = vertx.eventBus();
        for (Map.Entry<String, Object> handlerEntry : handlers.entrySet()) {
            VertxEndpoint vertxSetting = handlerEntry.getValue().getClass().getAnnotation(VertxEndpoint.class);
            if (Endpoint.class.isAssignableFrom(handlerEntry.getValue().getClass())) {
                Endpoint endpointService = (Endpoint) handlerEntry.getValue();
                eventBus.consumer(HttpMethod.GET.name()+"_"+vertxSetting.path().getValue()).handler(fiberHandler(msg -> {
                    try {
                        endpointService.processGet(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg.reply(e.getCause());
                    }
                }));
                eventBus.consumer(HttpMethod.POST.name()+"_"+vertxSetting.path().getValue()).handler(fiberHandler(msg -> {
                    try {
                        endpointService.processPost(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        msg.reply(e.getCause());
                    }
                }));
            }
        }
    }

    @Override
    @Suspendable
    public void stop() throws Exception {
        super.stop();
    }
}

