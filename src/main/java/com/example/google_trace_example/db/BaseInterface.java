package com.example.google_trace_example.db;

import com.example.google_trace_example.models.Result;
import java.util.Map;
import javax.annotation.Nullable;

public interface BaseInterface<K, T> {
  T get(K id);
  Result<T> getAll(String startCursorString, @Nullable Map<String, Object> queryParam);
  T create(T entity);
  void update(T entity);
  void delete(K id);
}

