package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.ListaFilmEntity;

import java.util.List;

public interface ListaFilmDao<E, I> extends Dao<E, I>{

    boolean addFilm(E ListaFilm, long idFilm);

    boolean removeFilm(E ListaFilm, long idFilm);

    boolean containsFilm(long idLista, long idFilm);

    List<Long> getAllFilm(E ListaFilm);

    E getPreferitiByPossessore(String idPossessore);

    E getDaVedereByPossessore(String idPossessore);

    E getByPossessore(String idPossessore, String nomeLista);
}