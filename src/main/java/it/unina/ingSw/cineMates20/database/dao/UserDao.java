package it.unina.ingSw.cineMates20.database.dao;

import java.util.Collection;

public interface UserDao<E, I> extends Dao<E, I> {

    Collection<E> getAllFriends(E e);

    boolean addFriend(E e);

    boolean removeFriend(E e);
}
