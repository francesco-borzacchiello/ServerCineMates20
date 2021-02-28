package it.unina.ingSw.cineMates20.database.dao;

import it.unina.ingSw.cineMates20.database.entity.ListaFilmEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository("postgresListaFilmTable")
public class ListaFilmDaoImplementation implements ListaFilmDao<ListaFilmEntity, Integer>{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ListaFilmDaoImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ListaFilmEntity getPreferitiByPossessore(String idPossessore) {
        return getByPossessore(idPossessore, "Preferiti");
    }

    @Override
    public ListaFilmEntity getDaVedereByPossessore(String idPossessore) {
        return getByPossessore(idPossessore, "Da vedere");
    }

    @Override
    public ListaFilmEntity getByPossessore(String idPossessore, String nomeLista) {
        if (idPossessore == null || nomeLista == null)
            return null;

        final String sql = "SELECT * FROM \"ListaFilm\" WHERE \"Email_Possessore\" = ? AND \"nome\" = ?;";

        try {
            return jdbcTemplate.queryForObject(sql, new String[] { idPossessore, nomeLista },
                                              (resultSet, i) -> resultSetToListaFilmEntity(resultSet));
        }catch(DataAccessException e){
            return null;
        }
    }

    private ListaFilmEntity resultSetToListaFilmEntity(ResultSet resultSet) throws  java.sql.SQLException{
        return new ListaFilmEntity(resultSet.getLong("id"),
                                   resultSet.getString("nome"),
                                   resultSet.getString("Email_Possessore"));
    }

    @Override
    public boolean addFilm(ListaFilmEntity listaFilm, long idFilm) {
        if(listaFilm == null) return false;

        try {
            return jdbcTemplate.update(getSqlCommandForAddFilm(),
                    listaFilm.getId(), idFilm) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForAddFilm() {
        return "INSERT INTO \"Lista_Contiene_Film\"" +
                "(\"FK_Lista_Contenitrice\", \"FK_Film\") " +
                "VALUES (?, ?);";
    }

    @Override
    public boolean removeFilm(ListaFilmEntity listaFilm, long idFilm) {
        if(listaFilm == null) return false;

        try {
            return jdbcTemplate.update(getSqlCommandForRemoveFilm(), listaFilm.getId(), idFilm) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean containsFilm(long idLista, long idFilm) {
        final String sql = "SELECT COUNT(*) FROM \"Lista_Contiene_Film\" WHERE \"FK_Lista_Contenitrice\" = ? AND \"FK_Film\" = ?;";

        try {
            Integer resultQuery = jdbcTemplate.queryForObject(sql, new Object[] { idLista, idFilm }, Integer.class);
            return resultQuery != null && resultQuery >= 1;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForRemoveFilm() {
        return "DELETE FROM \"Lista_Contiene_Film\" " +
                "WHERE \"FK_Lista_Contenitrice\" = ? AND \"FK_Film\" = ?;";
    }

    @Override
    public List<Long> getAllFilm(ListaFilmEntity listaFilm) {
        if(listaFilm == null) return null;

        try {
            return jdbcTemplate.query(getSqlCommandForGetAllFilm(), new Long[] { listaFilm.getId() },
                    (rs, rowNum) -> rs.getLong("FK_Film"));
        }catch(DataAccessException e){
            return null;
        }
    }

    private String getSqlCommandForGetAllFilm() {
        return "SELECT \"FK_Film\" FROM \"Lista_Contiene_Film\" " +
                "WHERE \"FK_Lista_Contenitrice\" = ?;";
    }

    @Override
    public ListaFilmEntity getById(Integer id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ListaFilmEntity> getAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean insert(ListaFilmEntity newListaFilm) {
        if (newListaFilm == null)
            return false;

        try {
            //Se non è stato inserito nessun record restituisco "false"
            return jdbcTemplate.update(getSqlCommandForInsert(),
                    newListaFilm.getNome(), newListaFilm.getEmailPossessore()) != 0;
        }catch(DataAccessException e){
            return false;
        }
    }

    private String getSqlCommandForInsert() {
        return "INSERT INTO public.\"ListaFilm\"(" +
                "id, nome, \"Email_Possessore\") " +
                "VALUES (nextval('pk_lista_film'), ?, ?);";
    }

    @Override
    public boolean update(ListaFilmEntity listaFilmEntity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(ListaFilmEntity listaFilmEntity) {
        throw new UnsupportedOperationException();
    }
}
