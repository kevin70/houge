package io.zhudy.xim;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** @author Kevin Zou (kevinz@weghst.com) */
public class MockTests {

  @Test
  public void spyC() {
    //        var handler = new WebSocketHandler();
    //        Whitebox.setInternalState(handler, "enabledAnonymous", false);
    //
    //        System.out.println(handler);
  }

  @Test
  void testFlux() {
    Flux.just("a", "b")
        .flatMap(
            a -> {
              if (a.equals("a")) {
                return Mono.empty();
              }
              return Mono.just(a);
            })
        .subscribe(System.out::println);
  }
}
