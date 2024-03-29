package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.SegnalazioneUtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository("postgresSegnalazioneUtenteTable")
public class SegnalazioneUtenteDaoImplementation implements SegnalazioneUtenteDao<SegnalazioneUtenteEntity, Integer>{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SegnalazioneUtenteDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SegnalazioneUtenteEntity getById(Integer id) {
        return null;
    }

    @Override
    public List<SegnalazioneUtenteEntity> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SegnalazioneUtenteEntity> getAllUsersReports(String userEmail) {
        try{
            return jdbcTemplate.query(getSqlCommandForAllUsersReports(),
                                      (resultSet, i) -> resultSetForUsersToSegnalazioneUtenteEntity(resultSet), userEmail);
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForAllUsersReports() {
        return  "SELECT DISTINCT ON (\"FK_UtenteSegnalato\") \"FK_UtenteSegnalato\", \"EsitoSegnalazione\" " +
                "FROM \"SegnalazioneUtente\" " +
                "WHERE \"SegnalazioneUtente\".\"notifica_visibile_per_utente\" = 'true' " +
                " AND \"SegnalazioneUtente\".\"FK_UtenteSegnalatore\" = ?;";
    }

    @Override
    public List<SegnalazioneUtenteEntity> getAllReportedUsers() {
        try{
            return jdbcTemplate.query(getSqlCommandForAllReportedUsers(),
                    (resultSet, i) -> resultSetForAdministratorsToSegnalazionePendenteUtenteEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForAllReportedUsers() {
        return "SELECT \"FK_UtenteSegnalato\", \"FK_UtenteSegnalatore\" , \"MessaggioSegnalazione\" " +
                "FROM \"SegnalazioneUtente\" " +
                "WHERE \"SegnalazioneUtente\".\"notifica_visibile_per_utente\" = 'false' " +
                " AND \"SegnalazioneUtente\".\"EsitoSegnalazione\" = 'Pendente' " +
                "ORDER BY \"SegnalazioneUtente\".\"FK_UtenteSegnalato\";";
    }

    @Override
    public List<SegnalazioneUtenteEntity> getAllManagedReportedUsers(String emailHash) {
        String emailAdmin = null;

        List<String> adminMails = getAllEmailAdmin();
        if(adminMails == null) return null;

        for(String email: adminMails)
            if(BCrypt.checkpw(email, emailHash)){
                emailAdmin = email;
                break;
            }

        if(emailAdmin == null) return null;

        try{
            return jdbcTemplate.query(getSqlCommandForAllManagedReportedUsers(),
                    (resultSet, i) -> resultSetForAdministratorsToSegnalazioneGestitaUtenteEntity(resultSet),
                    emailAdmin);
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForAllManagedReportedUsers() {
        return "SELECT \"FK_UtenteSegnalato\", \"FK_UtenteSegnalatore\" , \"MessaggioSegnalazione\", \"EsitoSegnalazione\" " +
                "FROM \"SegnalazioneUtente\" " +
                "WHERE \"SegnalazioneUtente\".\"EsitoSegnalazione\" <> 'Pendente' AND \"SegnalazioneUtente\".\"FK_AmministratoreCheGestisce\" = ? " +
                "ORDER BY \"SegnalazioneUtente\".\"FK_UtenteSegnalato\";";
    }

    private SegnalazioneUtenteEntity resultSetForAdministratorsToSegnalazionePendenteUtenteEntity(ResultSet resultSet) throws java.sql.SQLException {
        return new SegnalazioneUtenteEntity(resultSet.getString("FK_UtenteSegnalato"),
                resultSet.getString("FK_UtenteSegnalatore"),
                resultSet.getString("MessaggioSegnalazione"),
                null);
    }

    private SegnalazioneUtenteEntity resultSetForAdministratorsToSegnalazioneGestitaUtenteEntity(ResultSet resultSet) throws java.sql.SQLException {
        return new SegnalazioneUtenteEntity(resultSet.getString("FK_UtenteSegnalato"),
                resultSet.getString("FK_UtenteSegnalatore"),
                resultSet.getString("MessaggioSegnalazione"),
                TipoSegnalazione.valueOf(resultSet.getString("EsitoSegnalazione")));
    }

    private SegnalazioneUtenteEntity resultSetForUsersToSegnalazioneUtenteEntity(ResultSet resultSet) throws java.sql.SQLException {
        return new SegnalazioneUtenteEntity(resultSet.getString("FK_UtenteSegnalato"),
                null, null,
                TipoSegnalazione.valueOf(resultSet.getString("EsitoSegnalazione")));
    }

    @Override
    public boolean insert(SegnalazioneUtenteEntity newSegnalazioneUtente) {
        if (newSegnalazioneUtente == null ||
            newSegnalazioneUtente.getFkAmministratoreCheGestisce() != null ||
            newSegnalazioneUtente.getDataGestione() != null)
            return false;

        newSegnalazioneUtente.setEsitoSegnalazione(TipoSegnalazione.Pendente);

        try {
            //Se non è stato inserito nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForInsert(),
                    newSegnalazioneUtente.getFkUtenteSegnalato(),
                    newSegnalazioneUtente.getFkUtenteSegnalatore(),
                    newSegnalazioneUtente.getEsitoSegnalazione().toString(),
                    newSegnalazioneUtente.getFkAmministratoreCheGestisce(),
                    newSegnalazioneUtente.getMessaggioSegnalazione(),
                    newSegnalazioneUtente.getDataSegnalazione(),
                    newSegnalazioneUtente.getDataGestione(), false) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForInsert() {
        return "INSERT INTO public.\"SegnalazioneUtente\"(" +
                "\"FK_UtenteSegnalato\", \"FK_UtenteSegnalatore\", \"EsitoSegnalazione\", \"FK_AmministratoreCheGestisce\", " +
                "\"MessaggioSegnalazione\", \"DataSegnalazione\", \"DataGestione\", \"Id\",\"notifica_visibile_per_utente\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, nextval('pk_segnalazione_utente'), ?);";
    }

    @Override
    public boolean update(SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updateUserDeleteNotification(SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        if (segnalazioneUtenteEntity == null)
            return false;

        try {
            //Se non è stato modificato nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForUserNotificationDelete(),
                                 segnalazioneUtenteEntity.getFkUtenteSegnalato(),
                                       segnalazioneUtenteEntity.getFkUtenteSegnalatore(), true) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForUserNotificationDelete() {
        return  "UPDATE \"SegnalazioneUtente\" " +
                "SET \"notifica_visibile_per_utente\"= 'false' " +
                "WHERE \"FK_UtenteSegnalato\" = ? " +
                     " AND \"FK_UtenteSegnalatore\" = ? " +
                     " AND notifica_visibile_per_utente = ?;";
    }

    @Override
    public boolean updateAdministratorHandledUserReport(SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        String emailHash = segnalazioneUtenteEntity.getFkAmministratoreCheGestisce();
        if(emailHash == null) return false;

        String emailAdmin = null;

        List<String> adminMails = getAllEmailAdmin();
        if(adminMails == null) return false;

        for(String email: adminMails)
            if(BCrypt.checkpw(email, emailHash)){
                emailAdmin = email;
                break;
            }

        if(emailAdmin == null) return false;

        segnalazioneUtenteEntity.setFkAmministratoreCheGestisce(emailAdmin);
        segnalazioneUtenteEntity.setDataGestione(Timestamp.from(Instant.now()));

        try {
            if(segnalazioneUtenteEntity.getEsitoSegnalazione().equals(TipoSegnalazione.valueOf("Oscurata")))
                //Risoluzione a cascata per tutti i segnalatori di questo film
                return jdbcTemplate.update(getSqlCommandForAdminReportUpdateCascade(),
                        segnalazioneUtenteEntity.getFkAmministratoreCheGestisce(),
                        segnalazioneUtenteEntity.getDataGestione(),
                        segnalazioneUtenteEntity.getEsitoSegnalazione().toString(),
                        true,
                        segnalazioneUtenteEntity.getFkUtenteSegnalato()) != 0;
            else
                return jdbcTemplate.update(getSqlCommandForAdminReportUpdate(),
                        segnalazioneUtenteEntity.getFkAmministratoreCheGestisce(),
                        segnalazioneUtenteEntity.getDataGestione(),
                        segnalazioneUtenteEntity.getEsitoSegnalazione().toString(),
                        true,
                        segnalazioneUtenteEntity.getFkUtenteSegnalato(),
                        segnalazioneUtenteEntity.getFkUtenteSegnalatore()) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    //Risolve tutte le segnalazioni multiple di uno stesso utente
    private String getSqlCommandForAdminReportUpdate() {
        return  "UPDATE \"SegnalazioneUtente\" " +
                "SET \"FK_AmministratoreCheGestisce\" = ?, \"DataGestione\" = ?, \"EsitoSegnalazione\" = ?, \"notifica_visibile_per_utente\" = ? " +
                "WHERE \"FK_UtenteSegnalato\" = ? AND \"FK_UtenteSegnalatore\" = ? " +
                " AND \"EsitoSegnalazione\" = 'Pendente';";
    }

    private List<String> getAllEmailAdmin() {
        final String sqlSelectAllAdmin = "SELECT email FROM public.\"Utente\" WHERE \"tipoUtente\" = 'amministratore';";

        List<String> emailAdmin;
        try{
            emailAdmin = jdbcTemplate.query(sqlSelectAllAdmin, (rs, rownum) -> rs.getString("email"));
        }catch(DataAccessException e){
            return null;
        }
        return emailAdmin;
    }

    //Risolve tutte le segnalazioni di un utente pendenti
    private String getSqlCommandForAdminReportUpdateCascade() {
        return  "UPDATE \"SegnalazioneFilm\" " +
                "SET \"FK_AmministratoreCheGestisce\" = ?, \"DataGestione\" = ?, \"EsitoSegnalazione\" = ?, \"notifica_visibile_per_utente\" = ? " +
                "WHERE \"FK_FilmSegnalato\" = ? AND \"EsitoSegnalazione\" = 'Pendente';";
    }

    @Override
    public boolean delete(SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        throw new UnsupportedOperationException();
    }
}
