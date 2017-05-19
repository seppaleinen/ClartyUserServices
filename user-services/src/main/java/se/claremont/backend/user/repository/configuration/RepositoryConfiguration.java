package se.claremont.backend.user.repository.configuration;


import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import se.claremont.backend.user.repository.UserRepository;
import se.claremont.backend.user.repository.UserRepositoryMock;
import se.claremont.backend.user.repository.UserRepositoryPostgreSQL;

@Configuration
public class RepositoryConfiguration {

	@Autowired
	Environment env;

    @Bean
    public UserRepository userDao() {
    	String databaseType = env.getProperty("database.type");
    	switch (databaseType) {
		case "mock":
			return new UserRepositoryMock();
		case "postgresql":
			return new UserRepositoryPostgreSQL();
		default:
			return null;
		}
    }
    

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("clarty.database.driverClassName"));
        dataSource.setUrl(env.getProperty("clarty.datasource.url"));
        dataSource.setUsername(env.getProperty("clarty.datasource.username"));
        dataSource.setPassword(env.getProperty("clarty.datasource.password"));
        return dataSource;
    }
}
