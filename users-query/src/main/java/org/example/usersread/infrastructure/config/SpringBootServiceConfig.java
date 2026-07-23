package org.example.usersread.infrastructure.config;

import org.example.usersread.application.repository.UserProjectionRepository;
import org.example.usersread.application.repository.UserReadRepository;
import org.example.usersread.application.service.UserProjectionService;
import org.example.usersread.application.service.UserService;
import org.example.usersread.application.usecase.UserProjectionUseCase;
import org.example.usersread.application.usecase.UserQueryUseCase;
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
    public UserQueryUseCase userService(UserReadRepository userReadRepository) {
        TransactionProxyFactoryBean proxy = new TransactionProxyFactoryBean();
        // Inject transaction manager here
        proxy.setTransactionManager(transactionManager);

        // Define which object instance is to be proxied (your bean)
        proxy.setTarget(new UserService(userReadRepository));

        // Programmatically setup transaction attributes
        Properties transactionAttributes = new Properties();
        transactionAttributes.put("*", "PROPAGATION_REQUIRED");
        proxy.setTransactionAttributes(transactionAttributes);

        // Finish FactoryBean setup
        proxy.afterPropertiesSet();
        return (UserQueryUseCase) proxy.getObject();
    }

    @Bean
    public UserProjectionUseCase userProjectionService(UserProjectionRepository userProjectionRepository) {
        TransactionProxyFactoryBean proxy = new TransactionProxyFactoryBean();
        proxy.setTransactionManager(transactionManager);
        proxy.setTarget(new UserProjectionService(userProjectionRepository));
        proxy.setProxyInterfaces(new Class[]{UserProjectionUseCase.class});
        Properties transactionAttributes = new Properties();
        transactionAttributes.put("*", "PROPAGATION_REQUIRED");
        proxy.setTransactionAttributes(transactionAttributes);
        proxy.afterPropertiesSet();
        return (UserProjectionUseCase) proxy.getObject();
    }

}
