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

    @Autowired
    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Override
    public Set<UtenteEntity> getAllFriends(UtenteEntity utente) throws IllegalArgumentException{
        if (utente == null) throw new IllegalArgumentException("Passare un utente non nullo");

        try {
            return new HashSet<>(jdbcTemplate.query(getSqlCommandForGetAllFriend(),
                                                                new String[] { utente.getUsername(), utente.getUsername() },
                                                                (resultSet, i) -> resulSetToUserEntity(resultSet)));
        } catch(DataAccessException e) {
            return null;
        }
    }

    private String getSqlCommandForGetAllFriend() {
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
    public UtenteEntity getById(String id) {
        if (id == null)
            return null;

        final String sql = "SELECT * FROM public.\"Utente\" WHERE public.\"Utente\".username = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new String[] { id }, (resultSet, i) -> resulSetToUserEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    @Override
    public List<UtenteEntity> getAll() {
        final String sqlSelectAll = "SELECT * FROM public.\"Utente\"";

        try{
            return jdbcTemplate.query(sqlSelectAll, (resultSet, i) -> resulSetToUserEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    private UtenteEntity resulSetToUserEntity(ResultSet resultSet) throws  java.sql.SQLException{
        return new UtenteEntity(resultSet.getString("username"), resultSet.getString("nome"),
                                resultSet.getString("cognome"), resultSet.getString("email"),
                                resultSet.getString("password"), resultSet.getTimestamp("dataRegistrazione"),
                                TipologiaUtente.valueOf(resultSet.getString("tipoUtente")));
    }

    @Override
    public boolean insert(UtenteEntity newUtente) {
        if (newUtente == null)
            return false;

        try {
            //Se non è stato inserito nessun nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForInsert(),
                                        newUtente.getUsername(), newUtente.getNome(), newUtente.getCognome(), newUtente.getEmail(),
                                        newUtente.getPassword(), newUtente.getDataRegistrazione(), newUtente.getTipoUtente().toString()) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForInsert() {
        return "INSERT INTO public.\"Utente\"(" +
                "username, nome, cognome, email, password, \"dataRegistrazione\", \"tipoUtente\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?);";
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