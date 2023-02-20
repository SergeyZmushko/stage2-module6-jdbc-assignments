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

    private static final String CREATE_USER_SQL = "INSERT INTO myusers (id, firstname, lastname, age) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE myusers SET age = 20 WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM myusers";


    public Long createUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(CREATE_USER_SQL);
            ps.setLong(1, user.getId());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, user.getAge());
            ps.executeUpdate();
            return user.getId();
        } catch (SQLException e) {
            logger.info("create user exception");
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                logger.info("close connection error");
            }
        }
        return -1L;
    }

    public User findUserById(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(FIND_USER_BY_ID_SQL);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new User(
                        rs.getLong(ID),
                        rs.getString(FIRST_NAME),
                        rs.getString(LAST_NAME),
                        rs.getInt(AGE));
            }
        } catch (SQLException e) {
            logger.info("Can't find user with this userId");
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                logger.info("Close connection error in findUserById");
            }
        }
        return null;
    }

    public User findUserByName(String userName) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(FIND_USER_BY_NAME_SQL);
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return new User(
                        rs.getLong(ID),
                        rs.getString(FIRST_NAME),
                        rs.getString(LAST_NAME),
                        rs.getInt(AGE));
            }
        } catch (SQLException e) {
            logger.info("Can't find user with this userName");
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                logger.info("Close connection error in findUserByName");
            }
        }
        return null;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            st = connection.createStatement();
            ResultSet resultSet = st.executeQuery(FIND_ALL_USER_SQL);
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getLong(ID),
                        resultSet.getString(FIRST_NAME),
                        resultSet.getString(LAST_NAME),
                        resultSet.getInt(AGE));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            logger.info("Can't find users DB is empty");
        } finally {
            try {
                st.close();
                connection.close();
            } catch (SQLException e) {
                logger.info("Close connection error in findAllUsers");
            }
        }
        return new ArrayList<>();
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(UPDATE_USER_SQL);
            ps.setLong(1, user.getId());
            ps.executeUpdate();

            return findUserById(user.getId());
        } catch (SQLException e) {
            logger.info("Can't find user in DB for update");
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                logger.info("Close connection error in updateUSer");
            }
        }
        return new User();
    }

    public void deleteUser(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(DELETE_USER);
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.info("Can't find user in DB for delete");
        } finally {
            try {
                ps.close();
                connection.close();
            } catch (SQLException e) {
                logger.info("Close connection error in deleteUser");
            }

        }
    }
}
