package it.unina.ingSw.cineMates20.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "ListaFilm", schema = "public", catalog = "CineMates20")
public class ListaFilmEntity {
    private long id;
    private String nome;
    private String fkPossessore;

    public ListaFilmEntity(){}

    public ListaFilmEntity(@JsonProperty("id") long id,
                           @JsonProperty("nome") String nome,
                           @JsonProperty("FK_Possessore") String fkPossessore) {
        this.id = id;
        this.nome = nome;
        this.fkPossessore = fkPossessore;
    }

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "FK_Possessore")
    public String getFkPossessore() {
        return fkPossessore;
    }

    public void setFkPossessore(String fkPossessore) {
        this.fkPossessore = fkPossessore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListaFilmEntity that = (ListaFilmEntity) o;

        if (id != that.id) return false;
        return Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (nome != null ? nome.hashCode() : 0);
        return result;
    }
}
