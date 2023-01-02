import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryDB {

    private String jdbcURL = "jdbc:mysql://localhost:3306/booklibrary?allowPublicKeyRetrieval=true&useSSL=false";
    private String jdbcUsername = "dbmanager";
    private String jdbcPassword = "manager7349";

    // does it need to be static?
    private static final String SELECT_ALL_STAFF = "select * from staff";
    private static final String INSERT_USER = "INSERT INTO staff" + " (username, full_name, password) VALUES " + " (?,?,?);";

    private static final String SELECT_BOOK_BY_TITLE = "SELECT * FROM books WHERE title LIKE ?";
    private static final String SELECT_BOOK_BY_AUTHOR = "SELECT * FROM books WHERE author LIKE ?";
    private static final String SELECT_BOOK_BY_ISBN = "SELECT * FROM books WHERE isbn LIKE ?";
    private static final String SELECT_BOOK_ID = "SELECT book_id FROM books WHERE title = ? AND author = ? AND isbn = ? AND quantity = ?";
    private static final String INSERT_BOOK = "INSERT INTO books" + " (title, author, isbn, quantity) VALUES " + " (?,?,?,?);";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, isbn = ?, quantity = ? WHERE title = ? AND author = ? AND isbn = ? AND quantity = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE title = ? AND author = ? AND isbn = ? AND quantity = ?";

    private static final String INSERT_MEMBER = "INSERT INTO members" + " (name, surname, phone, email, address, postcode) VALUES " + "(?,?,?,?,?,?);";
    private static final String SELECT_MEMBER_BY_NAME = "SELECT * FROM members WHERE name LIKE ?";
    private static final String SELECT_MEMBER_BY_SURNAME = "SELECT * FROM members WHERE surname LIKE ?";
    private static final String SELECT_MEMBER_BY_PHONENO = "SELECT * FROM members WHERE phone LIKE ?";
    private static final String SELECT_MEMBER_BY_EMAIL = "SELECT * FROM members WHERE email LIKE ?";
    private static final String SELECT_MEMBER_BY_ADDRESS = "SELECT * FROM members WHERE address LIKE ?";
    private static final String SELECT_MEMBER_BY_POSTCODE = "SELECT * FROM members WHERE postcode LIKE ?";
    private static final String UPDATE_MEMBER = "UPDATE members SET name = ?, surname = ?, phone = ?, email = ?, address = ?, postcode = ? WHERE name = ? AND surname = ? AND phone = ? AND email = ? AND address = ? AND postcode = ?";
    private static final String DELETE_MEMBER = "DELETE FROM members WHERE name = ? AND surname = ? AND phone = ? AND email = ? AND address = ? AND postcode = ?";
    private static final String SELECT_MEMBER_NAME_AND_SURNAME_BY_MEMBER_ID = "SELECT name, surname FROM members WHERE member_id = ?";

    private static final String INSERT_BORROWER = "INSERT INTO borrowed_books" + " (book_id, member_id, return_date) VALUES " + "(?,?,?);";
    private static final String SELECT_BORROWERS_BY_BOOK = "SELECT * FROM borrowed_books WHERE book_id = ?";
    private static final String UPDATE_BORROWER = "UPDATE borrowed_books SET member_id = ?, return_date = ? WHERE book_id = ? AND member_id = ? AND return_date = ?";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    public boolean noStaff() throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STAFF)) {
            ResultSet rs = preparedStatement.executeQuery();
            boolean isEmptyResultSet = !rs.next();
            rs.close();
            preparedStatement.close();
            connection.close();
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
        preparedStatement.close();
        connection.close();
    }

    public void createBook(Book newBook) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK);
        preparedStatement.setString(1, newBook.getTitle());
        preparedStatement.setString(2, newBook.getAuthor());
        preparedStatement.setInt(3, newBook.getIsbn());
        preparedStatement.setInt(4, newBook.getQuantity());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public List<Book> selectBooksByTitle(String title) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_TITLE);
        preparedStatement.setString(1, "%"+title+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Book> books = getBookList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return books;
    }

    public List<Book> selectBooksByAuthor (String author) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_AUTHOR);
        preparedStatement.setString(1, "%"+author+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Book> books = getBookList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return books;
    }

    public List<Book> selectBooksByISBN (String isbn) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_ISBN);
        preparedStatement.setString(1, "%"+isbn+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Book> books = getBookList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return books;
    }

    public void updateBook(String newTitle, String newAuthor, String newISBN, String newQuantity,
                           String oldTitle, String oldAuthor, String oldISBN, String oldQuantity) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK);
        preparedStatement.setString(1, newTitle);
        preparedStatement.setString(2, newAuthor);
        preparedStatement.setString(3, newISBN);
        preparedStatement.setString(4, newQuantity);
        preparedStatement.setString(5, oldTitle);
        preparedStatement.setString(6, oldAuthor);
        preparedStatement.setString(7, oldISBN);
        preparedStatement.setString(8, oldQuantity);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public void deleteBook(String title, String author, String isbn, String quantity) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.setString(3, isbn);
        preparedStatement.setString(4, quantity);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public void createMember(Member newMember) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MEMBER);
        preparedStatement.setString(1, newMember.getName());
        preparedStatement.setString(2, newMember.getSurname());
        preparedStatement.setString(3, newMember.getPhoneNo());
        preparedStatement.setString(4, newMember.getEmail());
        preparedStatement.setString(5, newMember.getAddress());
        preparedStatement.setString(6, newMember.getPostcode());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public List<Member> selectMembersByName(String name) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_NAME);
        preparedStatement.setString(1, "%"+name+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Member> members = getMemberList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return members;
    }

    public List<Member> selectMembersBySurname(String surname) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_SURNAME);
        preparedStatement.setString(1, "%"+surname+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Member> members = getMemberList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return members;
    }

    public List<Member> selectMembersByPhoneNo(String phoneNo) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_PHONENO);
        preparedStatement.setString(1, "%"+phoneNo+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Member> members = getMemberList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return members;
    }

    public List<Member> selectMembersByEmail(String email) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_EMAIL);
        preparedStatement.setString(1, "%"+email+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Member> members = getMemberList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return members;
    }

    public List<Member> selectMembersByAddress(String address) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_ADDRESS);
        preparedStatement.setString(1, "%"+address+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Member> members = getMemberList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return members;
    }

    public List<Member> selectMembersByPostcode(String postcode) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_POSTCODE);
        preparedStatement.setString(1, "%"+postcode+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<Member> members = getMemberList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return members;
    }

    public void updateMember(String newName, String newSurname, String newPhoneNo, String newEmail, String newAddress, String newPostcode, String oldName, String oldSurname, String oldPhoneNo, String oldEmail, String oldAddress, String oldPostcode) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MEMBER);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, newSurname);
        preparedStatement.setString(3, newPhoneNo);
        preparedStatement.setString(4, newEmail);
        preparedStatement.setString(5, newAddress);
        preparedStatement.setString(6, newPostcode);
        preparedStatement.setString(7, oldName);
        preparedStatement.setString(8, oldSurname);
        preparedStatement.setString(9, oldPhoneNo);
        preparedStatement.setString(10, oldEmail);
        preparedStatement.setString(11, oldAddress);
        preparedStatement.setString(12, oldPostcode);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public void deleteMember(String name, String surname, String phoneNo, String email, String address, String postcode) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MEMBER);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, surname);
        preparedStatement.setString(3, phoneNo);
        preparedStatement.setString(4, email);
        preparedStatement.setString(5, address);
        preparedStatement.setString(6, postcode);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public void createBorrower(Borrower newBorrower) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BORROWER);
        preparedStatement.setInt(1, newBorrower.getBookID());
        preparedStatement.setInt(2, newBorrower.getMemberID());
        preparedStatement.setString(3, newBorrower.getReturnDate());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    public int getBookID(String title, String author, String isbn, String quantity) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_ID);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.setString(3, isbn);
        preparedStatement.setString(4, quantity);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        int bookID = rs.getInt("book_id");
        rs.close();
        preparedStatement.close();
        connection.close();
        return bookID;
    }

    public List<Borrower> selectBorrowers(int bookID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BORROWERS_BY_BOOK);
        preparedStatement.setInt(1, bookID);
        ResultSet rs = preparedStatement.executeQuery();
        List<Borrower> borrowers = getBorrowerList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return borrowers;
    }

    public String selectMemberNameSurnameByMemberID(int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_NAME_AND_SURNAME_BY_MEMBER_ID);
        preparedStatement.setInt(1, memberID);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        String name = rs.getString("name");
        String surname = rs.getString("surname");
        rs.close();
        preparedStatement.close();
        connection.close();
        return name+" "+surname;
    }

    public void updateBorrower(int newMemberID, String newReturnDate, int bookID, int oldMemberID, String oldReturnDate) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BORROWER);
        preparedStatement.setInt(1, newMemberID);
        preparedStatement.setString(2, newReturnDate);
        preparedStatement.setInt(3, bookID);
        preparedStatement.setInt(4, oldMemberID);
        preparedStatement.setString(5, oldReturnDate);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
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

    private static List<Member> getMemberList(ResultSet rs) throws SQLException {
        List<Member> members = new ArrayList<>();
        while (rs.next()) {
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String phoneNo = rs.getString("phone");
            String email = rs.getString("email");
            String address = rs.getString("address");
            String postcode = rs.getString("postcode");
            members.add(new Member(name, surname, phoneNo, email, address, postcode));
        }
        return members;
    }

    private static List<Borrower> getBorrowerList(ResultSet rs) throws SQLException {
        List<Borrower> borrowers = new ArrayList<>();
        while (rs.next()) {
            int bookID = rs.getInt("book_id");
            int memberID = rs.getInt("member_id");
            String returnDate = rs.getString("return_date");
            borrowers.add(new Borrower(bookID, memberID, returnDate));
        }
        return borrowers;
    }

}
