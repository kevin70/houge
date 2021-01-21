package top.yein.tethys.repository;

import reactor.core.publisher.Mono;
import top.yein.tethys.entity.ServerInstance;

/** @author KK (kzou227@qq.com) */
public interface ServerInstanceRepository {

  Mono<Integer> insert(ServerInstance entity);

  Mono<Integer> update(ServerInstance entity);

  Mono<Integer> updateCheckTime(int id);

  Mono<ServerInstance> findById(int id);
}
