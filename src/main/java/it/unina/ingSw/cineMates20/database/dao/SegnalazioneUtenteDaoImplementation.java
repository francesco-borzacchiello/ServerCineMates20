package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.SegnalazioneUtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
        return null;
    }

    @Override
    public boolean insert(SegnalazioneUtenteEntity newSegnalazioneUtente) {
        if (newSegnalazioneUtente == null ||
           newSegnalazioneUtente.getFkAmministratoreCheGestisce() != null ||
           newSegnalazioneUtente.getDataGestione() != null ||
           newSegnalazioneUtente.getEsitoSegnalazione() != TipoSegnalazione.Pendente)
            return false;

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
        return false;
    }

    @Override
    public boolean delete(SegnalazioneUtenteEntity segnalazioneUtenteEntity) {
        return false;
    }
}
