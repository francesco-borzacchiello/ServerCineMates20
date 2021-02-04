package it.unina.ingSw.cineMates20.database.dao;

public interface AdministratorDao<E, I> extends Dao<E, I>{
    String getHashPassword(String hashEmail);
}
