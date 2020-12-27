package it.unina.ingSw.cineMates20.database.dao;

import java.util.List;

public interface Dao<E,I> { //E: Entity, I: Identifier
    //@Autowired
    //JdbcTemplate jdbcTemplate = new JdbcTemplate();

    E getById(I id);

    List<E> getAll();

    boolean insert(E e);

    boolean update(E e);

    boolean delete(E e);
}
