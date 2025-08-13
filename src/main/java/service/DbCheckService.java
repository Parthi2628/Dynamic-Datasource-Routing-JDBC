package service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbCheckService {

    private final JdbcTemplate jdbcTemplate;

    public DbCheckService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public String checkReadDbHost() {
        String ip = jdbcTemplate.queryForObject("SELECT inet_server_addr()", String.class);
        System.out.println(">>> Connected to READ DB Host IP: " + ip);
        return ip;
    }

    @Transactional
    public String checkWriteDbHost() {
        String ip = jdbcTemplate.queryForObject("SELECT inet_server_addr()", String.class);
        System.out.println(">>> Connected to WRITE DB Host IP: " + ip);
        return ip;
    }
}