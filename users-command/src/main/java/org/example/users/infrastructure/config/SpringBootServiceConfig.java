package org.example.users.infrastructure.config;

import org.example.users.application.randomgenerator.UserGenerator;
import org.example.users.application.repository.UserRepository;
import org.example.users.application.service.UserService;
import org.example.users.infrastructure.messaging.UserEventsKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import java.util.Properties;

@Configuration
public class SpringBootServiceConfig {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public UserService userService(UserRepository userRepository, UserGenerator userGenerator,
                                   UserEventsKafkaProducer userEventsKafkaProducer) {
        TransactionProxyFactoryBean proxy = new TransactionProxyFactoryBean();
        // Inject transaction manager here
        proxy.setTransactionManager(transactionManager);

        // Define wich object instance is to be proxied (your bean)
        proxy.setTarget(new UserService(userRepository, userGenerator, userEventsKafkaProducer));

        // Programmatically setup transaction attributes
        Properties transactionAttributes = new Properties();
        transactionAttributes.put("*", "PROPAGATION_REQUIRED");
        proxy.setTransactionAttributes(transactionAttributes);

        // Finish FactoryBean setup
        proxy.afterPropertiesSet();
        return (UserService) proxy.getObject();
    }

}
