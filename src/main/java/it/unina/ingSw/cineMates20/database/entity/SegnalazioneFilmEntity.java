package it.unina.ingSw.cineMates20.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "SegnalazioneFilm", schema = "public", catalog = "CineMates20")
public class SegnalazioneFilmEntity {
    private long fkFilmSegnalato;
    private String fkUtenteSegnalatore;
    private String fkAmministratoreCheGestisce;
    private String messaggioSegnalazione;
    private Timestamp dataSegnalazione;
    private Timestamp dataGestione;
    private long id;
    private boolean notificaVisibilePerUtente;

    @Enumerated(EnumType.STRING)
    private TipoSegnalazione esitoSegnalazione;

    public SegnalazioneFilmEntity() {}

    public SegnalazioneFilmEntity(@JsonProperty("FK_FilmSegnalato") long fkFilmSegnalato,
                                  @JsonProperty("FK_UtenteSegnalatore") String fkUtenteSegnalatore,
                                  @JsonProperty("MessaggioSegnalazione") String messaggioSegnalazione,
                                  @JsonProperty("EsitoSegnalazione") TipoSegnalazione esitoSegnalazione) {
        this.fkFilmSegnalato = fkFilmSegnalato;
        this.fkUtenteSegnalatore = fkUtenteSegnalatore;
        this.messaggioSegnalazione = messaggioSegnalazione;
        this.dataSegnalazione = Timestamp.from(Instant.now());
        this.esitoSegnalazione = esitoSegnalazione;
    }

    @Basic
    @Column(name = "FK_FilmSegnalato")
    public long getFkFilmSegnalato() {
        return fkFilmSegnalato;
    }

    public void setFkFilmSegnalato(long fkFilmSegnalato) {
        this.fkFilmSegnalato = fkFilmSegnalato;
    }

    @Basic
    @Column(name = "FK_UtenteSegnalatore")
    public String getFkUtenteSegnalatore() {
        return fkUtenteSegnalatore;
    }

    public void setFkUtenteSegnalatore(String fkUtenteSegnalatore) {
        this.fkUtenteSegnalatore = fkUtenteSegnalatore;
    }

    @Basic
    @Column(name = "FK_AmministratoreCheGestisce")
    public String getFkAmministratoreCheGestisce() {
        return fkAmministratoreCheGestisce;
    }

    public void setFkAmministratoreCheGestisce(String fkAmministratoreCheGestisce) {
        this.fkAmministratoreCheGestisce = fkAmministratoreCheGestisce;
    }

    @Basic
    @Column(name = "EsitoSegnalazione")
    public TipoSegnalazione getEsitoSegnalazione() {
        return esitoSegnalazione;
    }

    public void setEsitoSegnalazione(TipoSegnalazione esitoSegnalazione) {
        this.esitoSegnalazione = esitoSegnalazione;
    }

    @Basic
    @Column(name = "MessaggioSegnalazione")
    public String getMessaggioSegnalazione() {
        return messaggioSegnalazione;
    }

    public void setMessaggioSegnalazione(String messaggioSegnalazione) {
        this.messaggioSegnalazione = messaggioSegnalazione;
    }

    @Basic
    @Column(name = "DataSegnalazione")
    public Timestamp getDataSegnalazione() {
        return dataSegnalazione;
    }

    public void setDataSegnalazione(Timestamp dataSegnalazione) {
        this.dataSegnalazione = dataSegnalazione;
    }

    @Basic
    @Column(name = "DataGestione")
    public Timestamp getDataGestione() {
        return dataGestione;
    }

    public void setDataGestione(Timestamp dataGestione) {
        this.dataGestione = dataGestione;
    }

    @Id
    @Column(name = "Id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "notifica_visibile_per_utente")
    public boolean isNotificaVisibilePerUtente() {
        return notificaVisibilePerUtente;
    }

    public void setNotificaVisibilePerUtente(boolean notificaVisibilePerUtente) {
        this.notificaVisibilePerUtente = notificaVisibilePerUtente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SegnalazioneFilmEntity that = (SegnalazioneFilmEntity) o;
        return fkFilmSegnalato == that.fkFilmSegnalato &&
                id == that.id &&
                notificaVisibilePerUtente == that.notificaVisibilePerUtente &&
                fkUtenteSegnalatore.equals(that.fkUtenteSegnalatore) &&
                Objects.equals(fkAmministratoreCheGestisce, that.fkAmministratoreCheGestisce) &&
                messaggioSegnalazione.equals(that.messaggioSegnalazione) &&
                dataSegnalazione.equals(that.dataSegnalazione) &&
                Objects.equals(dataGestione, that.dataGestione) &&
                esitoSegnalazione == that.esitoSegnalazione;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fkFilmSegnalato, fkUtenteSegnalatore, fkAmministratoreCheGestisce, messaggioSegnalazione, dataSegnalazione, dataGestione, id, notificaVisibilePerUtente, esitoSegnalazione);
    }
}

