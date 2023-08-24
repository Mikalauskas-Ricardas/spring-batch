package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.config;
import org.hibernate.jpa.HibernatePersistenceProvider;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.sql.DataSource;

@Configuration
public class DBConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.universitydatasource")
    public DataSource universityDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.postgresdatasource")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public EntityManagerFactory postgresqlEntityManagerFactory(DataSource postgresDataSource) {
        LocalContainerEntityManagerFactoryBean lem = new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(postgresDataSource);
        lem.setPackagesToScan("com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.postgresql.entity");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // lem.setPersistenceProvider((PersistenceProvider) new HibernatePersistenceProvider());
        lem.afterPropertiesSet();

        return lem.getObject();
    }

    @Bean
    public EntityManagerFactory mysqlEntityManagerFactory(DataSource universityDataSource) {
        LocalContainerEntityManagerFactoryBean lem = new LocalContainerEntityManagerFactoryBean();
        lem.setDataSource(universityDataSource);
        lem.setPackagesToScan("com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity");
        lem.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        // lem.setPersistenceProvider((PersistenceProvider) new HibernatePersistenceProvider());
        lem.afterPropertiesSet();

        return lem.getObject();
    }

    @Bean
    @Primary
    public JpaTransactionManager jpaTransactionManager(DataSource universityDataSource,
                                                       EntityManagerFactory mysqlEntityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(universityDataSource);
        transactionManager.setEntityManagerFactory(mysqlEntityManagerFactory);

        return transactionManager;
    }
}
