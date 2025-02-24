package com.helidon.adapter.k6.out;

import com.helidon.application.domain.model.Metrics;
import com.helidon.application.port.out.Repository;

import javax.sql.DataSource;

public class MetricJDBCRepository implements Repository {
  private final DataSource dataSource;

  public MetricJDBCRepository(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void save(Metrics metrics) {

//    String sql = "INSERT INTO metrics VALUES (?, ?, ?)";
//
//    try(var conn = dataSource.getConnection()){
//      conn.setAutoCommit(false);
//      try (var stmt = conn.prepareStatement())
//
//    } catch (SQLException e) {
//        throw new RuntimeException(e);
//    }
  }

  @Override
  public Metrics get(String id) {
    return null;
  }
}
