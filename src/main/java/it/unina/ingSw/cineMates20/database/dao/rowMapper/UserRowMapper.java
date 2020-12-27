package it.unina.ingSw.cineMates20.database.dao.rowMapper;

import it.unina.ingSw.cineMates20.database.entity.UtenteEntity;
import it.unina.ingSw.cineMates20.database.enums.TipologiaUtente;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

//Classe per mappare un utente estrapolato dal database con una query ad un oggetto di tipo UtenteEntity

public class UserRowMapper implements RowMapper<UtenteEntity> {

    @Override
    public UtenteEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        UtenteEntity utente = new UtenteEntity();
        utente.setUsername(rs.getString("username"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setPassword(rs.getString("password"));
        utente.setEmail(rs.getString("email"));
        utente.setDataRegistrazione(rs.getTimestamp("dataRegistrazione")); //.toLocalDateTime()
        utente.setTipoUtente(TipologiaUtente.valueOf(rs.getString("tipoUtente")));

        return utente;
    }

}