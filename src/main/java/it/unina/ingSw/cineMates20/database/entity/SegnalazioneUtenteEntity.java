package it.unina.ingSw.cineMates20.database.entity;

import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SegnalazioneUtente", schema = "public", catalog = "CineMates20")
public class SegnalazioneUtenteEntity {
    private String fkUtenteSegnalato;
    private String messaggioSegnalazione;
    private Timestamp dataSegnalazione;
    private Timestamp dataGestione;
    private long id;
    
    @Enumerated(EnumType.STRING)
    private TipoSegnalazione esitoSegnalazione;

    @Basic
    @Column(name = "FK_UtenteSegnalato")
    public String getFkUtenteSegnalato() {
        return fkUtenteSegnalato;
    }

    public void setFkUtenteSegnalato(String fkUtenteSegnalato) {
        this.fkUtenteSegnalato = fkUtenteSegnalato;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SegnalazioneUtenteEntity that = (SegnalazioneUtenteEntity) o;

        if (id != that.id) return false;
        if (fkUtenteSegnalato != null ? !fkUtenteSegnalato.equals(that.fkUtenteSegnalato) : that.fkUtenteSegnalato != null)
            return false;
        if (esitoSegnalazione != null ? !esitoSegnalazione.equals(that.esitoSegnalazione) : that.esitoSegnalazione != null)
            return false;
        if (messaggioSegnalazione != null ? !messaggioSegnalazione.equals(that.messaggioSegnalazione) : that.messaggioSegnalazione != null)
            return false;
        if (dataSegnalazione != null ? !dataSegnalazione.equals(that.dataSegnalazione) : that.dataSegnalazione != null)
            return false;
        if (dataGestione != null ? !dataGestione.equals(that.dataGestione) : that.dataGestione != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fkUtenteSegnalato != null ? fkUtenteSegnalato.hashCode() : 0;
        result = 31 * result + (esitoSegnalazione != null ? esitoSegnalazione.hashCode() : 0);
        result = 31 * result + (messaggioSegnalazione != null ? messaggioSegnalazione.hashCode() : 0);
        result = 31 * result + (dataSegnalazione != null ? dataSegnalazione.hashCode() : 0);
        result = 31 * result + (dataGestione != null ? dataGestione.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
