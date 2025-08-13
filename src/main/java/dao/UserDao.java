package dao;

import model.User;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserDao {

    private final JdbcTemplate jdbc;

    public UserDao(DataSource routingDataSource) {
        this.jdbc = new JdbcTemplate(routingDataSource);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return jdbc.queryForObject("SELECT * FROM users WHERE id=?", new Object[]{id}, userRowMapper);
    }

    @Transactional
    public void insertUser(User user) {
        jdbc.update("INSERT INTO users (name, email) VALUES (?, ?)", user.getName(), user.getEmail());
    }

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getLong("id"), rs.getString("name"), rs.getString("email"));
        }
    };
}