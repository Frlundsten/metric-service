package com.helidon.adapter.k6.out;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

class DataSourceInstanceTest {

//  @Test
//  void createDataSource() {
//    HikariDataSource dataSourceInstance =
//        (HikariDataSource)
//            DataSourceInstance.getDataSource(
//                "jdbc:postgresql://localhost:5432/helidon", "user", "password");
//
//    assertThat(dataSourceInstance).isNotNull();
//    assertThat(dataSourceInstance.getJdbcUrl())
//        .isEqualTo("jdbc:postgresql://localhost:5432/helidon");
//    assertThat(dataSourceInstance.getUsername()).isEqualTo("user");
//    assertThat(dataSourceInstance.getPassword()).isEqualTo("password");
//    assertThat(dataSourceInstance.isRunning()).isTrue();
//    dataSourceInstance.close();
//    assertThat(dataSourceInstance.isRunning()).isFalse();
//  }
}
