package home.spring.vertx.sync.endpoint;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.sync.Sync;

import java.util.Map;

/**
 * Created by alex on 9/30/2015.
 */
public class JsonVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        super.start();
        Map<String, Object> handlers = VertxConfig.ctx.getBeansWithAnnotation(VertxEndpoint.class);
        EventBus eventBus = vertx.eventBus();
        for(Map.Entry<String, Object> handlerEntry : handlers.entrySet()){
            VertxEndpoint vertxSetting= handlerEntry.getValue().getClass().getAnnotation(VertxEndpoint.class);
            if (Endpoint.class.isAssignableFrom(handlerEntry.getValue().getClass())) {
                Endpoint endpointService = (Endpoint) handlerEntry.getValue();
                if(vertxSetting.blocking()){
                    eventBus.consumer(vertxSetting.path().getValue()).handler(blockingHandler(endpointService));
                }else {
                    eventBus.consumer(vertxSetting.path().getValue()).handler( Sync.fiberHandler(msg -> {
                        try {
                            endpointService.processRequest(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                            msg.reply(e.getCause());
                        }
                    }));
                }
            }
        }
    }

    private Handler<Message<Object>> blockingHandler(Endpoint service) {
        return Sync.fiberHandler(msg -> vertx.<Object>executeBlocking(future -> {
                try {
                    service.processRequest(msg);
                    future.complete(msg.body());
                } catch (Exception e) {
                    future.fail(e);
                }
                },false,
                result -> {
                    if (result.succeeded()) {
                        msg.reply(result.result());
                    } else {
                        msg.reply(result.cause().toString());
                    }
                }));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}
