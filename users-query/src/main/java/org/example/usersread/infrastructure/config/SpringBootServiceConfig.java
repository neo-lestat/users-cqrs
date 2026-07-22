package org.example.usersread.infrastructure.config;

import org.example.usersread.application.repository.UserProjectionRepository;
import org.example.usersread.application.repository.UserReadRepository;
import org.example.usersread.application.service.UserProjectionService;
import org.example.usersread.application.service.UserService;
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
    public UserService userService(UserReadRepository userReadRepository) {
        TransactionProxyFactoryBean proxy = new TransactionProxyFactoryBean();
        // Inject transaction manager here
        proxy.setTransactionManager(transactionManager);

        // Define wich object instance is to be proxied (your bean)
        proxy.setTarget(new UserService(userReadRepository));

        // Programmatically setup transaction attributes
        Properties transactionAttributes = new Properties();
        transactionAttributes.put("*", "PROPAGATION_REQUIRED");
        proxy.setTransactionAttributes(transactionAttributes);

        // Finish FactoryBean setup
        proxy.afterPropertiesSet();
        return (UserService) proxy.getObject();
    }

    @Bean
    public UserProjectionService userProjectionService(UserProjectionRepository userProjectionRepository) {
        TransactionProxyFactoryBean proxy = new TransactionProxyFactoryBean();
        proxy.setTransactionManager(transactionManager);
        proxy.setTarget(new UserProjectionService(userProjectionRepository));
        Properties transactionAttributes = new Properties();
        transactionAttributes.put("*", "PROPAGATION_REQUIRED");
        proxy.setTransactionAttributes(transactionAttributes);
        proxy.afterPropertiesSet();
        return (UserProjectionService) proxy.getObject();
    }

}
