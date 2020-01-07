package wolox.training.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import wolox.training.repositories.UserRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UserTest {

    private User user;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        user = new User();
        user.setUsername("Username");
        user.setName("Name");
        user.setBirthdate(LocalDate.parse("1990-01-01"));
        userRepository.save(user);
    }

    @Test
    public void whenCreateAUserThenIsPersisted() {
        entityManager.persist(user);
        entityManager.flush();

        User userFounded = userRepository.findByUsername(user.getUsername());
        assertThat(userFounded.getName()).isEqualTo(user.getName());
        assertThat(userFounded.getUsername()).isEqualTo(user.getUsername());
    }

    @Test(expected = NullPointerException.class)
    public void whenCreateAUserWithoutUsername() {
        user.setUsername(null);
        userRepository.save(user);
    }
}
