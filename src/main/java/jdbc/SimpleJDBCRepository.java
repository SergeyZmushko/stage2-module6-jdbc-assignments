package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

//    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES ('Ivan', 'Fedosov', 25)";
    private static final String updateUserSQL = "UPDATE myusers SET age = 25 WHERE id = 3";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";


    public Long createUser() throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
//        ps = connection.prepareStatement(createUserSQL);
//        ps.setString(1, firstName);
//        ps.setString(2, lastName);
//        ps.setInt(3, age);
//        ps.executeUpdate();
        st = connection.createStatement();
        st.executeUpdate(createUserSQL);

//        st.executeUpdate("SELECT id FROM myusers WHERE firstname = 'Ivan' AND lastname = 'Fedosov'");
//        ps = connection.prepareStatement("SELECT id FROM myusers WHERE firstname = ? AND lastname = ?");
//        ps.setString(1, firstName);
//        ps.setString(2, lastName);
        ResultSet rs = st.executeQuery("SELECT id FROM myusers WHERE firstname = 'Ivan' AND lastname = 'Fedosov'");
        while (rs.next()){
            return rs.getLong("id");
        }
        return -1L;
    }

    public User findUserById(Long userId) throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findUserByIdSQL);
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            return new User(
                    rs.getLong("id"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getInt("age"));
        }
        return null;
    }

    public User findUserByName(String userName) throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(findUserByNameSQL);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            return new User(
                    rs.getLong("id"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getInt("age"));
        }
        return null;
    }

    public List<User> findAllUser() throws SQLException {
        List<User> users = new ArrayList<>();
        connection = CustomDataSource.getInstance().getConnection();
        st = connection.createStatement();
        ResultSet resultSet = st.executeQuery(findAllUserSQL);
        while (resultSet.next()){
            User user = new User(
                    resultSet.getLong("id"),
                    resultSet.getString("firstname"),
                    resultSet.getString("lastname"),
                    resultSet.getInt("age"));
            users.add(user);
        }
        return users;
    }

    public User updateUser() throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        st  = connection.createStatement();
        st.executeUpdate(updateUserSQL);
        ResultSet rs = st.executeQuery("SELECT id, firstname, lastname, age FROM myusers");
        User user = new User();
        while (rs.next()){
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("firstname"));
            user.setLastName(rs.getString("lastname"));
            user.setAge(rs.getInt("age"));
        }
        return user;
    }

    public void deleteUser(Long userId) throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(deleteUser);
        ps.setLong(1, userId);
        ps.executeUpdate();
    }
}
