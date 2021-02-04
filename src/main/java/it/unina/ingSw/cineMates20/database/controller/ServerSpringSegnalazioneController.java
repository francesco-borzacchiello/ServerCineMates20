package it.unina.ingSw.cineMates20.database.controller;

import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.dao.SegnalazioneFilmDao;
import it.unina.ingSw.cineMates20.database.dao.SegnalazioneUtenteDao;
import it.unina.ingSw.cineMates20.database.entity.SegnalazioneFilmEntity;
import it.unina.ingSw.cineMates20.database.entity.SegnalazioneUtenteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ServerSpringSegnalazioneController {
    @Qualifier("postgresSegnalazioneUtenteTable")
    private final Dao<SegnalazioneUtenteEntity, Integer> daoForUser;
    @Qualifier("postgresSegnalazioneFilmTable")
    private final Dao<SegnalazioneFilmEntity, Integer> daoForFilm;

    @Autowired
    public ServerSpringSegnalazioneController(@Qualifier("postgresSegnalazioneUtenteTable") Dao<SegnalazioneUtenteEntity, Integer> daoForUser,
                                              @Qualifier("postgresSegnalazioneFilmTable") Dao<SegnalazioneFilmEntity, Integer> daoForFilm) {
        this.daoForUser = daoForUser;
        this.daoForFilm = daoForFilm;
    }

    @RequestMapping(value="/ServerCineMates20/Report/User/add", method= RequestMethod.POST)
    public void addUserReport(@RequestBody SegnalazioneUtenteEntity segnalazioneUtente) {
        boolean insert = daoForUser.insert(segnalazioneUtente);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile inserire la segnalazione per l'utente.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/Film/add", method= RequestMethod.POST)
    public void addMovieReport(@RequestBody SegnalazioneFilmEntity segnalazioneFilm) {
        boolean insert = daoForFilm.insert(segnalazioneFilm);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile inserire la segnalazione per il film.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/getAllMoviesReports/{email}", method= RequestMethod.GET)
    public List<SegnalazioneFilmEntity> getAllMoviesReports(@PathVariable("email") String userEmail) {
        List<SegnalazioneFilmEntity> list = daoToSegnalazioneFilmDao().getAllMovieReports(userEmail);
        if (list == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile recuperare le segnalazioni dei film.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/getAllUsersReports/{email}", method= RequestMethod.GET)
    public List<SegnalazioneUtenteEntity> getAllUsersReports(@PathVariable("email") String userEmail) {
        List<SegnalazioneUtenteEntity> list = daoToSegnalazioneUtenteDao().getAllUsersReports(userEmail);
        if (list == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile recuperare le segnalazioni degli utente.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/userDeleteMovieNotification", method=RequestMethod.POST)
    public void updateUserDeleteMovieNotification(@RequestBody SegnalazioneFilmEntity segnalazioneFilmEntity) {
        boolean update = daoToSegnalazioneFilmDao().updateUserDeleteNotification(segnalazioneFilmEntity);
        if (!update)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Si è verificato un errore durante l'aggiornamento della segnalazione.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/administratorHandledMovieNotification", method=RequestMethod.POST)
    public void updateAdministratorHandledMovieNotification(@RequestBody SegnalazioneFilmEntity segnalazioneFilmEntity) {
        boolean update = daoToSegnalazioneFilmDao().updateAdministratorHandledNotification(segnalazioneFilmEntity);
        if (!update)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Si è verificato un errore durante l'aggiornamento della segnalazione.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/userDeleteUserNotification", method=RequestMethod.POST)
    public void updateUserDeleteUserNotification(@RequestBody SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        boolean update = daoToSegnalazioneUtenteDao().updateUserDeleteNotification(segnalazioneUtenteEntity);
        if (!update)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Si è verificato un errore durante l'aggiornamento della segnalazione.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/administratorHandledUserNotification", method=RequestMethod.POST)
    public void updateAdministratorHandledUserNotification(@RequestBody SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        boolean update = daoToSegnalazioneUtenteDao().updateAdministratorHandledNotification(segnalazioneUtenteEntity);
        if (!update)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Si è verificato un errore durante l'aggiornamento della segnalazione.");
    }

    private SegnalazioneFilmDao<SegnalazioneFilmEntity,Integer> daoToSegnalazioneFilmDao() {
        if (!(daoForFilm instanceof SegnalazioneFilmDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return (SegnalazioneFilmDao<SegnalazioneFilmEntity,Integer>) daoForFilm;
    }

    private SegnalazioneUtenteDao<SegnalazioneUtenteEntity,Integer> daoToSegnalazioneUtenteDao() {
        if (!(daoForUser instanceof SegnalazioneUtenteDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return (SegnalazioneUtenteDao<SegnalazioneUtenteEntity,Integer>) daoForUser;
    }
}
