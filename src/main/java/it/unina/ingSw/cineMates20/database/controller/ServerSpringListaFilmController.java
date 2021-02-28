package it.unina.ingSw.cineMates20.database.controller;

import it.unina.ingSw.cineMates20.database.dao.Dao;
import it.unina.ingSw.cineMates20.database.dao.ListaFilmDao;
import it.unina.ingSw.cineMates20.database.entity.ListaFilmEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class ServerSpringListaFilmController {
    @Qualifier("postgresListaFilmTable")
    private final Dao<ListaFilmEntity, Integer> dao;

    @Autowired
    public ServerSpringListaFilmController(Dao<ListaFilmEntity, Integer> dao) {
        this.dao = dao;
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/add", method=RequestMethod.POST)
    public void addListaFilm(@RequestBody ListaFilmEntity lista) {
        boolean insert = dao.insert(lista);
        if (!insert)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La lista esiste già oppure il possessore non esiste.");
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/getPreferitiByPossessore/{Email_Possessore}", method = RequestMethod.GET)
    public ListaFilmEntity getListaFilmPreferitiByPossessore(@PathVariable("Email_Possessore") String idPossessore) {
        ListaFilmEntity listaFilm = daoToListaFilmDao().getPreferitiByPossessore(idPossessore);

        if (listaFilm == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista non esiste.");

        return listaFilm;
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/getDaVedereByPossessore/{Email_Possessore}", method = RequestMethod.GET)
    public ListaFilmEntity getListaFilmDaVedereByPossessore(@PathVariable("Email_Possessore") String idPossessore) {
        ListaFilmEntity listaFilm = daoToListaFilmDao().getDaVedereByPossessore(idPossessore);

        if (listaFilm == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista non esiste.");

        return listaFilm;
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/addFilmToListaFilm/{FK_Film}", method = RequestMethod.POST)
    public void addFilmByIdListaFilm(@RequestBody ListaFilmEntity listaFilm, @PathVariable("FK_Film") long idFilm) {
        if (!daoToListaFilmDao().addFilm(listaFilm, idFilm))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Il film o la lista non esiste.");
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/removeFilmFromListaFilm/{FK_Film}", method = RequestMethod.POST)
    public void removeFilmByIdListaFilm(@RequestBody ListaFilmEntity listaFilm, @PathVariable("FK_Film") long idFilm) {
        if (!daoToListaFilmDao().removeFilm(listaFilm, idFilm))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Il film o la lista non esiste oppure la lista è vuota.");
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/containsFilm/{id}/{FK_Film}", method = RequestMethod.GET)
    public boolean containsFilm(@PathVariable("id") long idLista, @PathVariable("FK_Film") long idFilm) {
        return daoToListaFilmDao().containsFilm(idLista, idFilm);
    }

    @RequestMapping(value="/ServerCineMates20/ListaFilm/getAll", method = RequestMethod.POST)
    public List<Long> getAllFilmByIdListaFilm(@RequestBody ListaFilmEntity listaFilm) {
        List<Long> list = daoToListaFilmDao().getAllFilm(listaFilm);

        if (list == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La lista non esiste.");
        return list;
    }

    private ListaFilmDao<ListaFilmEntity,Integer> daoToListaFilmDao() {
        if (!(dao instanceof ListaFilmDao))
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        return (ListaFilmDao<ListaFilmEntity,Integer>) dao;
    }
}
