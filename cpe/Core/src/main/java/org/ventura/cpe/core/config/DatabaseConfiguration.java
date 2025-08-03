package org.ventura.cpe.core.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableTransactionManagement
@EnableJpaRepositories("org.ventura.cpe.core.repository")
public class DatabaseConfiguration {

    private final AppProperties properties;

    @Bean
    public DataSource dataSource() {
        ConfiguracionAplicacion.Repositorio repositorio = properties.getRepositorio();
        String serverType = repositorio.getTipoServidor();
        String dbServerType = findServerType(serverType);
        String driverServerType = findDriverType(serverType);
        boolean isSQLServer = serverType.equals("8") || serverType.equals("10");
        boolean isMySQL = serverType.equals("11");
        String format = isSQLServer ? "jdbc:%s://%s:%s;databaseName=%s" : "jdbc:%s://%s:%s/%s";
        String url = String.format(format, dbServerType, repositorio.getServidorBD(), repositorio.getPuerto(), repositorio.getBaseDatos());
        HikariConfig config = new HikariConfig();
        if (isMySQL) {
            url += "?serverTimezone=UTC";
            config.addDataSourceProperty("serverTimezone", "UTC");
        }
        config.setJdbcUrl(url);
        config.setUsername(repositorio.getUser());
        config.setPassword(repositorio.getPassword());
        config.setDriverClassName(driverServerType);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("allowMultiQueries", "true");
        config.setMaximumPoolSize(10);
        log.info(driverServerType);
        return new HikariDataSource(config);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        ConfiguracionAplicacion.Repositorio repositorio = properties.getRepositorio();
        String serverType = repositorio.getTipoServidor();
        Properties properties = new Properties();
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setPersistenceUnitName("Facturacion-Electronica");
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setDataSource(dataSource);
        properties.put("hibernate.dialect", findHibernateDialect(serverType));
        properties.put("hibernate.jdbc.time_zone", "America/Lima");
        properties.put("javax.persistence.lock.timeout", "60000");
        properties.put("hibernate.id.new_generator_mappings", "true");
        properties.put("hibernate.generate_statistics", "false");
        properties.put("connection.release_mode", "auto");
        entityManagerFactory.setPackagesToScan("org.ventura.cpe.core.domain");
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactory.setJpaProperties(properties);
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory;
    }

    private String findServerType(String serverType) {
        switch (serverType) {
            case "8":
            case "10":
                return "sqlserver";
            case "11":
                return "mysql";
            case "12":
                return "postgresql";
        }
        return "";
    }

    private String findDriverType(String serverType) {
        switch (serverType) {
            case "8":
            case "10":
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "11":
                return "com.mysql.cj.jdbc.Driver";
            case "12":
                return "org.postgresql.Driver";
        }
        return "";
    }

    private Class findHibernateDialect(String serverType) {
        switch (serverType) {
            case "8":
            case "10":
                return org.hibernate.dialect.SQLServer2012Dialect.class;
            case "11":
                return org.hibernate.dialect.MySQL8Dialect.class;
            case "12":
                return org.hibernate.dialect.PostgreSQL95Dialect.class;
        }
        return org.hibernate.dialect.SQLServer2012Dialect.class;
    }
}
