package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.SegnalazioneFilmEntity;
import it.unina.ingSw.cineMates20.database.entity.SegnalazioneUtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        return null;
    }

    @Override
    public boolean insert(SegnalazioneFilmEntity newSegnalazioneFilm) {
        if (newSegnalazioneFilm == null ||
                newSegnalazioneFilm.getFkAmministratoreCheGestisce() != null ||
                newSegnalazioneFilm.getDataGestione() != null ||
                newSegnalazioneFilm.getEsitoSegnalazione() != TipoSegnalazione.Pendente)
            return false;

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
        return false;
    }

    @Override
    public boolean delete(SegnalazioneFilmEntity segnalazioneFilmEntity) {
        return false;
    }

    private String getSqlCommandForInsert() {
        return "INSERT INTO public.\"SegnalazioneFilm\"(" +
                "\"FK_FilmSegnalato\", \"FK_UtenteSegnalatore\", \"EsitoSegnalazione\", \"FK_AmministratoreCheGestisce\", " +
                "\"MessaggioSegnalazione\", \"DataSegnalazione\", \"DataGestione\", \"Id\",\"notifica_visibile_per_utente\") " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, nextval('pk_segnalazione_film'), ?);";
    }

}
