package top.yein.tethys.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.javafaker.Faker;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.powermock.reflect.Whitebox;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import top.yein.tethys.domain.CachedJwtSecret;
import top.yein.tethys.entity.JwtSecret;

/**
 * {@link JwtSecretRepositoryImpl} 单元测试.
 *
 * @author KK (kzou227@qq.com)
 */
class JwtSecretRepositoryImplTest extends AbstractTestRepository {

  private Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);

  private JwtSecretRepository newJwtSecretRepository() {
    return new JwtSecretRepositoryImpl(dc);
  }

  @Test
  void insert() {
    var repo = newJwtSecretRepository();
    var entity = new JwtSecret();
    entity.setId("00");
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var fo = findOne("select * from t_jwt_secret where id=:id", Map.of("id", entity.getId()));
    var p = transactional(repo.insert(entity).zipWith(fo));
    StepVerifier.create(p)
        .consumeNextWith(
            tuple -> {
              assertThat(tuple.getT1()).isEqualTo(1);

              var dbRow = tuple.getT2();
              assertSoftly(
                  s -> {
                    s.assertThat(dbRow.get("id")).as("id").isEqualTo(entity.getId());
                    s.assertThat(dbRow.get("algorithm"))
                        .as("algorithm")
                        .isEqualTo(entity.getAlgorithm());
                    s.assertThat(dbRow.get("secret_key"))
                        .as("secret_key")
                        .isEqualTo(entity.getSecretKey());
                    s.assertThat(dbRow.get("deleted")).as("deleted").isEqualTo(0);
                    s.assertThat(dbRow.get("create_time")).as("create_time").isNotNull();
                    s.assertThat(dbRow.get("update_time")).as("update_time").isNotNull();
                  });
            })
        .expectComplete()
        .verify();
  }

  @Test
  void delete() {
    var repo = newJwtSecretRepository();
    var entity = new JwtSecret();
    entity.setId("00");
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p =
        transactional(
            repo.insert(entity)
                .then(repo.delete(entity.getId()).zipWith(repo.findById(entity.getId()))));
    StepVerifier.create(p)
        .consumeNextWith(
            tuple -> {
              assertThat(tuple.getT1()).isEqualTo(1);

              var dbRow = tuple.getT2();
              assertSoftly(
                  s -> {
                    s.assertThat(dbRow.getId()).as("id").isEqualTo(entity.getId());
                    s.assertThat(dbRow.getAlgorithm())
                        .as("algorithm")
                        .isEqualTo(entity.getAlgorithm());
                    s.assertThat(dbRow.getSecretKey())
                        .as("secret_key")
                        .isEqualTo(entity.getSecretKey());
                    s.assertThat(dbRow.getDeleted()).as("deleted").isGreaterThan(1);
                    s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                    s.assertThat(dbRow.getUpdateTime()).as("update_time").isNotNull();
                  });
            })
        .expectComplete()
        .verify();
  }

  @Test
  void findById() {
    var repo = newJwtSecretRepository();
    var entity = new JwtSecret();
    entity.setId("00");
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p = transactional(repo.insert(entity).then(repo.findById(entity.getId())));
    StepVerifier.create(p)
        .consumeNextWith(
            dbRow -> {
              assertSoftly(
                  s -> {
                    s.assertThat(dbRow.getId()).as("id").isEqualTo(entity.getId());
                    s.assertThat(dbRow.getAlgorithm())
                        .as("algorithm")
                        .isEqualTo(entity.getAlgorithm());
                    s.assertThat(dbRow.getSecretKey())
                        .as("secret_key")
                        .isEqualTo(entity.getSecretKey());
                    s.assertThat(dbRow.getDeleted()).as("deleted").isEqualTo(0);
                    s.assertThat(dbRow.getCreateTime()).as("create_time").isNotNull();
                    s.assertThat(dbRow.getUpdateTime()).as("update_time").isNotNull();
                  });
            })
        .expectComplete()
        .verify();
  }

  @Test
  void findAll() {
    var repo = newJwtSecretRepository();
    var entity1 = new JwtSecret();
    entity1.setId("00");
    entity1.setAlgorithm("HS512");
    entity1.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var entity2 = new JwtSecret();
    entity2.setId("01");
    entity2.setAlgorithm("HS512");
    entity2.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p = transactional(repo.insert(entity1).then(repo.insert(entity2)).thenMany(repo.findAll()));
    StepVerifier.create(p)
        .recordWith(ArrayList::new)
        .thenConsumeWhile(unused -> true)
        .consumeRecordedWith(
            jwtSecrets ->
                assertThat(jwtSecrets).extracting("id").contains(entity1.getId(), entity2.getId()))
        .expectComplete()
        .verify();
  }

  @Test
  void refreshAll() {
    var repo = newJwtSecretRepository();
    AsyncCache<String, CachedJwtSecret> jwtSecretCache =
        Whitebox.getInternalState(repo, "jwtSecretCache");

    var entity1 = new JwtSecret();
    entity1.setId("00");
    entity1.setAlgorithm("HS512");
    entity1.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var entity2 = new JwtSecret();
    entity2.setId("01");
    entity2.setAlgorithm("HS512");
    entity2.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var p =
        transactional(repo.insert(entity1).then(repo.insert(entity2)).thenMany(repo.refreshAll()));
    var cachedJwtSecrets = new ArrayList<CachedJwtSecret>();
    StepVerifier.create(p)
        .recordWith(() -> cachedJwtSecrets)
        .thenConsumeWhile(unused -> true)
        .expectComplete()
        .verify();

    // 校验缓存中的对象
    var syncCache = jwtSecretCache.synchronous();
    for (CachedJwtSecret cachedJwtSecret : cachedJwtSecrets) {
      assertThat(cachedJwtSecret).isEqualTo(syncCache.getIfPresent(cachedJwtSecret.getId()));
    }
  }

  @Test
  void loadById() {
    var repo = newJwtSecretRepository();
    var entity = new JwtSecret();
    entity.setId("00");
    entity.setAlgorithm("HS512");
    entity.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var loadByIdPublishers = new ArrayList<Publisher<CachedJwtSecret>>();
    for (int i = 0; i < 10; i++) {
      loadByIdPublishers.add(repo.loadById(entity.getId()));
    }

    var p = transactional(repo.insert(entity).then(Flux.concat(loadByIdPublishers).collectList()));
    StepVerifier.create(p)
        .consumeNextWith(
            cachedJwtSecrets -> {
              System.out.println(cachedJwtSecrets);
              assertThat(cachedJwtSecrets)
                  .allMatch(
                      cachedJwtSecret ->
                          cachedJwtSecret == cachedJwtSecrets.stream().findAny().get());
            })
        .expectComplete()
        .verify();
  }

  @Test
  void loadNoDeleted() {
    var repo = newJwtSecretRepository();
    AsyncCache<String, CachedJwtSecret> jwtSecretCache =
        Whitebox.getInternalState(repo, "jwtSecretCache");

    var entity1 = new JwtSecret();
    entity1.setId("00");
    entity1.setAlgorithm("HS512");
    entity1.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));

    var entity2 = new JwtSecret();
    entity2.setId("01");
    entity2.setAlgorithm("HS512");
    entity2.setSecretKey(ByteBuffer.wrap(faker.random().hex(256).getBytes(StandardCharsets.UTF_8)));
    var p =
        transactional(
            repo.insert(entity1).then(repo.insert(entity2)).thenMany(repo.loadNoDeleted()));
    StepVerifier.create(p)
        .recordWith(ArrayList::new)
        .thenConsumeWhile(unused -> true)
        .consumeRecordedWith(
            cachedJwtSecrets -> {
              assertThat(cachedJwtSecrets)
                  .extracting("id")
                  .contains(entity1.getId(), entity2.getId());
            })
        .expectComplete()
        .verify();
  }
}
