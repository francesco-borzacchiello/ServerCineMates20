package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;

public interface AdministratorDao<E, I> extends Dao<E, I>{
    String getHashPassword(String hashEmail);

    boolean emailHashAlreadyExists(String hashEmail);

    UtenteEntity getBasicAdminInfo(String emailHash);
}
