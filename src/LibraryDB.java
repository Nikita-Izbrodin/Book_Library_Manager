import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryDB {

    private String jdbcURL = "jdbc:mysql://localhost:3306/booklibrary?useSSL=false";
    private String jdbcUsername = "dbmanager";
    private String jdbcPassword = "manager7349";

    private static final String SELECT_ALL_STAFF = "select * from staff";

    private static final String INSERT_USER = "INSERT INTO staff" + " (username, full_name, password) VALUES " + " (?,?,?);";
    private static final String INSERT_BOOK = "INSERT INTO books" + " (title, author, isbn, quantity) VALUES " + " (?,?,?,?);";

    private static final String SELECT_ALL_BOOKS = "select * from books";
    private static final String SELECT_BOOK_BY_TITLE = "SELECT * FROM books WHERE title LIKE ?";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public boolean noStaff() throws SQLException {

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STAFF)) {
            ResultSet rs = preparedStatement.executeQuery();
            boolean isEmptyResultSet = !rs.next();
            return isEmptyResultSet;
        }

    }

    public void createUser(User user) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getFull_name());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.executeUpdate();
    }

    public void createBook(Book newBook) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK);
        preparedStatement.setString(1, newBook.getTitle());
        preparedStatement.setString(2, newBook.getAuthor());
        preparedStatement.setInt(3, newBook.getIsbn());
        preparedStatement.setInt(4, newBook.getQuantity());
        preparedStatement.executeUpdate();
    }

    public List<Book> selectAllBooks() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_BOOKS);
        ResultSet rs = preparedStatement.executeQuery();
        List<Book> books = getBookList(rs);
        return books;
    }

    public List<Book> selectBooksByTitle(String title) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_TITLE);
        preparedStatement.setString(1, "%"+title+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Book> books = getBookList(rs);
        return books;
    }

    private static List<Book> getBookList(ResultSet rs) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (rs.next()) {
            String title = rs.getString("title");
            String author = rs.getString("author");
            int isbn = rs.getInt("isbn");
            int quantity = rs.getInt("quantity");
            books.add(new Book(title, author, isbn, quantity));
        }
        return books;
    }

}
