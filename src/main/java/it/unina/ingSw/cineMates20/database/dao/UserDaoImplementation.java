package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository("postgresUserTable")
public class UserDaoImplementation implements UserDao<UtenteEntity, String> {

    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Override
    public Collection<UtenteEntity> getAllFriends(UtenteEntity utente) {
        if (utente == null)
            return null;

        final String sql = "SELECT public.\"Amici\".\"FK_Utente\" " +
                           "FROM public.\"Amici\" " +
                           "WHERE public.\"Amici\".\"FK_Amico\" = ? " +
                           "UNION " +
                           "SELECT public.\"Amici\".\"FK_Amico\" " +
                           "FROM public.\"Amici\" " +
                           "WHERE public.\"Amici\".\"FK_Utente\" = ?";

        try {
            return jdbcTemplate.query(sql, new String[] { utente.getUsername(), utente.getUsername() },
                    //RowMapper<UtenteEntity> per mappare un amico ad un utente
                    (resultSet, i) -> {
                        String idAmico = resultSet.getString("FK_Utente");
                        if(resultSet.wasNull())
                            idAmico = resultSet.getString("FK_Amico");

                        if(resultSet.wasNull())
                            return null;

                        return getById(idAmico);
                    });
        } catch(DataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean addFriend(UtenteEntity utente, UtenteEntity amicoDaAggiungere) {
        if (utente == null || amicoDaAggiungere == null)
            return false;

        final String sql = "INSERT INTO public.\"Amici\"(\"FK_Utente\",\"FK_Amico\") VALUES (?,?);";

        try {
            int affectedRows = jdbcTemplate.update(sql, utente.getUsername(), amicoDaAggiungere.getUsername());
            //Se non è stato aggiunto nessun amico (nessun record modificato) restituisco "false"
            return affectedRows != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean deleteFriend(UtenteEntity utente, UtenteEntity amicoDaEliminare) {
        if (utente == null || amicoDaEliminare == null)
            return false;

        final String sql = "DELETE FROM public.\"Amici\" WHERE public.\"Amici\".\"FK_Utente\" = ? AND public.\"Amici\".\"FK_Amico\" = ?;";

        try {
            int affectedRows = jdbcTemplate.update(sql, utente.getUsername(), amicoDaEliminare.getUsername());
            //Se non è stato rimosso nessun amico (nessun record modificato) restituisco "false"
            return affectedRows != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public UtenteEntity getById(String id) {
        if (id == null)
            return null;

        final String sql = "SELECT * FROM public.\"Utente\" WHERE public.\"Utente\".username = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new String[] { id }, (resultSet, i) -> {
                String username = resultSet.getString("username");
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Timestamp dataRegistrazione = resultSet.getTimestamp("dataRegistrazione");
                TipologiaUtente tipoUtente = TipologiaUtente.valueOf(resultSet.getString("tipoUtente"));

                return new UtenteEntity(username, nome, cognome, email, password, dataRegistrazione, tipoUtente);
            });
        }catch(DataAccessException e){
            return null;
        }
    }

    @Override
    public List<UtenteEntity> getAll() {
        final String sql = "SELECT * FROM public.\"Utente\"";

        try{
            return jdbcTemplate.query(sql, (resultSet, i) -> {
                String username = resultSet.getString("username");
                String nome = resultSet.getString("nome");
                String cognome = resultSet.getString("cognome");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                Timestamp dataRegistrazione = resultSet.getTimestamp("dataRegistrazione");
                TipologiaUtente tipoUtente = TipologiaUtente.valueOf(resultSet.getString("tipoUtente"));

                return new UtenteEntity(username, nome, cognome, email, password, dataRegistrazione, tipoUtente);
            });
        }catch(DataAccessException e){
            return null;
        }
    }

    @Override
    public boolean insert(UtenteEntity newUtente) {
        if (newUtente == null)
            return false;

        final String sql = "INSERT INTO public.\"Utente\"(" +
                "username, nome, cognome, email, password, \"dataRegistrazione\", \"tipoUtente\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";

        try {
            int affectedRows = jdbcTemplate.update(sql, newUtente.getUsername(), newUtente.getNome(), newUtente.getCognome(), newUtente.getEmail(),
                    newUtente.getPassword(), newUtente.getDataRegistrazione(), newUtente.getTipoUtente().toString());
            //Se non è stato inserito nessun nessun record restituisco "false"
            return affectedRows != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean update(UtenteEntity utenteEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(UtenteEntity utente) {
        if (utente == null)
            return false;

        final String sql = "DELETE FROM public.\"Utente\" WHERE public.\"Utente\".username = ?;";

        try {
            jdbcTemplate.update(sql, utente.getUsername());
            return true;
        } catch(DataAccessException e){
            return false;
        }
    }
}
