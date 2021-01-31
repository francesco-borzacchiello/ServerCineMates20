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

    @Qualifier("postgresUserTable")
    private final Dao<UtenteEntity, String> dao;

    @Autowired
    public ServerSpringUserController(Dao<UtenteEntity, String> dao) {
        this.dao = dao;
    }

    @RequestMapping(value="/ServerCineMates20/User/add", method=RequestMethod.POST)
    public void addUser(@RequestBody UtenteEntity utente) {
        boolean insert = dao.insert(utente);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'utente esiste già oppure uno o più dei dati inseriti corrispondono ad un utente già registrato.");
    }

    @RequestMapping(value="/ServerCineMates20/User/getById/{email}", method=RequestMethod.GET)
    public UtenteEntity getUserById(@PathVariable("email") String email) {
        UtenteEntity utente = dao.getById(email);

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

    @RequestMapping(value="/ServerCineMates20/User/delete/{email}", method=RequestMethod.POST)
    public void deleteUser(@PathVariable("email") String username) {
        boolean result = dao.delete(dao.getById(username));
        if (!result)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente da eliminare non esiste.");
    }

    @RequestMapping(value="/ServerCineMates20/User/getUsersByQuery/{query}", method=RequestMethod.GET)
    public Set<UtenteEntity> getUsersByQuery(@PathVariable("query") String query) {
        try {
            return daoToUserDao().getUsersByQuery(query);
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/ServerCineMates20/User/getAllFriends/{email}", method=RequestMethod.GET)
    public Set<UtenteEntity> getAllFriends(@PathVariable("email") String email) {
        try {
            return daoToUserDao().getAllFriends(getUserById(email));
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/ServerCineMates20/User/getAllPendingFriendRequests/{email}", method=RequestMethod.GET)
    public Set<UtenteEntity> getAllPendingFriendRequests(@PathVariable("email") String email) {
        try {
            return daoToUserDao().getPendingFriendRequests(getUserById(email));
        }
        catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/ServerCineMates20/User/isFriendRequestPending/{Email_Utente}/{Email_Amico}", method=RequestMethod.GET)
    public boolean isFriendRequestPending(@PathVariable("Email_Utente") String userEmail, @PathVariable("Email_Amico") String friendEmail) {
        try {
            return daoToUserDao().isFriendRequestPending(userEmail, friendEmail);
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

    @RequestMapping(value="/ServerCineMates20/User/addFriend/{Email_Utente}/{Email_Amico}", method=RequestMethod.POST)
    public void addUserFriend(@PathVariable("Email_Utente") String emailUtente, @PathVariable("Email_Amico") String emailAmico) {
        if(!(dao instanceof UserDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        UtenteEntity utente = dao.getById(emailUtente);
        if (utente == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + emailUtente + " non esiste.");

        UtenteEntity amico = dao.getById(emailAmico);
        if (amico == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + emailAmico + " non esiste.");

        if(utente.getTipoUtente() == TipologiaUtente.amministratore || amico.getTipoUtente() == TipologiaUtente.amministratore)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un amministratore non può avere amici");

        if (!daoToUserDao().addFriend(utente, amico))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, emailAmico + " appartiene già alla lista amici di " + emailUtente);
    }

    @RequestMapping(value="/ServerCineMates20/User/deleteFriend/{Email_Utente}/{Email_Amico}", method=RequestMethod.POST)
    public void deleteUserFriend(@PathVariable("Email_Utente") String emailUtente, @PathVariable("Email_Amico") String emailAmico) {
        if(!(dao instanceof UserDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        UtenteEntity utente = dao.getById(emailUtente);
        if (utente == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + emailUtente + " non esiste.");

        UtenteEntity amico = dao.getById(emailAmico);
        if (amico == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + emailAmico + " non esiste.");

        if(utente.getTipoUtente() == TipologiaUtente.amministratore || amico.getTipoUtente() == TipologiaUtente.amministratore)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un amministratore non possiede amici");

        if (!daoToUserDao().deleteFriend(utente, amico))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, emailAmico + " non appartiene alla lista amici di " + emailUtente);
    }

    @RequestMapping(value="/ServerCineMates20/User/confirmFriendRequest/{Email_Utente}/{Email_Amico}", method=RequestMethod.POST)
    public void confirmUserFriendRequest(@PathVariable("Email_Utente") String emailUtente, @PathVariable("Email_Amico") String emailAmico) {
        if(!(dao instanceof UserDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        UtenteEntity utente = dao.getById(emailUtente);
        if (utente == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + emailUtente + " non esiste.");

        UtenteEntity amico = dao.getById(emailAmico);
        if (amico == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'utente " + emailAmico + " non esiste.");

        if(utente.getTipoUtente() == TipologiaUtente.amministratore || amico.getTipoUtente() == TipologiaUtente.amministratore)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Un amministratore non possiede amici");

        if (!daoToUserDao().confirmFriendRequest(utente, amico))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, emailAmico + " non ha ricevuto una richiesta di amicizia da " + emailUtente);
    }
}