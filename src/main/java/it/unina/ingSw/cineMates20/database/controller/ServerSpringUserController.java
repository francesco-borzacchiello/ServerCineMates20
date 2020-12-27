package it.unina.ingSw.cineMates20.database.controller;

import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ServerCineMates20/User")
public class ServerSpringUserController {

    @Autowired
    @Qualifier("postgresUserTable")
    private Dao<UtenteEntity,String> dao;

    @PostMapping
    public void addUser(@RequestBody UtenteEntity utente) {
        dao.insert(utente);
    }

    @GetMapping(path = "{username}")
    public UtenteEntity getUserById(@PathVariable("username") String username) {
        return dao.getById(username);
        //TODO: aggiungere restituzione errore numerico in caso di risultato query vuota
    }
}