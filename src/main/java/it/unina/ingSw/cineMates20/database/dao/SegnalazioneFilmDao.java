package it.unina.ingSw.cineMates20.database.dao;

import java.util.List;

public interface SegnalazioneFilmDao<E, I> extends Dao<E,I>{
    List<E> getAllMovieReports(String userEmail);

    boolean updateUserDeleteNotification(E segnalazioneFilmEntity);

    boolean updateAdministratorHandledNotification(E segnalazioneFilmEntity);
}
