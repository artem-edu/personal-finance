package mephi.repository;

import mephi.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest {

    private UserRepository userRepository;
    private Path tempDbFile;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        tempDbFile = tempDir.resolve("test_users.json");
        userRepository = new UserRepository(tempDbFile.toString());
    }

    @Test
    void createAndFindByIdSuccess() {
        User user = new User("testUser", "hashedPassword");

        userRepository.create(user);
        Optional<User> found = userRepository.findById(user.getId());

        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
        assertEquals("testUser", found.get().getLogin());
        assertTrue(Files.exists(tempDbFile));
    }

    @Test
    void findByIdReturnsEmpty() {
        Optional<User> found = userRepository.findById(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    void findByLoginSuccess() {
        User user = new User("uniqueLogin", "hashedPassword");
        userRepository.create(user);

        Optional<User> found = userRepository.findByLogin("uniqueLogin");

        assertTrue(found.isPresent());
        assertEquals(user.getId(), found.get().getId());
    }

    @Test
    void findByLoginReturnsEmpty() {
        Optional<User> found = userRepository.findByLogin("nonExistentLogin");

        assertTrue(found.isEmpty());
    }

    @Test
    void findAllSuccess() {
        User user1 = new User("user1", "hash1");
        User user2 = new User("user2", "hash2");

        userRepository.create(user1);
        userRepository.create(user2);

        List<User> allUsers = userRepository.findAll();

        assertEquals(2, allUsers.size());
    }

    @Test
    void afterLoadRestoresLoginIndexFromFile() {
        User user = new User("persistedUser", "hashedPassword");
        userRepository.create(user);

        UserRepository reloadedRepository = new UserRepository(tempDbFile.toString());
        Optional<User> foundByLogin = reloadedRepository.findByLogin("persistedUser");
        Optional<User> foundById = reloadedRepository.findById(user.getId());

        assertTrue(foundByLogin.isPresent());
        assertEquals(user.getId(), foundByLogin.get().getId());
        assertTrue(foundById.isPresent());
    }
}
