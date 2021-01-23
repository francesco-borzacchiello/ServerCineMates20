package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("postgresUserTable")
public class UserDaoImplementation implements UserDao<UtenteEntity, String> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<UtenteEntity> getAllFriends(UtenteEntity utente) throws IllegalArgumentException{
        if (utente == null) throw new IllegalArgumentException("Passare un utente non nullo");

        try {
            return new HashSet<>(jdbcTemplate.query(getSqlCommandForGetAllFriends(),
                                                    new String[] { utente.getUsername(), utente.getUsername() },
                                                    (resultSet, i) -> resultSetToUserEntity(resultSet)));
        } catch(DataAccessException e) {
            return null;
        }
    }

    private String getSqlCommandForGetAllFriends() {
        return "Select * " +
                "From public.\"Utente\" " +
                "Where public.\"Utente\".\"username\" in (SELECT public.\"Amici\".\"FK_Utente\" as \"ID_Amico\" " +
                                                            "FROM public.\"Amici\" " +
                                                            "WHERE public.\"Amici\".\"FK_Amico\" = ? " +
                                                            "UNION " +
                                                            "SELECT public.\"Amici\".\"FK_Amico\" as \"ID_Amico\" " +
                                                            "FROM public.\"Amici\" " +
                                                            "WHERE public.\"Amici\".\"FK_Utente\" = ?);";
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
            //Se non è stato rimosso nessun amico (nessun record modificato) restituisco "false"
            return jdbcTemplate.update(sql, utente.getUsername(), amicoDaEliminare.getUsername()) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public UtenteEntity getById(String email) {
        if (email == null)
            return null;

        final String sql = "SELECT * FROM public.\"Utente\" WHERE public.\"Utente\".email = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new String[] { email }, (resultSet, i) -> resultSetToUserEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    @Override
    public List<UtenteEntity> getAll() {
        final String sqlSelectAll = "SELECT * FROM public.\"Utente\"";

        try{
            return jdbcTemplate.query(sqlSelectAll, (resultSet, i) -> resultSetToUserEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    private UtenteEntity resultSetToUserEntity(ResultSet resultSet) throws  java.sql.SQLException{
        return new UtenteEntity(resultSet.getString("username"), resultSet.getString("nome"),
                                resultSet.getString("cognome"), resultSet.getString("email"),
                                TipologiaUtente.valueOf(resultSet.getString("tipoUtente")));
    }

    @Override
    public boolean insert(UtenteEntity newUtente) {
        if (newUtente == null)
            return false;

        try {
            //Se non è stato inserito nessun nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForInsert(),
                                        newUtente.getEmail(), newUtente.getNome(), newUtente.getCognome(),
                                        newUtente.getTipoUtente().toString(), newUtente.getUsername()) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForInsert() {
        return "INSERT INTO public.\"Utente\"(" +
                "email, nome, cognome, \"tipoUtente\", username)" +
                "VALUES (?, ?, ?, ?, ?);";
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