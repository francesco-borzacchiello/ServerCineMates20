package it.unina.ingSw.cineMates20.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Utente", schema = "public", catalog = "CineMates20")
public class UtenteEntity {
    private String username;
    private String nome;
    private String cognome;
    private String email;

    @Enumerated(EnumType.STRING)
    private TipologiaUtente tipoUtente;

    public UtenteEntity() {}

    public UtenteEntity(@JsonProperty("username") String username,
                        @JsonProperty("nome") String nome,
                        @JsonProperty("cognome") String cognome,
                        @JsonProperty("email") String email,
                        @JsonProperty("tipoUtente") TipologiaUtente tipoUtente) {
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
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

        if (!Objects.equals(username, that.username)) return false;
        if (!Objects.equals(nome, that.nome)) return false;
        if (!Objects.equals(cognome, that.cognome)) return false;
        return Objects.equals(tipoUtente, that.tipoUtente);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        result = 31 * result + (cognome != null ? cognome.hashCode() : 0);
        result = 31 * result + (tipoUtente != null ? tipoUtente.hashCode() : 0);
        return result;
    }
}
