package it.unina.ingSw.cineMates20.database.dao;

import java.util.List;

public interface ListaFilmDao<E, I> extends Dao<E, I>{

    boolean addFilm(E listaFilm, long idFilm);

    boolean removeFilm(E listaFilm, long idFilm);

    boolean containsFilm(long idLista, long idFilm);

    List<Long> getAllFilm(E listaFilm);

    E getPreferitiByPossessore(String idPossessore);

    E getDaVedereByPossessore(String idPossessore);

    E getByPossessore(String idPossessore, String nomeLista);
}