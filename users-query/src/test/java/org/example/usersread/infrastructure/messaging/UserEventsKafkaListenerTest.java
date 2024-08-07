package org.example.usersread.infrastructure.messaging;

import org.example.usersread.infrastructure.db.repository.SpringDataUserRepository;
import org.example.usersread.infrastructure.messaging.dto.UserMessageDto;
import org.example.usersread.infrastructure.messaging.dto.UserMessageDtoBuilder;
import org.example.usersread.testcontainers.LocalTestsKafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

import java.net.URISyntaxException;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@TestPropertySource(properties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
@ComponentScan(basePackages = "org.example.usersread")
@ConfigurationPropertiesScan(basePackages = "org.example.usersread.testcontainers")
class UserEventsKafkaListenerTest {

    @Autowired
    private LocalTestsKafkaProducer localTestsKafkaProducer;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @BeforeEach
    void setUp() throws URISyntaxException {
        springDataUserRepository.deleteAll();
    }

    @Test
    void testListenUpdateMessageAndSaveUser() {
        UserMessageDto userMessageDto = buildUserMessageDto("userTest");
        localTestsKafkaProducer.sendUpdateMessage(userMessageDto);
        //assert user saved in db
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    springDataUserRepository.findById(userMessageDto.username())
                            .ifPresentOrElse(Assertions::assertNotNull, () -> fail("User not saved in db"));
                });
    }

    @Test
    void testListenUpdateMessageAndUpdateUser() {
        UserMessageDto userMessageDto = buildUserMessageDto("userTest");
        localTestsKafkaProducer.sendUpdateMessage(userMessageDto);
        //assert user saved in db
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    springDataUserRepository.findById(userMessageDto.username())
                            .ifPresentOrElse(Assertions::assertNotNull, () -> fail("User not saved in db"));
                });
        //assert update user
        UserMessageDto userMessageDtoUpdated = UserMessageDtoBuilder.builder(userMessageDto)
                .name("nameUpdated").build();
        localTestsKafkaProducer.sendUpdateMessage(userMessageDtoUpdated);

        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    springDataUserRepository.findById(userMessageDtoUpdated.username())
                            .ifPresentOrElse(userEntity -> {
                                assertEquals(userMessageDtoUpdated.name(), userEntity.getName());
                            }, () -> fail("User not saved in db"));
                });
    }

    @Test
    void testListenDeleteMessageAndDeleteUser() {
        UserMessageDto userMessageDto = buildUserMessageDto("userTest");
        localTestsKafkaProducer.sendUpdateMessage(userMessageDto);
        //assert user saved in db
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    springDataUserRepository.findById(userMessageDto.username())
                            .ifPresentOrElse(Assertions::assertNotNull, () -> fail("User not saved in db"));
                });
        //assert delete user
        localTestsKafkaProducer.sendDeleteMessage(userMessageDto.username());
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    assertTrue(springDataUserRepository.findById(userMessageDto.username()).isEmpty());
                });
    }

    private UserMessageDto buildUserMessageDto(String username) {
        return UserMessageDtoBuilder.builder()
                .username(username)
                .email("email")
                .name("name")
                .gender("male")
                .pictureUrl("pictureUrl")
                .country("country")
                .state("state")
                .city("city")
                .build();
    }
}
