package it.unina.ingSw.cineMates20.database.dao;

import java.util.List;

public interface SegnalazioneFilmDao<E, I> extends Dao<E,I>{
    List<E> getAllMovieReports(String userEmail);

    List<E> getAllReportedMovies();

    List<E> getAllManagedReportedMovies(String emailHash);

    boolean updateUserDeleteNotification(E segnalazioneFilmEntity);

    boolean updateAdministratorHandledMovieReport(E segnalazioneFilmEntity);
}
