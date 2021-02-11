package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.CredenzialiAmministratoriEntity;
import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository("postgresCredenzialiAdminTable")
public class AdministratorDaoImplementation implements AdministratorDao<CredenzialiAmministratoriEntity, Integer> {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AdministratorDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getHashPassword(String hashEmail) {
        String[] listEmailAdmin = getAllEmailAdmin();
        String emailAdmin = null;
        if(listEmailAdmin == null) return null;

        for(String email : listEmailAdmin)
            if(BCrypt.checkpw(email, hashEmail)){
                emailAdmin = email;
                break;
            }

        if(emailAdmin == null) return null;
        final String sqlSelectAllHash = "SELECT * FROM public.\"CredenzialiAmministratori\";";

        List<CredenzialiAmministratoriEntity> listHash = null;
        try{
            listHash =
                    jdbcTemplate.query(sqlSelectAllHash, (resultSet, i) -> resultSetToCredenzialiAmministratoreEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }

        for (CredenzialiAmministratoriEntity actualHash : listHash)
            if(BCrypt.checkpw(emailAdmin, actualHash.getEmail()))
                return actualHash.getPassword();

        return null;
    }

    @Override
    public boolean emailHashAlreadyExists(String hashEmail) {
        String[] listEmailAdmin = getAllEmailAdmin();
        if(listEmailAdmin == null) return false;

        for(String email : listEmailAdmin)
            if(BCrypt.checkpw(email, hashEmail))
                return true;

        return false;
    }

    private CredenzialiAmministratoriEntity resultSetToCredenzialiAmministratoreEntity(ResultSet resultSet) throws SQLException {
        return new CredenzialiAmministratoriEntity(resultSet.getString("email"), resultSet.getString("password"));
    }

    private String[] getAllEmailAdmin() {
        final String sqlSelectAllAdmin = "SELECT email FROM public.\"Utente\" WHERE \"tipoUtente\" = 'amministratore';";

        String[] emailAdmin;
        try{
            emailAdmin =  jdbcTemplate.queryForObject(sqlSelectAllAdmin, String[].class);
        }catch(DataAccessException e){
            return null;
        }
        return emailAdmin;
    }

    @Override
    public UtenteEntity getBasicAdminInfo(String emailHash) {
        String[] adminEmail = getAllEmailAdmin();
        String decryptedEmailHash = null;

        for(String email : adminEmail)
            if(BCrypt.checkpw(email, emailHash))
                decryptedEmailHash = email;

        if(decryptedEmailHash == null) return null;

        final String sql = "SELECT \"Utente\".nome, \"Utente\".cognome FROM \"Utente\" WHERE \"Utente\".email = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new String[] { decryptedEmailHash },
                    (resultSet, i) -> resultSetToBasicAdminEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    private UtenteEntity resultSetToBasicAdminEntity(ResultSet resultSet) {
        try {
            return new UtenteEntity(null, resultSet.getString("nome"),
                    resultSet.getString("cognome"), null, TipologiaUtente.amministratore);
        } catch (SQLException throwables) {
            return null;
        }
    }

    @Override
    public CredenzialiAmministratoriEntity getById(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CredenzialiAmministratoriEntity> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean insert(CredenzialiAmministratoriEntity credenzialiAmministratoriEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update(CredenzialiAmministratoriEntity credenzialiAmministratoriEntity) {
        return false;
    }

    @Override
    public boolean delete(CredenzialiAmministratoriEntity credenzialiAmministratoriEntity) {
        throw new UnsupportedOperationException();
    }
}
