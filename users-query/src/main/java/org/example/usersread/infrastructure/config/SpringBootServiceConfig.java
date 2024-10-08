package org.example.usersread.infrastructure.config;

import org.example.usersread.application.repository.UserRepository;
import org.example.usersread.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionProxyFactoryBean;

import java.util.Properties;

@Configuration
public class SpringBootServiceConfig {

    /*
     * this is not working with usecases so no transaction database
     */
    /*
    /*@Autowired
    private PlatformTransactionManager transactionManager;

    @Bean
    public UserService userService(UserRepository userRepository) {
        TransactionProxyFactoryBean proxy = new TransactionProxyFactoryBean();
        // Inject transaction manager here
        proxy.setTransactionManager(transactionManager);

        // Define wich object instance is to be proxied (your bean)
        proxy.setTarget(new UserService(userRepository));

        // Programmatically setup transaction attributes
        Properties transactionAttributes = new Properties();
        transactionAttributes.put("*", "PROPAGATION_REQUIRED");
        proxy.setTransactionAttributes(transactionAttributes);

        // Finish FactoryBean setup
        proxy.afterPropertiesSet();
        return (UserService) proxy.getObject();
    }*/

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }


}
