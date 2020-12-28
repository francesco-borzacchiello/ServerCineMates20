package it.unina.ingSw.cineMates20.database.controller;

import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.dao.UserDao;
import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
public class ServerSpringUserController {

    @Autowired
    @Qualifier("postgresUserTable")
    private Dao<UtenteEntity, String> dao;

    @RequestMapping(value="/ServerCineMates20/User/add", method=RequestMethod.POST)
    public void addUser(@RequestBody UtenteEntity utente) {
        boolean insert = dao.insert(utente);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'utente esiste già oppure uno o più dei dati inseriti corrispondono ad un utente già registrato.");
    }

    @RequestMapping(value="/ServerCineMates20/User/getById/{username}", method=RequestMethod.GET)
    public UtenteEntity getUserById(@PathVariable("username") String username) {
        UtenteEntity utente = dao.getById(username);

        if (utente == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente non esiste.");

        return utente;
    }

    @RequestMapping(value="/ServerCineMates20/User/getAll", method=RequestMethod.GET)
    public List<UtenteEntity> getAllUsers() {
        List<UtenteEntity> utenti = dao.getAll();

        if (utenti == null || utenti.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Non sono presenti utenti.");

        return utenti;
    }

    @RequestMapping(value="/ServerCineMates20/User/delete/{username}", method=RequestMethod.POST)
    public void deleteUser(@PathVariable("username") String username) {
        boolean result = dao.delete(dao.getById(username));
        if (!result)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente da eliminare non esiste.");
    }

    @RequestMapping(value="/ServerCineMates20/User/getAllFriends/{username}", method=RequestMethod.GET)
    public Set<UtenteEntity> getAllFriends(@PathVariable("username") String username) {
        try {
            return daoToUserDao().getAllFriends(getUserById(username));
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private UserDao<UtenteEntity,String> daoToUserDao() {
        if (!(dao instanceof UserDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return (UserDao<UtenteEntity,String>) dao;
    }

    @RequestMapping(value="/ServerCineMates20/User/addFriend/{FK_Utente}/{FK_Amico}", method=RequestMethod.POST)
    public void addUserFriend(@PathVariable("FK_Utente") String usernameUtente, @PathVariable("FK_Amico") String usernameAmico) {
        if(!(dao instanceof UserDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        UtenteEntity utente = dao.getById(usernameUtente);
        if (utente == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + usernameUtente + " non esiste.");

        UtenteEntity amico = dao.getById(usernameAmico);
        if (amico == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + usernameAmico + " non esiste.");

        if(utente.getTipoUtente() == TipologiaUtente.amministratore || amico.getTipoUtente() == TipologiaUtente.amministratore)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un amministratore non può avere amici");

        UserDao<UtenteEntity,String> userDao = (UserDao<UtenteEntity,String>) dao;
        boolean add = userDao.addFriend(utente, amico);

        if (!add)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, usernameAmico + " appartiene già alla lista amici di " + usernameUtente);
    }

    @RequestMapping(value="/ServerCineMates20/User/deleteFriend/{FK_Utente}/{FK_Amico}", method=RequestMethod.POST)
    public void deleteUserFriend(@PathVariable("FK_Utente") String usernameUtente, @PathVariable("FK_Amico") String usernameAmico) {
        if(!(dao instanceof UserDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        UtenteEntity utente = dao.getById(usernameUtente);
        if (utente == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + usernameUtente + " non esiste.");

        UtenteEntity amico = dao.getById(usernameAmico);
        if (amico == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + usernameAmico + " non esiste.");

        if(utente.getTipoUtente() == TipologiaUtente.amministratore || amico.getTipoUtente() == TipologiaUtente.amministratore)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un amministratore non possiede amici");

        UserDao<UtenteEntity,String> userDao = (UserDao<UtenteEntity,String>) dao;
        boolean delete = userDao.deleteFriend(utente, amico);

        if (!delete)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, usernameAmico + " non appartiene alla lista amici di " + usernameUtente);
    }
}