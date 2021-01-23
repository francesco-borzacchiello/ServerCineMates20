package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.ListaFilmEntity;

import java.util.List;

public interface ListaFilmDao<E, I> extends Dao<E, I>{

    boolean addFilm(long idListaFilm, long idFilm);

    boolean removeFilm(long idListaFilm, long idFilm);

    List<Long> getAllFilm(long idListaFilm);

    ListaFilmEntity getPreferitiByPossessore(String idPossessore);

    ListaFilmEntity getDaVedereByPossessore(String idPossessore);

    ListaFilmEntity getByPossessore(String idPossessore, String nomeLista);
}