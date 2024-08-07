package org.example.users.infrastructure.rest.resources;

import org.example.users.infrastructure.db.repository.SpringDataUserRepository;
import org.example.users.infrastructure.rest.dto.UserDto;
import org.example.users.infrastructure.rest.dto.UserDtoBuilder;
import org.example.users.testcontainers.LocalTestsKafkaListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
@ComponentScan(basePackages = "org.example.users")
@ConfigurationPropertiesScan(basePackages = "org.example.users.testcontainers")
class UserResourcesTestContainers {

    // bind the above RANDOM_PORT
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    @Autowired
    private LocalTestsKafkaListener localTestsKafkaListener;

    private HttpHeaders headers;
    private URI uriBase;

    @BeforeEach
    void setUp() throws URISyntaxException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        uriBase = new URI("http://localhost:" + port + "/api/users");
        localTestsKafkaListener.clearMessages();
        springDataUserRepository.deleteAll();
    }

    @Test
    void testRestCreateThenSaveUserDbAndSendKafkaMessage() {
        UserDto userDto = buildUserDto("userTest");
        ResponseEntity<UserDto> result = createUser(userDto);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(userDto, result.getBody());
        //assert user saved in db
        springDataUserRepository.findById(userDto.username())
                .ifPresentOrElse(Assertions::assertNotNull, () -> fail("User not saved in db"));
        //assert message sent to kafka
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(1, localTestsKafkaListener.getMessages().size());
                });
    }

    private ResponseEntity<UserDto> createUser(UserDto userDto) {
        HttpEntity<UserDto> request = new HttpEntity<>(userDto, headers);
        return restTemplate.postForEntity(
                uriBase.toString(), request, UserDto.class);
    }


    private UserDto buildUserDto(String username) {
        return new UserDto(username, "test test",
                "test@tes.com", "male", "http://test.com",
                "spain", "catalunia", "barcelona");
    }

    @Test
    void testRestUpdateThenUpdateUserDbAndSendKafkaMessage() {

        UserDto userDtoCreate = buildUserDto("userTest");
        ResponseEntity<UserDto> responseCreateUser = createUser(userDtoCreate);
        assertEquals(HttpStatus.CREATED, responseCreateUser.getStatusCode());

        //assert user saved in db
        springDataUserRepository.findById(userDtoCreate.username())
                .ifPresentOrElse(Assertions::assertNotNull, () -> fail("User not saved in db"));

        UserDto userDtoUpdate = UserDtoBuilder.builder(userDtoCreate).name("new name").build();
        HttpEntity<UserDto> request = new HttpEntity<>(userDtoUpdate, headers);
        restTemplate.put(
                uriBase.toString() + "/" + userDtoUpdate.username(),
                request);
        //assert user updated in db
        springDataUserRepository.findById(userDtoCreate.username())
                .ifPresentOrElse(user -> assertEquals(user.getName(), userDtoUpdate.name()),
                        () -> fail("User not saved in db"));
        //assert message sent to kafka
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(2, localTestsKafkaListener.getMessages().size());
                });
    }

    @Test
    void testRestDeleteThenDeleteUserDbAndSendKafkaMessage() {

        UserDto userDtoCreate = buildUserDto("userTest");
        ResponseEntity<UserDto> responseCreateUser = createUser(userDtoCreate);
        assertEquals(HttpStatus.CREATED, responseCreateUser.getStatusCode());

        restTemplate.delete(uriBase.toString() + "/" + userDtoCreate.username());

        //assert user delete from db
        assertTrue(springDataUserRepository.findById(userDtoCreate.username()).isEmpty());

        //assert message sent to kafka
        await().pollInterval(Duration.ofSeconds(3)).atMost(20, SECONDS)
                .untilAsserted(() -> {
                    assertFalse(localTestsKafkaListener.getMessages().isEmpty());
                    //assertEquals(2, localTestsKafkaListener.getMessages().size());
                });
    }

    @Test
    void testRestGenerateThenSaveUsersDbAndSendKafkaMessages() {

        ResponseEntity<List> response = restTemplate.getForEntity(
                uriBase.toString() + "/generate/5", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().size());
        //assert user saved in db
        assertEquals(5, springDataUserRepository.findAll().size());
        //assert message sent to kafka
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(5, localTestsKafkaListener.getMessages().size());
                });
    }
}

