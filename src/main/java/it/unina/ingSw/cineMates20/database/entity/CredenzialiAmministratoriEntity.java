package it.unina.ingSw.cineMates20.database.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "CredenzialiAmministratori", schema = "public", catalog = "CineMates20")
public class CredenzialiAmministratoriEntity {
    private String email;
    private String password;
    private long id;

    public CredenzialiAmministratoriEntity() {}

    public CredenzialiAmministratoriEntity(@JsonProperty("email") String email,
                        @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
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

    @Id
    @Column(name = "id")
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

        CredenzialiAmministratoriEntity that = (CredenzialiAmministratoriEntity) o;

        if (id != that.id) return false;
        if (!Objects.equals(email, that.email)) return false;
        return Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }
}
