package com.helidon.adapter.k6.out;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataSourceInstance {
  private static Logger LOG = LoggerFactory.getLogger(DataSourceInstance.class);

  private static DataSourceSingleton dataSource;

  private record DataSourceSingleton(DataSource dataSource) {}

  public static DataSource getDataSource(String jdbcUrl, String username, String password) {
    DataSourceSingleton temp = dataSource;
    if (temp == null) {
      synchronized (DataSourceInstance.class) {
        if (dataSource == null) {
          dataSource = new DataSourceSingleton(createDataSource(jdbcUrl, username, password));
        }
        temp = dataSource;
      }
    }
    return temp.dataSource;
  }

  private static DataSource createDataSource(String jdbcUrl, String username, String password) {
    LOG.info("Creating DataSource with connectionString: {}", jdbcUrl);
    var config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    return new HikariDataSource(config);
  }
}
