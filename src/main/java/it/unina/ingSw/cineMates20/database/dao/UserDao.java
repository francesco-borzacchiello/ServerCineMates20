package it.unina.ingSw.cineMates20.database.dao;

import java.util.Collection;

public interface UserDao<E, I> extends Dao<E, I> {

    Collection<E> getAllFriends(E user);

    boolean addFriend(E user, E friendToAdd);

    boolean deleteFriend(E user, E friendToRemove);
}
