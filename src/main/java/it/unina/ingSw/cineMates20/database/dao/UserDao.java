package it.unina.ingSw.cineMates20.database.dao;

import java.util.Set;

public interface UserDao<E, I> extends Dao<E, I> {

    Set<E> getAllFriends(E user);

    boolean addFriend(E user, E friendToAdd);

    boolean deleteFriend(E user, E friendToRemove);
}
