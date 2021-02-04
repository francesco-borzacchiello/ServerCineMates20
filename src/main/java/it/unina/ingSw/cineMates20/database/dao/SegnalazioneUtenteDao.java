package it.unina.ingSw.cineMates20.database.dao;

import java.util.List;

public interface SegnalazioneUtenteDao<E, I> extends Dao<E,I>{
    List<E> getAllUsersReports(String userEmail);

    boolean updateUserDeleteNotification(E segnalazioneUtenteEntity);

    boolean updateAdministratorHandledNotification(E segnalazioneUtenteEntity);
}
