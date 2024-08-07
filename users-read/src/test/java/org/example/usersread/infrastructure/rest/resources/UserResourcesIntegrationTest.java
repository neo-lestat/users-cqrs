package org.example.usersread.infrastructure.rest.resources;

import org.example.usersread.infrastructure.db.entity.UserEntity;
import org.example.usersread.infrastructure.db.repository.SpringDataUserRepository;
import org.example.usersread.infrastructure.rest.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ConfigurationPropertiesScan(basePackages = "org.example.usersread.testcontainers")
class UserResourcesIntegrationTest {

    // bind the above RANDOM_PORT
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SpringDataUserRepository springDataUserRepository;

    private URI uriBase;

    @BeforeEach
    void setUp() throws URISyntaxException {
        uriBase = new URI("http://localhost:" + port + "/api/users");
        springDataUserRepository.deleteAll();
        springDataUserRepository.saveAll(buildUsers());
    }

    private List<UserEntity> buildUsers() {
        return List.of(
                createUserEntity("test1"),
                createUserEntity("test2"),
                createUserEntity("test3"),
                createUserEntity("test4"),
                createUserEntity("test5"),
                createUserEntity("test6"),
                createUserEntity("test7"),
                createUserEntity("test8")
        );
    }

    @Test
    void getGetUsersWithPageSizeAndPageNumber() {
        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(
                uriBase.toString() + "?size=5&page=0", UserDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto[] userDtoArray = response.getBody();
        assertNotNull(userDtoArray);
        assertEquals(5, userDtoArray.length);

        response = restTemplate.getForEntity(
                uriBase.toString() + "?size=5&page=1", UserDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        userDtoArray = response.getBody();
        assertNotNull(userDtoArray);
        assertEquals(3, userDtoArray.length);
    }

    @Test
    void testGetUserFound() {
        ResponseEntity<UserDto> response = restTemplate.getForEntity(
                uriBase.toString() + "/test1", UserDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDto userDtoResponse = response.getBody();
        assertEquals("test1", userDtoResponse.username());
        assertEquals("test@test", userDtoResponse.email());
    }

    @Test
    void testGetUserNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                uriBase.toString() + "/other", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    //@Test
    void getUserTree() {
    }

    private UserEntity createUserEntity(String username) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail("test@test");
        user.setName("test");
        user.setGender("test");
        user.setPictureUrl("test");
        user.setCountry("test");
        user.setState("test");
        user.setCity("test");
        return user;
    }

}
