package com.tms.repository;

import com.tms.config.DatabaseService;
import com.tms.config.SQLQuery;
import com.tms.model.Role;
import com.tms.model.Security;
import com.tms.model.User;
import com.tms.model.dto.RegistrationRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

import static com.tms.config.SQLQuery.CREATE_SECURITY_WEB;
import static com.tms.config.SQLQuery.DELETE_SECURITY;
import static com.tms.config.SQLQuery.GET_SECURITY_BY_ID;
import static com.tms.config.SQLQuery.UPDATE_SECURITY;

@Repository
public class SecurityRepository {

    private final DatabaseService databaseService;

    @Autowired
    public SecurityRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    public Optional<Security> getSecurityById(Long id) {
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_SECURITY_BY_ID)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(parseSecurity(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean createSecurity(Security security) {
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_SECURITY_WEB, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, security.getLogin());
            statement.setString(2, security.getPassword());
            statement.setString(3, security.getRole().toString());
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.setLong(5, security.getUserId());
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    security.setId(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSecurity(Security security) {
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SECURITY)) {
            statement.setString(1, security.getLogin());
            statement.setString(2, security.getPassword());
            statement.setString(3, security.getRole().toString());
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.setLong(5, security.getUserId());
            statement.setLong(6, security.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSecurity(Long id) {
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SECURITY)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Security parseSecurity(ResultSet resultSet) throws SQLException {
        Security security = new Security();
        security.setId(resultSet.getLong("id"));
        security.setLogin(resultSet.getString("login"));
        security.setPassword(resultSet.getString("password"));
        security.setRole(Role.valueOf(resultSet.getString("role")));
        security.setCreated(resultSet.getTimestamp("created"));
        security.setUpdated(resultSet.getTimestamp("updated"));
        security.setUserId(resultSet.getLong("user_id"));
        return security;
    }

    public Optional<User> registration(RegistrationRequestDto requestDto) {
        Connection connection = databaseService.getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement createUserStatement = connection.prepareStatement(SQLQuery.CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            createUserStatement.setString(1, requestDto.getFirstname());
            createUserStatement.setString(2, requestDto.getSecondName());
            createUserStatement.setInt(3, requestDto.getAge());
            createUserStatement.setString(4, requestDto.getTelephoneNumber());
            createUserStatement.setString(5, requestDto.getEmail());
            createUserStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            createUserStatement.setString(7, requestDto.getSex());
            createUserStatement.setBoolean(8, false);
            createUserStatement.executeUpdate();
            ResultSet generatedKeys = createUserStatement.getGeneratedKeys();
            long userId = -1;
            if (generatedKeys.next()) {
                userId = generatedKeys.getLong(1);
            }
            PreparedStatement createSecurityStatement = connection.prepareStatement(SQLQuery.CREATE_SECURITY);
            createSecurityStatement.setString(1, requestDto.getLogin());
            createSecurityStatement.setString(2, requestDto.getPassword());
            createSecurityStatement.setString(3, Role.USER.toString());
            createSecurityStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            createSecurityStatement.setLong(5, userId);
            createSecurityStatement.executeUpdate();
            connection.commit();
            return Optional.of(new User(
                    userId,
                    requestDto.getFirstname(),
                    requestDto.getSecondName(),
                    requestDto.getAge(),
                    requestDto.getEmail(),
                    requestDto.getSex(),
                    requestDto.getTelephoneNumber(),
                    new Timestamp(System.currentTimeMillis()),
                    new Timestamp(System.currentTimeMillis()),
                    false, null
            ));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
        }
        return Optional.empty();
    }
}

