package com.yyggee.eggs.configs;


import com.yyggee.eggs.constants.ConstantTenant;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@PropertySource("classpath:application.properties")
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "DS2EntityManagerFactory",
        transactionManagerRef = "DS2TransactionManager", basePackages = {"com.**.**.repositories.ds2"})
public class DS2 {

    @Autowired
    Environment environment;

    @Bean(name = "DS2DataSource")
    public DataSource DS2DataSource() {
        TenantRoutingDataSource tenantRoutingDataSource = new TenantRoutingDataSource();
        tenantRoutingDataSource.setTargetDataSources(datasources());
        tenantRoutingDataSource.afterPropertiesSet();
        return tenantRoutingDataSource;
    }

    @Bean(name = "DS2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean DS2EntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(DS2DataSource());
        em.setPersistenceUnitName("DS2");
        em.setPackagesToScan("com.**.**.model.ds2");
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(AvailableSettings.HBM2DDL_AUTO, environment.getProperty("spring.jpa.hibernate.ddl-auto"));
        em.setJpaPropertyMap(properties);
        return em;
    }


    @Bean(name = "DS2TransactionManager")
    public PlatformTransactionManager DS2TransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                DS2EntityManagerFactory().getObject()
        );
        return transactionManager;
    }


    private Map <Object, Object> datasources(){
        Map <Object, Object> datasources = new LinkedHashMap<>();

        //STAGING
        HikariDataSource dataSourceA = new HikariDataSource();
        dataSourceA.setDriverClassName(environment.getProperty("spring.datasource.driver"));
        dataSourceA.setJdbcUrl(environment.getProperty("ds2.spring.datasource.url"));
        dataSourceA.setUsername(environment.getProperty("ds2.spring.datasource.username"));
        dataSourceA.setPassword(environment.getProperty("ds2.spring.datasource.password"));

        dataSourceA.setMaximumPoolSize(50);
        dataSourceA.setAllowPoolSuspension(true);
        dataSourceA.setMinimumIdle(5);
        dataSourceA.setConnectionTimeout(300000);
        dataSourceA.setMaxLifetime(900000);

        dataSourceA.addDataSourceProperty("cachePrepStmts", true);
        dataSourceA.addDataSourceProperty("prepStmtCacheSize", 25000);
        dataSourceA.addDataSourceProperty("prepStmtCacheSqlLimit", 20048);
        dataSourceA.addDataSourceProperty("useServerPrepStmts", true);
        dataSourceA.addDataSourceProperty("initializationFailFast", true);
        dataSourceA.setPoolName("DS2_HIKARICP_CONNECTION_POOL");


        //PRODUCTION
        HikariDataSource dataSourceB = new HikariDataSource();
        dataSourceB.setDriverClassName(environment.getProperty("spring.datasource.driver"));
        dataSourceB.setJdbcUrl(environment.getProperty("ds3.spring.datasource.url"));
        dataSourceB.setUsername(environment.getProperty("ds3.spring.datasource.username"));
        dataSourceB.setPassword(environment.getProperty("ds3.spring.datasource.password"));

        dataSourceB.setMaximumPoolSize(50);
        dataSourceB.setAllowPoolSuspension(true);
        dataSourceB.setMinimumIdle(5);
        dataSourceB.setConnectionTimeout(300000);
        dataSourceB.setMaxLifetime(900000);

        dataSourceB.addDataSourceProperty("cachePrepStmts", true);
        dataSourceB.addDataSourceProperty("prepStmtCacheSize", 25000);
        dataSourceB.addDataSourceProperty("prepStmtCacheSqlLimit", 20048);
        dataSourceB.addDataSourceProperty("useServerPrepStmts", true);
        dataSourceB.addDataSourceProperty("initializationFailFast", true);
        dataSourceB.setPoolName("DS3_HIKARICP_CONNECTION_POOL");

        //Register the object
        datasources.put(ConstantTenant.A_TENANT, dataSourceA);
        datasources.put(ConstantTenant.B_TENANT, dataSourceB);

        return datasources;
    }

}
