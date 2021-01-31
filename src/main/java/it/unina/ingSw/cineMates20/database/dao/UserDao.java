package it.unina.ingSw.cineMates20.database.dao;

import java.util.Set;

public interface UserDao<E, I> extends Dao<E, I> {

    Set<E> getUsersByQuery(String query);

    Set<E> getAllFriends(E user);

    Set<E> getPendingFriendRequests(E user);

    boolean isFriendRequestPending(String userEmail, String friendEmail);

    boolean addFriend(E user, E friendToAdd);

    boolean deleteFriend(E user, E friendToRemove);

    boolean confirmFriendRequest(E user, E friendToConfirm);
}