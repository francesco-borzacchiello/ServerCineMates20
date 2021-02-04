package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.SegnalazioneFilmEntity;
import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
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
                                      (resultSet, i) -> resultSetToSegnalazioneFilmEntity(resultSet), userEmail);
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

    private SegnalazioneFilmEntity resultSetToSegnalazioneFilmEntity(ResultSet resultSet) throws java.sql.SQLException {
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
    public boolean updateAdministratorHandledNotification(SegnalazioneFilmEntity segnalazioneFilmEntity) {
        throw new UnsupportedOperationException(); //TODO: da completare durante applicativo desktop
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
