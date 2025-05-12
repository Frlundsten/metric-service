package com.helidon.adapter.common;

import com.helidon.adapter.out.entity.MetricEntity;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import io.helidon.dbclient.spi.DbMapperProvider;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MetricTypeMapperProvider implements DbMapperProvider {
  private static final MetricEntityMapper MAPPER = new MetricEntityMapper();

  @Override
  public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
    return type.equals(MetricEntity.class)
        ? Optional.of((DbMapper<T>) MAPPER)
        : Optional.empty();
  }

  static class MetricEntityMapper implements DbMapper<MetricEntity> {
    @Override
    public MetricEntity read(DbRow row) {
      var id = row.column("id").as(UUID.class).get();
      var name = row.column("name").asString().get();
      var reportId = row.column("metric_report_id").as(UUID.class).get();
      var type = row.column("type").asString().get();
      var values = row.column("values").asString().get();

      return new MetricEntity(id, name, reportId, type, values);
    }

    @Override
    public Map<String, ?> toNamedParameters(MetricEntity value) {
      return Map.of();
    }

    @Override
    public List<?> toIndexedParameters(MetricEntity value) {
      return List.of();
    }
  }
}
