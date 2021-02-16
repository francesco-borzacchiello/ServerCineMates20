package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.SegnalazioneFilmEntity;
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

@Repository("postgresSegnalazioneFilmTable")
public class SegnalazioneFilmDaoImplementation implements SegnalazioneFilmDao<SegnalazioneFilmEntity, Integer>{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SegnalazioneFilmDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SegnalazioneFilmEntity getById(Integer id) {
        return null;
    }

    @Override
    public List<SegnalazioneFilmEntity> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<SegnalazioneFilmEntity> getAllMovieReports(String userEmail) {
        try{
            return jdbcTemplate.query(getSqlCommandForAllMovieReports(),
                                      (resultSet, i) -> resultSetForUsersToSegnalazioneFilmEntity(resultSet), userEmail);
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForAllMovieReports() {
        return  "SELECT DISTINCT ON (\"FK_FilmSegnalato\") \"FK_FilmSegnalato\", \"EsitoSegnalazione\" " +
                "FROM \"SegnalazioneFilm\" " +
                "WHERE \"SegnalazioneFilm\".\"notifica_visibile_per_utente\" = 'true' " +
                " AND \"SegnalazioneFilm\".\"FK_UtenteSegnalatore\" = ?;";
    }

    @Override
    public List<SegnalazioneFilmEntity> getAllReportedMovies() {
        try{
            return jdbcTemplate.query(getSqlCommandForAllReportedMovies(),
                    (resultSet, i) -> resultSetForAdministratorsToSegnalazionePendenteFilmEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForAllReportedMovies() {
        return "SELECT \"FK_FilmSegnalato\", \"FK_UtenteSegnalatore\" , \"MessaggioSegnalazione\" " +
                "FROM \"SegnalazioneFilm\" " +
                "WHERE \"SegnalazioneFilm\".\"notifica_visibile_per_utente\" = 'false' " +
                " AND \"SegnalazioneFilm\".\"EsitoSegnalazione\" = 'Pendente' " +
                "ORDER BY \"SegnalazioneFilm\".\"FK_FilmSegnalato\";";
    }

    @Override
    public List<SegnalazioneFilmEntity> getAllManagedReportedMovies(String emailHash) {
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
            return jdbcTemplate.query(getSqlCommandForAllManagedReportedMovies(),
                    (resultSet, i) -> resultSetForAdministratorsToSegnalazioneGestitaFilmEntity(resultSet),
                    emailAdmin);
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForAllManagedReportedMovies() {
        return "SELECT \"FK_FilmSegnalato\", \"FK_UtenteSegnalatore\" , \"MessaggioSegnalazione\", \"EsitoSegnalazione\" " +
                "FROM \"SegnalazioneFilm\" " +
                "WHERE \"SegnalazioneFilm\".\"EsitoSegnalazione\" <> 'Pendente' AND \"SegnalazioneFilm\".\"FK_AmministratoreCheGestisce\" = ? " +
                "ORDER BY \"SegnalazioneFilm\".\"FK_FilmSegnalato\";";
    }

    private SegnalazioneFilmEntity resultSetForAdministratorsToSegnalazionePendenteFilmEntity(ResultSet resultSet) throws java.sql.SQLException {
        return new SegnalazioneFilmEntity(resultSet.getLong("FK_FilmSegnalato"),
                resultSet.getString("FK_UtenteSegnalatore"),
                resultSet.getString("MessaggioSegnalazione"),
                null);
    }

    private SegnalazioneFilmEntity resultSetForAdministratorsToSegnalazioneGestitaFilmEntity(ResultSet resultSet) throws java.sql.SQLException {
        return new SegnalazioneFilmEntity(resultSet.getLong("FK_FilmSegnalato"),
                resultSet.getString("FK_UtenteSegnalatore"),
                resultSet.getString("MessaggioSegnalazione"),
                TipoSegnalazione.valueOf(resultSet.getString("EsitoSegnalazione")));
    }

    private SegnalazioneFilmEntity resultSetForUsersToSegnalazioneFilmEntity(ResultSet resultSet) throws java.sql.SQLException {
        return new SegnalazioneFilmEntity(resultSet.getLong("FK_FilmSegnalato"),
                  null, null,
                   TipoSegnalazione.valueOf(resultSet.getString("EsitoSegnalazione")));
    }

    @Override
    public boolean insert(SegnalazioneFilmEntity newSegnalazioneFilm) {
        if (newSegnalazioneFilm == null ||
                newSegnalazioneFilm.getFkAmministratoreCheGestisce() != null ||
                newSegnalazioneFilm.getDataGestione() != null)
            return false;

        newSegnalazioneFilm.setEsitoSegnalazione(TipoSegnalazione.Pendente);

        try {
            //Se non è stato inserito nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForInsert(),
                    newSegnalazioneFilm.getFkFilmSegnalato(),
                    newSegnalazioneFilm.getFkUtenteSegnalatore(),
                    newSegnalazioneFilm.getEsitoSegnalazione().toString(),
                    newSegnalazioneFilm.getFkAmministratoreCheGestisce(),
                    newSegnalazioneFilm.getMessaggioSegnalazione(),
                    newSegnalazioneFilm.getDataSegnalazione(),
                    newSegnalazioneFilm.getDataGestione(), false) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean update(SegnalazioneFilmEntity segnalazioneFilmEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updateUserDeleteNotification(SegnalazioneFilmEntity segnalazioneFilmEntity) {
        if (segnalazioneFilmEntity == null)
            return false;

        try {
            //Se non è stato modificato nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForUserNotificationDelete(),
                                 segnalazioneFilmEntity.getFkFilmSegnalato(),
                                       segnalazioneFilmEntity.getFkUtenteSegnalatore(), true) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForUserNotificationDelete() {
        return  "UPDATE \"SegnalazioneFilm\" " +
                "SET \"notifica_visibile_per_utente\" = 'false' " +
                "WHERE \"FK_FilmSegnalato\" = ? " +
                "      AND \"FK_UtenteSegnalatore\" = ? AND " +
                       " notifica_visibile_per_utente = ?;";
    }

    @Override
    public boolean updateAdministratorHandledMovieReport(SegnalazioneFilmEntity segnalazioneFilmEntity) {
        String emailHash = segnalazioneFilmEntity.getFkAmministratoreCheGestisce();
        if(emailHash == null) return false;

        List<String> adminMails = getAllEmailAdmin();
        if(adminMails == null) return false;

        String emailAdmin = null;
        for(String email: adminMails)
            if(BCrypt.checkpw(email, emailHash)){
                emailAdmin = email;
                break;
            }

        if(emailAdmin == null) return false;

        segnalazioneFilmEntity.setFkAmministratoreCheGestisce(emailAdmin);
        segnalazioneFilmEntity.setDataGestione(Timestamp.from(Instant.now()));

        try {
            if(segnalazioneFilmEntity.getEsitoSegnalazione().equals(TipoSegnalazione.valueOf("Oscurata")))
                //Risoluzione a cascata per tutti i segnalatori di questo film
                return jdbcTemplate.update(getSqlCommandForAdminReportUpdateCascade(),
                    segnalazioneFilmEntity.getFkAmministratoreCheGestisce(),
                    segnalazioneFilmEntity.getDataGestione(),
                    segnalazioneFilmEntity.getEsitoSegnalazione().toString(),
                    true,
                    segnalazioneFilmEntity.getFkFilmSegnalato()) != 0;
            else
                return jdbcTemplate.update(getSqlCommandForAdminReportUpdate(),
                    segnalazioneFilmEntity.getFkAmministratoreCheGestisce(),
                    segnalazioneFilmEntity.getDataGestione(),
                    segnalazioneFilmEntity.getEsitoSegnalazione().toString(),
                    true,
                    segnalazioneFilmEntity.getFkFilmSegnalato(),
                    segnalazioneFilmEntity.getFkUtenteSegnalatore()) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    //Risolve tutte le segnalazioni multiple di uno stesso utente
    private String getSqlCommandForAdminReportUpdate() {
        return  "UPDATE \"SegnalazioneFilm\" " +
                "SET \"FK_AmministratoreCheGestisce\" = ?, \"DataGestione\" = ?, \"EsitoSegnalazione\" = ?, \"notifica_visibile_per_utente\" = ? " +
                "WHERE \"FK_FilmSegnalato\" = ? AND \"FK_UtenteSegnalatore\" = ? " +
                     " AND \"EsitoSegnalazione\" = 'Pendente';";
    }

    //Risolve tutte le segnalazioni di un film pendenti
    private String getSqlCommandForAdminReportUpdateCascade() {
        return  "UPDATE \"SegnalazioneFilm\" " +
                "SET \"FK_AmministratoreCheGestisce\" = ?, \"DataGestione\" = ?, \"EsitoSegnalazione\" = ?, \"notifica_visibile_per_utente\" = ? " +
                "WHERE \"FK_FilmSegnalato\" = ? AND \"EsitoSegnalazione\" = 'Pendente';";
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

    @Override
    public boolean delete(SegnalazioneFilmEntity segnalazioneFilmEntity) {
        throw new UnsupportedOperationException();
    }

    private String getSqlCommandForInsert() {
        return "INSERT INTO public.\"SegnalazioneFilm\"(" +
                "\"FK_FilmSegnalato\", \"FK_UtenteSegnalatore\", \"EsitoSegnalazione\", \"FK_AmministratoreCheGestisce\", " +
                "\"MessaggioSegnalazione\", \"DataSegnalazione\", \"DataGestione\", \"Id\",\"notifica_visibile_per_utente\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, nextval('pk_segnalazione_film'), ?);";
    }
}
