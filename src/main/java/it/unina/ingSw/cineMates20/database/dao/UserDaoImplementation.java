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
    public Set<UtenteEntity> getUsersByQuery(String query) throws IllegalArgumentException {
        if(query == null) throw new IllegalArgumentException("Passare una query non nulla");

        try {
            return new HashSet<>(jdbcTemplate.query(getSqlCommandForGetUsersByQuery(),
                    new String[] { query, query, query, query, query, query },
                    (resultSet, i) -> resultSetToUserEntity(resultSet)));
        } catch(DataAccessException e) {
            return null;
        }
    }

    private String getSqlCommandForGetUsersByQuery() {
        return  "SELECT * " +
                "FROM \"Utente\" " +
                "WHERE LOWER(nome) LIKE '%' || TRIM(LOWER(?)) || '%' " +
                "      OR LOWER(cognome) LIKE '%' || TRIM(LOWER(?)) || '%' " +
                "      OR username LIKE '%' || TRIM(?) || '%' " +
                "      OR LOWER(email) LIKE '%' || TRIM (LOWER(?)) || '%' " +
                "      OR LOWER(nome || ' ' || cognome) " +
                "         LIKE '%' || TRIM(LOWER(REPLACE(REPLACE(REPLACE(?, ' ', '*^'), '^*', ''), '*^', ' '))) || '%' " +
                "      OR LOWER(cognome || ' ' || nome) " +
                "         LIKE '%' || TRIM(LOWER(REPLACE(REPLACE(REPLACE(?, ' ', '*^'), '^*', ''), '*^', ' '))) || '%';";
    }

    @Override
    public Set<UtenteEntity> getAllFriends(UtenteEntity utente) throws IllegalArgumentException {
        return queryFriendsTable(utente);
    }

    @Override
    public Set<UtenteEntity> getPendingFriendRequests(UtenteEntity utente) throws IllegalArgumentException {
        if (utente == null) throw new IllegalArgumentException("Passare un utente non nullo");

        try {
            return new HashSet<>(jdbcTemplate.query(getSqlCommandForGetAllFriends(),
                    new Object[] { utente.getEmail(), false, null, null },
                    (resultSet, i) -> resultSetToUserEntity(resultSet)));
        } catch(DataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean isFriendRequestPending(String userEmail, String friendEmail) throws IllegalArgumentException {
        if (userEmail == null || friendEmail == null) throw new IllegalArgumentException("Passare un'email non nulla");

        try {
            Boolean result = jdbcTemplate.queryForObject(getSqlCommandForIsFriendRequestPending(),
                                                         new String[]{ userEmail, friendEmail, friendEmail, userEmail }, Boolean.class);
            if(result != null)
                return !result; //Se confermata = false, allora la richiesta è pendente
            else
                return false;
        }
        catch(Exception e) {
            return false;
        }
    }

    private String getSqlCommandForIsFriendRequestPending() {
        return  "SELECT confermata " +
                "FROM  \"Amici\" " +
                "WHERE ((\"Amici\".\"Email_Utente\" = ? AND \"Amici\".\"Email_Amico\" = ?) " +
                "       OR (\"Amici\".\"Email_Utente\" = ? AND \"Amici\".\"Email_Amico\" = ?)) " +
                "AND confermata = 'false';";
    }

    private Set<UtenteEntity> queryFriendsTable(UtenteEntity utente) throws IllegalArgumentException {
        if (utente == null) throw new IllegalArgumentException("Passare un utente non nullo");

        try {
            return new HashSet<>(jdbcTemplate.query(getSqlCommandForGetAllFriends(),
                    new Object[] { utente.getEmail(), true, utente.getEmail(), true },
                    (resultSet, i) -> resultSetToUserEntity(resultSet)));
        } catch(DataAccessException e) {
            return null;
        }
    }

    private String getSqlCommandForGetAllFriends() {
        return  "Select * " +
                "From public.\"Utente\" " +
                "Where public.\"Utente\".\"email\" in (SELECT public.\"Amici\".\"Email_Utente\" as \"ID_Amico\" " +
                                                      "FROM public.\"Amici\" " +
                                                      "WHERE public.\"Amici\".\"Email_Amico\" = ? AND confermata = ?" +
                                                      " UNION " +
                                                      "SELECT public.\"Amici\".\"Email_Amico\" as \"ID_Amico\" " +
                                                      "FROM public.\"Amici\" " +
                                                      "WHERE public.\"Amici\".\"Email_Utente\" = ? AND confermata = ?);";
    }

    //Invia richiesta di amicizia
    @Override
    public boolean addFriend(UtenteEntity utente, UtenteEntity amicoDaAggiungere) {
        if (utente == null || amicoDaAggiungere == null)
            return false;

        final String sql = "INSERT INTO public.\"Amici\"(\"Email_Utente\",\"Email_Amico\",confermata) VALUES (?,?,?);";

        try {
            int affectedRows = jdbcTemplate.update(sql, utente.getEmail(), amicoDaAggiungere.getEmail(), false);
            //Se non è stato aggiunto nessun amico (nessun record modificato) restituisco "false"
            return affectedRows != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean confirmFriendRequest(UtenteEntity utente, UtenteEntity amicoDaConfermare) {
        if (utente == null || amicoDaConfermare == null)
            return false;

        final String sql = "UPDATE public.\"Amici\" " +
                           "SET confermata = ? " +
                           "WHERE ((\"Amici\".\"Email_Utente\" = ? AND \"Amici\".\"Email_Amico\" = ?) " +
                           "OR (\"Amici\".\"Email_Utente\" = ? AND \"Amici\".\"Email_Amico\" = ?)) AND confermata = ?;";

        try {
            int affectedRows = jdbcTemplate.update(sql, true, utente.getEmail(), amicoDaConfermare.getEmail(),
                    amicoDaConfermare.getEmail(), utente.getEmail(), false);
            //Se non è stato aggiunto nessun amico (nessun record modificato) restituisco "false"
            return affectedRows != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    /**
     * Elimina amicizia o richiesta di amicizia
     */
    @Override
    public boolean deleteFriend(UtenteEntity utente, UtenteEntity amicoDaEliminare) {
        if (utente == null || amicoDaEliminare == null)
            return false;

        final String sql = "DELETE FROM public.\"Amici\" " +
                           "WHERE (\"Amici\".\"Email_Utente\" = ? AND \"Amici\".\"Email_Amico\" = ?) " +
                                   "OR (\"Amici\".\"Email_Utente\" = ? AND \"Amici\".\"Email_Amico\" = ?)";

        try {
            //Se non è stato rimosso nessun amico (nessun record modificato) restituisco "false"
            return jdbcTemplate.update(sql, utente.getEmail(), amicoDaEliminare.getEmail(),
                    amicoDaEliminare.getEmail(), utente.getEmail()) != 0;
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
        final String sqlSelectAll = "SELECT * FROM public.\"Utente\" WHERE \"tipoUtente\" = 'utente';";

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

        final String sql = "DELETE FROM public.\"Utente\" WHERE public.\"Utente\".email = ?;";

        try {
            jdbcTemplate.update(sql, utente.getEmail());
            return true;
        } catch(DataAccessException e){
            return false;
        }
    }
}