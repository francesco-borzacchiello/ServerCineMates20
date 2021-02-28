package it.unina.ingSw.cineMates20.database;

import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.dao.UserDao;
import it.unina.ingSw.cineMates20.database.dao.UserDaoImplementation;
import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
class UserDaoImplementationTest {

    @Qualifier("postgresUserTable")
    private final Dao<UtenteEntity, String> dao;
    private UserDao<UtenteEntity, String> userDao;
    private final UtenteEntity utente, amicoPendente, amico;

    @Autowired
    UserDaoImplementationTest(Dao<UtenteEntity, String> dao) {
        this.dao = dao;
        utente = new UtenteEntity("TestUsername", "test", "test", "test@test.com", TipologiaUtente.utente);
        amicoPendente = new UtenteEntity("TestAmicizia", "mario", "rossi", "test1@test.com", TipologiaUtente.utente);
        amico = new UtenteEntity("TestAmicizia2", "mario2", "rossi2", "test2@test.com", TipologiaUtente.utente);
    }

    @BeforeEach
    void setUp() {
        //Se gli inserimenti falliscono, l'esito del test è compromesso
        Assertions.assertTrue(dao.insert(utente));
        Assertions.assertTrue(dao.insert(amicoPendente));
        Assertions.assertTrue(dao.insert(amico));

        if(!(dao instanceof UserDaoImplementation)) throw new IllegalStateException();

        userDao = ((UserDaoImplementation)dao);
        Assertions.assertTrue(userDao.addFriend(utente, amicoPendente));

        Assertions.assertTrue(userDao.addFriend(utente, amico));
        Assertions.assertTrue(userDao.confirmFriendRequest(utente, amico));
    }

    /**
     Testing Black-Box:
     getById(String)
     */
    @Test
    void getByIdNullEmail() {
        Assertions.assertNull(dao.getById(null));
    }

    @Test
    void getByIdWithValidEmail() {
        Assertions.assertEquals(utente, dao.getById("test@test.com"));
    }

    @Test
    void getByIdWithWrongEmail() {
        Assertions.assertNull(dao.getById("testErrore@test.com"));
    }

    @Test
    void getByIdWithEmptyString() {
        Assertions.assertNull(dao.getById(""));
    }

    @Test
    void getByIdWithBlankSpace() {
        Assertions.assertNull(dao.getById("    "));
    }

    /**
     Testing White-Box:
     */
    @Test
    void getById_1b_2b() {
        Assertions.assertNull(dao.getById(null));
    }

    @Test
    void getById_1b_4b_7b() {
        Assertions.assertEquals(utente, dao.getById("test@test.com"));
    }

    @Test
    void getById_1b_4b_7b_9b() {
        Assertions.assertNull(dao.getById("testErrore.com"));
    }

    /*-----------------------------------------------------------------------------------------------------------------*/

    /**
     Testing Black-Box:
     isFriendRequestPending(String, String)
     */

    @Test
    void isFriendRequestPendingUserIsNullAndFriendIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userDao.isFriendRequestPending(null, null));
    }

    @Test
    void isFriendRequestPendingUserIsNotNullAndFriendIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userDao.isFriendRequestPending("test@test.com", null));
    }

    @Test
    void isFriendRequestPendingUserIsNullAndFriendIsNotNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userDao.isFriendRequestPending(null, "test@test.com"));
    }

    @Test
    void isFriendRequestPendingUserIsNotNullAndFriendIsNotNull() {
        Assertions.assertTrue(userDao.isFriendRequestPending
                                ("test@test.com", "test1@test.com"));
    }

    @Test
    void isFriendRequestPendingFriendIsNotNullAndUserIsNotNull() {
        Assertions.assertTrue(userDao.isFriendRequestPending
                                ("test1@test.com", "test@test.com"));
    }

    @Test
    void isFriendRequestPendingInvalidFriend() {
        Assertions.assertFalse(userDao.isFriendRequestPending
                                ("test@test.com", "testFail@test.com"));
    }

    @Test
    void isFriendRequestPendingInvalidUserWithFriendEmail() {
        Assertions.assertFalse(userDao.isFriendRequestPending
                                ("testFail@test.com", "test1@test.com"));
    }

    @Test
    void isFriendRequestPendingInvalidUserWithUserEmail() {
        Assertions.assertFalse(userDao.isFriendRequestPending
                                ("testFail@test.com", "test@test.com"));
    }

    @Test
    void isFriendRequestPendingUserIsBlankAndFriendIsBlank() {
        Assertions.assertFalse(userDao.isFriendRequestPending("", ""));
    }

    /**
     Testing White-Box:
     isFriendRequestPending(String, String)
     */

    @Test
    void isFriendRequestPending_1b_2b() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> userDao.isFriendRequestPending("test1@test.com", null));
    }

    @Test
    void isFriendRequestPending_1b_4b_12b() {
        Assertions.assertFalse(userDao.isFriendRequestPending("testFail@test.com", "testError@test.com"));
    }

    @Test
    void isFriendRequestPending_1b_4b_6b_7b_isPending() {
        Assertions.assertTrue(userDao.isFriendRequestPending("test@test.com", "test1@test.com"));
    }

    @Test
    void isFriendRequestPending_1b_4b_12b_isNotPending() {
        Assertions.assertFalse(userDao.isFriendRequestPending("test@test.com", "test2@test.com"));
    }

    @Test
    void isFriendRequestPending_1b_4b_9b() {
        /*
            Questo ramo non è testabile poiché il metodo jdbcTemplate.queryForObject() del nodo 4,
            non restituisce mai null
        */
        Assertions.assertTrue(true);
    }


}