package com.yyggee.eggs.configs;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@PropertySource("classpath:application.properties")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "DS1EntityManagerFactory",
    transactionManagerRef = "DS1TransactionManager",
    basePackages = {"com.**.**.repositories.ds1"})
public class DS1 {

  @Autowired Environment environment;

  @Bean(name = "DS1DataSource")
  @ConfigurationProperties(prefix = "ds1.spring.datasource")
  public DataSource DS1DataSource() {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver"));
    dataSource.setJdbcUrl(environment.getProperty("ds1.spring.datasource.url"));
    dataSource.setUsername(environment.getProperty("ds1.spring.datasource.username"));
    dataSource.setPassword(environment.getProperty("ds1.spring.datasource.password"));
    //        dataSource.setCatalog("*****");
    /** HikariCP specific properties. Remove if you move to other connection pooling library. */
    dataSource.addDataSourceProperty("cachePrepStmts", true);
    dataSource.addDataSourceProperty("prepStmtCacheSize", 25000);
    dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", 20048);
    dataSource.addDataSourceProperty("useServerPrepStmts", true);
    dataSource.addDataSourceProperty("initializationFailFast", true);
    dataSource.setPoolName("DS1_HIKARICP_CONNECTION_POOL");

    return dataSource;
  }

  @Bean(name = "DS1EntityManagerFactory")
  @Primary // important
  public LocalContainerEntityManagerFactoryBean DS1EntityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(DS1DataSource());
    em.setPersistenceUnitName("DS1");
    em.setPackagesToScan("com.**.**.model.ds1");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    HashMap<String, Object> properties = new HashMap<>();
    properties.put(
        AvailableSettings.HBM2DDL_AUTO, environment.getProperty("spring.jpa.hibernate.ddl-auto"));
    em.setJpaPropertyMap(properties);
    return em;
  }

  @Bean(name = "DS1TransactionManager")
  public PlatformTransactionManager DS1TransactionManager() {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(DS1EntityManagerFactory().getObject());
    return transactionManager;
  }
}
