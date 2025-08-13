# Dynamic Datasource Routing (JDBC Only)

A lightweight **library** for Spring (Boot 3.x, Java 17) that routes **READ** and **WRITE** calls to separate PostgreSQL endpoints using `@Transactional(readOnly=true)` for **JDBC/JdbcTemplate** code. Credentials are fetched transparently by the **AWS Secrets Manager JDBC driver** — _no password in app config_.

---

## How it works
- `@Transactional(readOnly=true)` → Aspect sets `DataSourceType.READ`.
- Other calls (default) → `DataSourceType.WRITE`.
- `RoutingDataSource` (extends `AbstractRoutingDataSource`) checks a ThreadLocal key (`DataSourceContextHolder`) and returns the correct target DataSource.
- Your DAOs use **JdbcTemplate** injected with the **routing** DataSource.

## Project layout
```
src/
  main/java/
    aspect/DataSourceRoutingAspect.java
    com/example/config/
      AppConfig.java
      DataSourceConfig.java
      RoutingDataSource.java
    dao/UserDao.java
    model/User.java
    routing/
      DataSourceContextHolder.java
      DataSourceType.java
    service/DbCheckService.java
  main/resources/
    application.properties
```

## Install / Build
```bash
mvn -q -DskipTests package
```
This produces a JAR suitable to add as a dependency in your apps.

## Configure
In your application (or while testing the lib), set:
```properties
spring.datasource.write.url=jdbc-secretsmanager:postgresql://<rds-proxy-write>:5432/<db>
spring.datasource.read.url=jdbc-secretsmanager:postgresql://<rds-proxy-read>:5432/<db>
spring.datasource.username=<aws_secret_name>
```

> The AWS Secrets Manager JDBC driver expects the **username** to be the **SecretId** and builds credentials at runtime.

## Use in your service
```java
@Service
public class UserService {
  private final UserDao userDao;
  public UserService(UserDao userDao){ this.userDao = userDao; }

  @Transactional(readOnly = true)   // routes to READ
  public User get(Long id) { return userDao.getUserById(id); }

  @Transactional                     // routes to WRITE
  public void create(String n, String e) { userDao.insertUser(new User(null, n, e)); }
}
```

## Notes
- Connection pooling: you can swap `SimpleDriverDataSource` with `HikariDataSource` and set `driverClassName=com.amazonaws.secretsmanager.sql.AWSSecretsManagerPostgreSQLDriver` and `jdbcUrl` to your `jdbc-secretsmanager:` URL.
- The library logs which path is chosen via the aspect; remove the `System.out.println` in production or replace with a logger.
