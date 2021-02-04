package it.unina.ingSw.cineMates20.database.controller;

import it.unina.ingSw.cineMates20.database.dao.AdministratorDao;
import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.entity.CredenzialiAmministratoriEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ServerSpringAmministratoriController {
    @Qualifier("postgresCredenzialiAdminTable")
    private final Dao<CredenzialiAmministratoriEntity, Integer> dao;

    @Autowired
    public ServerSpringAmministratoriController (Dao<CredenzialiAmministratoriEntity, Integer> dao){
        this.dao = dao;
    }

    @RequestMapping(value="/ServerCineMates20/Admin/getHashPassword", method = RequestMethod.POST)
    public String getHashPassword(@RequestBody String emailHash) {
        System.out.println("EmailHash: " + emailHash);
        if(emailHash == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Argomento non valido, c'è bisogno del hash di una mail");
        String passwordHash = daoToAdministratorDao().getHashPassword(emailHash);
        if(passwordHash == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return passwordHash;
    }

    private AdministratorDao<CredenzialiAmministratoriEntity,Integer> daoToAdministratorDao() {
        if (!(dao instanceof AdministratorDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return (AdministratorDao<CredenzialiAmministratoriEntity,Integer>) dao;
    }
}