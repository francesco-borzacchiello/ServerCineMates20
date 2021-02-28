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
import java.util.Map;

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
                    "Non è stato possibile recuperare le segnalazioni degli utenti.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/getAllReportedUsers", method= RequestMethod.GET)
    public List<SegnalazioneUtenteEntity> getAllReportedUsers() {
        List<SegnalazioneUtenteEntity> list = daoToSegnalazioneUtenteDao().getAllReportedUsers();
        if (list == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile recuperare le segnalazioni degli utenti.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/getAllManagedReportedUsers", method=RequestMethod.POST)
    public List<SegnalazioneUtenteEntity> getAllManagedReportedUsers(@RequestBody String emailHash) {
        List<SegnalazioneUtenteEntity> list = daoToSegnalazioneUtenteDao().getAllManagedReportedUsers(emailHash);
        if (list == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile recuperare le segnalazioni degli utenti.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/getAllReportedMovies", method=RequestMethod.GET)
    public List<SegnalazioneFilmEntity> getAllReportedMovies() {
        List<SegnalazioneFilmEntity> list = daoToSegnalazioneFilmDao().getAllReportedMovies();
        if (list == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile recuperare le segnalazioni degli utenti.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/getAllManagedReportedMovies", method=RequestMethod.POST)
    public List<SegnalazioneFilmEntity> getAllManagedReportedMovies(@RequestBody String emailHash) {
        List<SegnalazioneFilmEntity> list = daoToSegnalazioneFilmDao().getAllManagedReportedMovies(emailHash);
        if (list == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile recuperare le segnalazioni degli utenti.");
        return list;
    }

    @RequestMapping(value="/ServerCineMates20/Report/userDeleteMovieNotification", method=RequestMethod.POST)
    public void updateUserDeleteMovieNotification(@RequestBody SegnalazioneFilmEntity segnalazioneFilmEntity) {
        boolean update = daoToSegnalazioneFilmDao().updateUserDeleteNotification(segnalazioneFilmEntity);
        if (!update)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Si è verificato un errore durante l'aggiornamento della segnalazione.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/administratorHandledMovieReport", method=RequestMethod.POST)
    public boolean updateAdministratorHandledMovieReport(@RequestBody Map<String, SegnalazioneFilmEntity> json) {
        Map.Entry<String, SegnalazioneFilmEntity> entry = json.entrySet().stream().findFirst().orElse(null);
        if(entry == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Si è verificato un errore durante l'aggiornamento della segnalazione.");

        String emailHash = entry.getKey();
        SegnalazioneFilmEntity segnalazioneFilmEntity = entry.getValue();

        if(emailHash == null || segnalazioneFilmEntity == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Si è verificato un errore durante l'aggiornamento della segnalazione.");

        segnalazioneFilmEntity.setFkAmministratoreCheGestisce(emailHash);
        return daoToSegnalazioneFilmDao().updateAdministratorHandledMovieReport(segnalazioneFilmEntity);
    }

    @RequestMapping(value="/ServerCineMates20/Report/userDeleteUserNotification", method=RequestMethod.POST)
    public void updateUserDeleteUserNotification(@RequestBody SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        boolean update = daoToSegnalazioneUtenteDao().updateUserDeleteNotification(segnalazioneUtenteEntity);
        if (!update)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Si è verificato un errore durante l'aggiornamento della segnalazione.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/administratorHandledUserReport", method=RequestMethod.POST)
    public boolean updateAdministratorHandledUserReport(@RequestBody Map<String, SegnalazioneUtenteEntity> json) {
        Map.Entry<String, SegnalazioneUtenteEntity> entry = json.entrySet().stream().findFirst().orElse(null);
        if(entry == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Si è verificato un errore durante l'aggiornamento della segnalazione.");

        String emailHash = entry.getKey();
        SegnalazioneUtenteEntity segnalazioneUtenteEntity = entry.getValue();

        if(emailHash == null || segnalazioneUtenteEntity == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Si è verificato un errore durante l'aggiornamento della segnalazione.");

        segnalazioneUtenteEntity.setFkAmministratoreCheGestisce(emailHash);
        return daoToSegnalazioneUtenteDao().updateAdministratorHandledUserReport(segnalazioneUtenteEntity);
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
