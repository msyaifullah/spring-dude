package lib.yyggee.jupiter.repository;

import java.util.List;
import java.util.Optional;

public interface CacheRepository<T, ID> {

  T save(T entity);

  T save(T entity, long ttlSeconds);

  Optional<T> findById(ID id);

  List<T> findAll();

  boolean existsById(ID id);

  void deleteById(ID id);

  void deleteAll();

  long count();
}
