package it.unina.ingSw.cineMates20.database.controller;

import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.dao.SegnalazioneFilmDao;
import it.unina.ingSw.cineMates20.database.dao.SegnalazioneFilmDaoImplementation;
import it.unina.ingSw.cineMates20.database.entity.SegnalazioneFilmEntity;
import it.unina.ingSw.cineMates20.database.entity.SegnalazioneUtenteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
    public void addReportUser(@RequestBody SegnalazioneUtenteEntity segnalazioneUtente) {
        boolean insert = daoForUser.insert(segnalazioneUtente);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile inserire la segnalazione per l'utente.");
    }

    @RequestMapping(value="/ServerCineMates20/Report/Film/add", method= RequestMethod.POST)
    public void addReportUser(@RequestBody SegnalazioneFilmEntity segnalazioneFilm) {
        boolean insert = daoForFilm.insert(segnalazioneFilm);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Non è stato possibile inserire la segnalazione per il film.");
    }
}
