package it.unina.ingSw.cineMates20.database.dao;

import java.util.List;

public interface SegnalazioneUtenteDao<E, I> extends Dao<E,I>{
    List<E> getAllUsersReports(String userEmail);

    List<E> getAllReportedUsers();

    List<E> getAllManagedReportedUsers(String emailHash);

    boolean updateUserDeleteNotification(E segnalazioneUtenteEntity);

    boolean updateAdministratorHandledUserReport(E segnalazioneUtenteEntity);
}
