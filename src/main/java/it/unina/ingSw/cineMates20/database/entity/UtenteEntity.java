package it.unina.ingSw.cineMates20.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "Utente", schema = "public", catalog = "CineMates20")
public class UtenteEntity {
    private String username;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private Timestamp dataRegistrazione;

    @Enumerated(EnumType.STRING)
    private TipologiaUtente tipoUtente;

    public UtenteEntity() {}

    public UtenteEntity(@JsonProperty("username") String username, @JsonProperty("nome") String nome,
                        @JsonProperty("cognome") String cognome, @JsonProperty("email") String email,
                        @JsonProperty("password") String password, @JsonProperty("dataRegistrazione") Timestamp dataRegistrazione,
                        @JsonProperty("tipoUtente") TipologiaUtente tipoUtente) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.dataRegistrazione = dataRegistrazione;
        this.tipoUtente = tipoUtente;
    }

    @Id
    @Column(name = "username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "nome")
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Basic
    @Column(name = "cognome")
    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @Basic
    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column(name = "dataRegistrazione")
    public Timestamp getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(Timestamp dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    @Basic
    @Column(name = "tipoUtente")
    public TipologiaUtente getTipoUtente() {
        return tipoUtente;
    }

    public void setTipoUtente(TipologiaUtente tipoUtente) {
        this.tipoUtente = tipoUtente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtenteEntity that = (UtenteEntity) o;

        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (nome != null ? !nome.equals(that.nome) : that.nome != null) return false;
        if (cognome != null ? !cognome.equals(that.cognome) : that.cognome != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (dataRegistrazione != null ? !dataRegistrazione.equals(that.dataRegistrazione) : that.dataRegistrazione != null)
            return false;
        if (tipoUtente != null ? !tipoUtente.equals(that.tipoUtente) : that.tipoUtente != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (cognome != null ? cognome.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (dataRegistrazione != null ? dataRegistrazione.hashCode() : 0);
        result = 31 * result + (tipoUtente != null ? tipoUtente.hashCode() : 0);
        return result;
    }
}
