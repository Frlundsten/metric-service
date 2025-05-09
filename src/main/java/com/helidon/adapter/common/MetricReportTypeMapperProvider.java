package com.helidon.adapter.common;

import com.helidon.adapter.out.entity.MetricEntity;
import com.helidon.adapter.out.entity.MetricReportEntity;
import io.helidon.dbclient.DbColumn;
import io.helidon.dbclient.DbMapper;
import io.helidon.dbclient.DbRow;
import io.helidon.dbclient.spi.DbMapperProvider;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MetricReportTypeMapperProvider implements DbMapperProvider {
  private static final MetricReportEntityMapper MAPPER = new MetricReportEntityMapper();

  @Override
  public <T> Optional<DbMapper<T>> mapper(Class<T> type) {
    return type.equals(MetricReportEntity.class)
        ? Optional.of((DbMapper<T>) MAPPER)
        : Optional.empty();
  }

  static class MetricReportEntityMapper implements DbMapper<MetricReportEntity> {

    @Override
    public MetricReportEntity read(DbRow row) {
      DbColumn id = row.column("id");
      DbColumn data = row.column("data");
      DbColumn createdAt = row.column("created_at");
      DbColumn name = row.column("name");
      DbColumn type = row.column("type");
      DbColumn values = row.column("values");
      return new MetricReportEntity(
          id.as(UUID.class).get(),
          data.as(String.class).get(),
          createdAt.as(Timestamp.class).get().toInstant(),
          List.of(
              new MetricEntity(
                  name.as(String.class).get(),
                  type.as(String.class).get(),
                  values.as(String.class).get())));
    }

    @Override
    public Map<String, ?> toNamedParameters(MetricReportEntity value) {
      return Map.of();
    }

    @Override
    public List<?> toIndexedParameters(MetricReportEntity value) {
      return List.of();
    }
  }
}
