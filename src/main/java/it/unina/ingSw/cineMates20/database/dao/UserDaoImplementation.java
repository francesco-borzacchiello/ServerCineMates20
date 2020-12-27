package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.dao.rowMapper.UserRowMapper;
import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository("postgresUserTable")
public class UserDaoImplementation implements UserDao<UtenteEntity, String> {

    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Override
    public Collection<UtenteEntity> getAllFriends(UtenteEntity utenteEntity) {
        return null;
    }

    @Override
    public boolean addFriend(UtenteEntity utenteEntity) {
        return false;
    }

    @Override
    public boolean removeFriend(UtenteEntity utenteEntity) {
        return false;
    }

    @Override
    public UtenteEntity getById(String id) {
        if (id == null)
            return null;

        final String sql = "SELECT * FROM public.\"Utente\" WHERE public.\"Utente\".username = '" + id + "';";

        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper());
        }catch(DataAccessException e){
            return null;
        }
    }

    @Override
    public List<UtenteEntity> getAll() {
        return null;
    }

    @Override
    public boolean insert(UtenteEntity newUtente) {
        if (newUtente == null)
            return false;

        final String sql = "INSERT INTO public.\"Utente\"(" +
                "username, nome, cognome, email, password, \"dataRegistrazione\", \"tipoUtente\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            jdbcTemplate.update(sql, newUtente.getUsername(), newUtente.getNome(), newUtente.getCognome(), newUtente.getEmail(),
                    newUtente.getPassword(), newUtente.getDataRegistrazione(), newUtente.getTipoUtente().toString());
            return true;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean update(UtenteEntity utenteEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(UtenteEntity utenteEntity) {
        //TODO
        return false;
    }
}
