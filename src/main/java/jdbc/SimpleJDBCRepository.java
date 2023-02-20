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

    private static final String createUserSQL = "INSERT INTO myusers (id, firstname, lastname, age) VALUES (?, ?, ?, ?)";
//    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES ('Ivan', 'Fedosov', 25)";
    private static final String updateUserSQL = "UPDATE myusers SET age = 20 WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";


    public Long createUser(User user) throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(createUserSQL);
        ps.setLong(1, user.getId());
        ps.setString(2, user.getFirstName());
        ps.setString(3, user.getLastName());
        ps.setInt(4, user.getAge());
        ps.executeUpdate();


//        st.executeUpdate("SELECT id FROM myusers WHERE firstname = 'Ivan' AND lastname = 'Fedosov'");
//        ps = connection.prepareStatement("SELECT id FROM myusers WHERE firstname = ? AND lastname = ?");
//        ps.setString(1, user.getFirstName());
//        ps.setString(2, user.getLastName());
//        ResultSet rs = ps.executeQuery();
//        while (rs.next()){
//            return rs.getLong("id");
//        }
        return user.getId();
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

    public User updateUser(User user) throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps  = connection.prepareStatement(updateUserSQL);
        ps.setLong(1, user.getId());
        ps.executeUpdate();

        return findUserById(user.getId());
//        ps = connection.prepareStatement(findUserByIdSQL)
//
//        ResultSet rs = ps.executeQuery();
//        User newUser = new User();
//        while (rs.next()){
//            user.setId(rs.getLong("id"));
//            user.setFirstName(rs.getString("firstname"));
//            user.setLastName(rs.getString("lastname"));
//            user.setAge(rs.getInt("age"));
//        }
//        return newUser;
    }

    public void deleteUser(Long userId) throws SQLException {
        connection = CustomDataSource.getInstance().getConnection();
        ps = connection.prepareStatement(deleteUser);
        ps.setLong(1, userId);
        ps.executeUpdate();
    }
}
