package it.unina.ingSw.cineMates20.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unina.ingSw.cineMates20.database.enums.TipoSegnalazione;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "SegnalazioneUtente", schema = "public", catalog = "CineMates20")
public class SegnalazioneUtenteEntity {
    private String fkUtenteSegnalato;
    private String fkUtenteSegnalatore;
    private String fkAmministratoreCheGestisce;
    private String messaggioSegnalazione;
    private Timestamp dataSegnalazione;
    private Timestamp dataGestione;
    private long id;
    private boolean notificaVisibilePerUtente;

    @Enumerated(EnumType.STRING)
    private TipoSegnalazione esitoSegnalazione;

    public SegnalazioneUtenteEntity() {}


    public SegnalazioneUtenteEntity(@JsonProperty("FK_UtenteSegnalato") String fkUtenteSegnalato,
                                    @JsonProperty("FK_UtenteSegnalatore") String fkUtenteSegnalatore,
                                    @JsonProperty("MessaggioSegnalazione") String messaggioSegnalazione) {
        this.fkUtenteSegnalato = fkUtenteSegnalato;
        this.fkUtenteSegnalatore = fkUtenteSegnalatore;
        this.messaggioSegnalazione = messaggioSegnalazione;
        this.dataSegnalazione = Timestamp.from(Instant.now());
        esitoSegnalazione = TipoSegnalazione.Pendente;
    }

    /*
    {
        "FK_UtenteSegnalato":"hwnd875o@yasellerbot.xyz",
        "FK_UtenteSegnalatore":"carmineegr@gmail.com",
        "FK_AmministratoreCheGestisce":null,
        "EsitoSegnalazione":"pendente",
        "MessaggioSegnalazione":"Si finge qualcun'altro",
        "DataSegnalazione":"2021-01-31T11:39:43.511Z",
        "DataGestione":null,
        "Id":null,
        "notifica_visibile_per_utente": "false"
    }
    */

    @Basic
    @Column(name = "FK_UtenteSegnalato")
    public String getFkUtenteSegnalato() {
        return fkUtenteSegnalato;
    }


    public void setFkUtenteSegnalato(String fkUtenteSegnalato) {
        this.fkUtenteSegnalato = fkUtenteSegnalato;
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

        SegnalazioneUtenteEntity that = (SegnalazioneUtenteEntity) o;

        if (id != that.id) return false;
        if (!Objects.equals(fkUtenteSegnalato, that.fkUtenteSegnalato))
            return false;
        if (!Objects.equals(fkUtenteSegnalatore, that.fkUtenteSegnalatore))
            return false;
        if (!Objects.equals(fkAmministratoreCheGestisce, that.fkAmministratoreCheGestisce))
            return false;
        if (!Objects.equals(esitoSegnalazione, that.esitoSegnalazione))
            return false;
        if (!Objects.equals(messaggioSegnalazione, that.messaggioSegnalazione))
            return false;
        if (!Objects.equals(dataSegnalazione, that.dataSegnalazione))
            return false;
        if (notificaVisibilePerUtente != that.notificaVisibilePerUtente)
            return false;
        return Objects.equals(dataGestione, that.dataGestione);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fkUtenteSegnalato, fkUtenteSegnalatore, fkAmministratoreCheGestisce,
                            messaggioSegnalazione, dataSegnalazione, dataGestione, id,
                            notificaVisibilePerUtente, esitoSegnalazione);
    }
}


