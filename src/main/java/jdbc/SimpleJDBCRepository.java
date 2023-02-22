package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static constants.Constants.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final Logger logger = Logger.getLogger(SimpleJDBCRepository.class.getName());

    private static final String CREATE_USER_SQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM myusers";


    public Long createUser(User user) {
        Long result = null;
        try (Connection connection1 = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection1.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                result = rs.getLong(1);
            }
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return result;
    }

    public User findUserById(Long userId) {
        User user = null;
        ResultSet rs = null;
        try (Connection connection1 = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection1.prepareStatement(FIND_USER_BY_ID_SQL)) {
            ps.setLong(1, userId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No such users");
            }
            Long id = Long.parseLong(rs.getString(ID));
            String firstName = rs.getString(FIRST_NAME);
            String lastName = rs.getString(LAST_NAME);
            int age = rs.getInt(AGE);
            user = new User(id, firstName, lastName, age);
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        ResultSet rs = null;
        try (Connection connection1 = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection1.prepareStatement(FIND_USER_BY_NAME_SQL)) {
            ps.setString(1, userName);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("No such users");
            }
            Long id = Long.parseLong(rs.getString(ID));
            String firstName = rs.getString(FIRST_NAME);
            String lastName = rs.getString(LAST_NAME);
            int age = rs.getInt(AGE);
            user = new User(id, firstName, lastName, age);
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = null;
        ResultSet rs = null;
        try (Connection connection1 = CustomDataSource.getInstance().getConnection();
             Statement st = connection1.createStatement()) {
            rs = st.executeQuery(FIND_ALL_USER_SQL);
            users = new ArrayList<>();
            while (rs.next()) {
                Long id = Long.parseLong(rs.getString(ID));
                String firstName = rs.getString(FIRST_NAME);
                String lastName = rs.getString(LAST_NAME);
                int age = rs.getInt(AGE);
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection1 = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection1.prepareStatement(UPDATE_USER_SQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No such user exists");
            }
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
        return user;
    }

    public void deleteUser(Long userId) {
        try (Connection connection1 = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection1.prepareStatement(DELETE_USER);) {
            ps.setLong(1, userId);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("No such user exists");
            }
        } catch (SQLException throwables) {
            throwRuntimeException(throwables);
        }
    }

    public void throwRuntimeException(Exception e) {
        String message = String.format("%s: %s", e.getClass().getName(), e.getMessage());
        if (e.getCause() != null) {
            message += String.format("/nCause: %s: %s", e.getCause().getClass().getName(), e.getCause().getMessage());
        }
        throw new RuntimeException(message);
    }
}
