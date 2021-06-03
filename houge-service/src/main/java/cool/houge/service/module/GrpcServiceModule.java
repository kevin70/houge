package cool.houge.service.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.typesafe.config.Config;
import cool.houge.grpc.MessageGrpc;
import cool.houge.service.message.SendMessageService;
import cool.houge.service.message.impl.SendMessageServiceImpl;
import io.grpc.ManagedChannelBuilder;

/**
 * gRPC 存根模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class GrpcServiceModule extends AbstractModule {

  private final Config config;

  /**
   * 使用应用配置构造对象.
   *
   * @param config 应用配置
   */
  public GrpcServiceModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    var grpcTarget = config.getString("logic-service.grpc-target");
    var channel =
        ManagedChannelBuilder.forTarget(grpcTarget)
            .usePlaintext()
            .enableRetry()
            .disableServiceConfigLookUp()
            .build();
    bind(MessageGrpc.MessageStub.class).toInstance(MessageGrpc.newStub(channel));

    bind(SendMessageService.class).to(SendMessageServiceImpl.class).in(Scopes.SINGLETON);
  }
}
