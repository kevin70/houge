package top.yein.tethys.rest.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import top.yein.tethys.ApplicationIdentifier;
import top.yein.tethys.core.http.Interceptors;
import top.yein.tethys.core.http.RoutingService;
import top.yein.tethys.core.resource.AuthInterceptor;
import top.yein.tethys.rest.RestApplicationIdentifier;
import top.yein.tethys.rest.resource.MessageIdResource;

/**
 * REST Guice 模块.
 *
 * @author KK (kzou227@qq.com)
 */
public class RestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ApplicationIdentifier.class).to(RestApplicationIdentifier.class).in(Scopes.SINGLETON);

    var routingServicesBinder = Multibinder.newSetBinder(binder(), RoutingService.class);
    routingServicesBinder.addBinding().to(MessageIdResource.class).in(Scopes.SINGLETON);
//    routingServicesBinder.addBinding().to(PrivateMessageResource.class).in(Scopes.SINGLETON);
//    routingServicesBinder.addBinding().to(GroupMessageResource.class).in(Scopes.SINGLETON);
  }

  @Provides
  public Interceptors interceptors(AuthInterceptor authInterceptor) {
    return new Interceptors(authInterceptor::handle);
  }
}
