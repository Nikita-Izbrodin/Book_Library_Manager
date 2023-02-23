package DataAccess;

import Entities.Book;
import Entities.Borrower;
import Entities.Member;
import Entities.User;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for connecting to and interacting with a MySQL database.
 * Interacts with the database by executing SQL statements.
 */
public class MySqlLibraryDatabase implements LibraryDatabase {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/booklibrary";
    private static final String JDBC_USERNAME = "dbmanager";
    private static final String JDBC_PASSWORD = "manager7349";

    // book related SQL
    private static final String INSERT_BOOK = "INSERT INTO books (title, author, isbn, quantity) VALUES (?,?,?,?)";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, isbn = ?, quantity = ? " +
                                              "WHERE book_id = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE book_id = ?";
    private static final String SELECT_BOOK_BY_TITLE = "SELECT * FROM books WHERE title LIKE ?";
    private static final String SELECT_BOOK_BY_AUTHOR = "SELECT * FROM books WHERE author LIKE ?";
    private static final String SELECT_BOOK_BY_ISBN = "SELECT * FROM books WHERE isbn LIKE ?";
    private static final String SELECT_BOOK_ID = "SELECT book_id FROM books " +
                                                 "WHERE title = BINARY ? AND author = BINARY ? AND isbn = BINARY ?";
    private static final String COUNT_BOOKS = "SELECT SUM(quantity) FROM books";

    // member related SQL
    private static final String INSERT_MEMBER = "INSERT INTO members " +
                                                "(member_id, name, surname, phone, email, address, postcode) " +
                                                "VALUES (?,?,?,?,?,?,?)";
    private static final String UPDATE_MEMBER = "UPDATE members " +
                                                "SET member_id = ?, name = ?, surname = ?, " +
                                                "phone = ?, email = ?, address = ?, postcode = ? " +
                                                "WHERE member_id = ?";
    private static final String DELETE_MEMBER = "DELETE FROM members WHERE member_id = ?";
    private static final String SELECT_ALL_MEMBERS = "SELECT * FROM members";
    private static final String SELECT_MEMBER_BY_ID = "SELECT * FROM members WHERE member_id = ?";
    private static final String SELECT_MEMBER_BY_NAME = "SELECT * FROM members WHERE name LIKE ?";
    private static final String SELECT_MEMBER_BY_SURNAME = "SELECT * FROM members WHERE surname LIKE ?";
    private static final String SELECT_MEMBER_NAME_AND_SURNAME_BY_MEMBER_ID = "SELECT name, surname FROM members " +
                                                                              "WHERE member_id = ?";
    private static final String SELECT_MEMBER_BY_PHONENO = "SELECT * FROM members WHERE phone LIKE ?";
    private static final String SELECT_MEMBER_BY_EMAIL = "SELECT * FROM members WHERE email LIKE ?";
    private static final String SELECT_MEMBER_BY_ADDRESS = "SELECT * FROM members WHERE address LIKE ?";
    private static final String SELECT_MEMBER_BY_POSTCODE = "SELECT * FROM members WHERE postcode LIKE ?";
    private static final String COUNT_MEMBERS = "SELECT COUNT(*) FROM members";

    // borrowers
    private static final String INSERT_BORROWER = "INSERT INTO borrowers (book_id, member_id, return_date) " +
                                                  "VALUES (?,?,?)";
    private static final String UPDATE_BORROWER = "UPDATE borrowers SET member_id = ?, return_date = ? " +
                                                  "WHERE book_id = ? AND member_id = ?";
    private static final String DELETE_BORROWER = "DELETE FROM borrowers WHERE book_id = ? AND member_id = ?";
    private static final String SELECT_BORROWERS_BY_MEMBER_ID = "SELECT * FROM borrowers WHERE member_id = ?";
    private static final String SELECT_BORROWERS_BY_BOOK_ID =
            "SELECT book_id, borrowers.member_id, return_date, members.name, members.surname " +
            "FROM borrowers INNER JOIN members ON borrowers.member_id = members.member_id WHERE book_id = ?";
    private static final String SELECT_BORROWERS_BY_BOOK_ID_AND_MEMBER_ID = "SELECT * FROM borrowers " +
                                                                            "WHERE book_id = ? AND member_id = ?";
    private static final String COUNT_BORROWERS = "SELECT COUNT(*) FROM borrowers";

    // user related SQL
    private static final String INSERT_USER = "INSERT INTO users" +
                                              " (username, full_name, password) VALUES " + " (?,?,?)";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, full_name = ?, password = ? " +
                                              "WHERE username = BINARY ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE username = BINARY ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM users WHERE username LIKE ?";
    private static final String SELECT_USER_BY_USERNAME_AND_PASSWORD = "SELECT * FROM users " +
                                                                       "WHERE username = BINARY ? " +
                                                                       "AND password = BINARY ?";
    private static final String SELECT_USER_BY_FULLNAME = "SELECT * FROM users WHERE full_name LIKE ?";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USERNAME, JDBC_PASSWORD);
    }

    // methods are in the following order: books, members, borrowers, users, list getters

    //
    // book methods
    //

    @Override
    public void createBook(Book book) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK);
        preparedStatement.setString(1, book.title());
        preparedStatement.setString(2, book.author());
        preparedStatement.setString(3, book.isbn());
        preparedStatement.setInt(4, book.quantity());
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the books table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void updateBook(Book book, int book_id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK);
        preparedStatement.setString(1, book.title());
        preparedStatement.setString(2, book.author());
        preparedStatement.setString(3, book.isbn());
        preparedStatement.setInt(4, book.quantity());
        preparedStatement.setInt(5, book_id);
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the books table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void deleteBook(int book_id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK);
        preparedStatement.setInt(1, book_id);
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the books table.";
        closeConnection(connection, preparedStatement);
    }



    @Override
    public List<Book> selectBooksByTitle(String title) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_TITLE);
        preparedStatement.setString(1, "%"+title+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Book> books = getBookList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return books;
    }

    @Override
    public List<Book> selectBooksByAuthor(String author) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_AUTHOR);
        preparedStatement.setString(1, "%"+author+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Book> books = getBookList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return books;
    }

    @Override
    public List<Book> selectBooksByISBN(String isbn) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_ISBN);
        preparedStatement.setString(1, "%"+isbn+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Book> books = getBookList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return books;
    }

    @Override
    public int getBookID(String title, String author, String isbn) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_ID);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.setString(3, isbn);
        ResultSet resultSet = preparedStatement.executeQuery();
        int bookID = -1;
        if (resultSet.next()) {
            bookID = resultSet.getInt("book_id");
        }
        closeConnection(connection, preparedStatement, resultSet);
        return bookID;
    }

    @Override
    public boolean doesBookExist(String title, String author, String isbn) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_ID);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.setString(3, isbn);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean bookExists = resultSet.next();
        closeConnection(connection, preparedStatement, resultSet);
        return bookExists;
    }

    @Override
    public int countBooks() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BOOKS);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int totalBooks = resultSet.getInt(1);
        closeConnection(connection, preparedStatement, resultSet);
        return totalBooks;
    }

    //
    // member methods
    //

    @Override
    public void createMember(Member member) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MEMBER);
        preparedStatement.setInt(1, member.id());
        preparedStatement.setString(2, member.name());
        preparedStatement.setString(3, member.surname());
        preparedStatement.setString(4, member.phoneNo());
        preparedStatement.setString(5, member.email());
        preparedStatement.setString(6, member.address());
        preparedStatement.setString(7, member.postcode());
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the members table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void updateMember(Member member, int oldID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MEMBER);
        preparedStatement.setInt(1, member.id());
        preparedStatement.setString(2, member.name());
        preparedStatement.setString(3, member.surname());
        preparedStatement.setString(4, member.phoneNo());
        preparedStatement.setString(5, member.email());
        preparedStatement.setString(6, member.address());
        preparedStatement.setString(7, member.postcode());
        preparedStatement.setInt(8, oldID);
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the members table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void deleteMember(int id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MEMBER);
        preparedStatement.setInt(1, id);
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the members table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public boolean noMembers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_MEMBERS);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean noMembers = !resultSet.next();
        closeConnection(connection, preparedStatement, resultSet);
        return noMembers;
    }

    @Override
    public boolean isMemberIDUsed(int memberID) throws  SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_ID);
        preparedStatement.setInt(1, memberID);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isMemberIDUsed = resultSet.next();
        closeConnection(connection, preparedStatement, resultSet);
        return isMemberIDUsed;
    }

    @Override
    public List<Member> selectMembersByName(String name) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_NAME);
        preparedStatement.setString(1, "%"+name+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Member> members = getMemberList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return members;
    }

    @Override
    public List<Member> selectMembersBySurname(String surname) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_SURNAME);
        preparedStatement.setString(1, "%"+surname+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Member> members = getMemberList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return members;
    }

    @Override
    public String selectMemberNameSurnameByMemberID(int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_NAME_AND_SURNAME_BY_MEMBER_ID);
        preparedStatement.setInt(1, memberID);
        ResultSet resultSet = preparedStatement.executeQuery();
        String name = null;
        String surname = null;
        if (resultSet.next()) {
            name = resultSet.getString("name");
            surname = resultSet.getString("surname");
        }
        closeConnection(connection, preparedStatement, resultSet);
        return name+" "+surname;
    }

    @Override
    public List<Member> selectMembersByPhoneNo(String phoneNo) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_PHONENO);
        preparedStatement.setString(1, "%"+phoneNo+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Member> members = getMemberList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return members;
    }

    @Override
    public List<Member> selectMembersByEmail(String email) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_EMAIL);
        preparedStatement.setString(1, "%"+email+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Member> members = getMemberList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return members;
    }

    @Override
    public List<Member> selectMembersByAddress(String address) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_ADDRESS);
        preparedStatement.setString(1, "%"+address+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Member> members = getMemberList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return members;
    }

    @Override
    public List<Member> selectMembersByPostcode(String postcode) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_BY_POSTCODE);
        preparedStatement.setString(1, "%"+postcode+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Member> members = getMemberList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return members;
    }

    @Override
    public int countMembers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(COUNT_MEMBERS);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int totalMembers = resultSet.getInt(1);
        closeConnection(connection, preparedStatement, resultSet);
        return totalMembers;
    }

    //
    // borrower methods
    //

    @Override
    public void createBorrower(Borrower newBorrower) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BORROWER);
        preparedStatement.setInt(1, newBorrower.bookID());
        preparedStatement.setInt(2, newBorrower.memberID());
        preparedStatement.setDate(3, Date.valueOf(newBorrower.returnDate()));
        assert preparedStatement.executeUpdate() == 1 :
                "A single row is expected to be updated in the borrowers table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void updateBorrower(
            int newMemberID,
            LocalDate newReturnDate,
            int bookID,
            int oldMemberID
    ) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BORROWER);
        preparedStatement.setInt(1, newMemberID);
        preparedStatement.setDate(2, Date.valueOf(newReturnDate));
        preparedStatement.setInt(3, bookID);
        preparedStatement.setInt(4, oldMemberID);
        assert preparedStatement.executeUpdate() == 1 :
                "A single row is expected to be updated in the borrowers table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void deleteBorrower(int bookID, int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BORROWER);
        preparedStatement.setInt(1, bookID);
        preparedStatement.setInt(2, memberID);
        assert preparedStatement.executeUpdate() == 1 :
                "A single row is expected to be updated in the borrowers table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public boolean isBorrower(int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BORROWERS_BY_MEMBER_ID);
        preparedStatement.setInt(1, memberID);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isBorrower = resultSet.next();
        closeConnection(connection, preparedStatement, resultSet);
        return isBorrower;
    }

    @Override
    public List<Borrower> selectBorrowersByBookID(int bookID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BORROWERS_BY_BOOK_ID);
        preparedStatement.setInt(1, bookID);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Borrower> borrowers = new ArrayList<>();
        while (resultSet.next()) {
            int memberID = resultSet.getInt("member_id");
            LocalDate returnDate = resultSet.getDate("return_date").toLocalDate();
            String fullName = resultSet.getString("name") + " " + resultSet.getString("surname");
            borrowers.add(new Borrower(bookID, memberID, fullName, returnDate));
        }
        closeConnection(connection, preparedStatement, resultSet);
        return borrowers;
    }

    @Override
    public boolean isBookBorrowedByMember(int bookID, int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BORROWERS_BY_BOOK_ID_AND_MEMBER_ID);
        preparedStatement.setInt(1, bookID);
        preparedStatement.setInt(2, memberID);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean isBorrowed = resultSet.next();
        closeConnection(connection, preparedStatement, resultSet);
        return isBorrowed;
    }

    @Override
    public int countBorrowers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BORROWERS);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int numOfBooksBorrowed = resultSet.getInt(1);
        closeConnection(connection, preparedStatement, resultSet);
        return numOfBooksBorrowed;
    }

    //
    // user methods
    //

    @Override
    public void createUser(User user) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER);
        preparedStatement.setString(1, user.username());
        preparedStatement.setString(2, user.fullName());
        preparedStatement.setString(3, user.password());
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the users table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void updateUser(
            String newUsername,
            String newFullName,
            String newPassword,
            String oldUsername
    ) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);
        preparedStatement.setString(1, newUsername);
        preparedStatement.setString(2, newFullName);
        preparedStatement.setString(3, newPassword);
        preparedStatement.setString(4, oldUsername);
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the users table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public void deleteUser(String username) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER);
        preparedStatement.setString(1, username);
        assert preparedStatement.executeUpdate() == 1 : "A single row is expected to be updated in the users table.";
        closeConnection(connection, preparedStatement);
    }

    @Override
    public boolean noUsers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
        ResultSet resultSet = preparedStatement.executeQuery();
        boolean noUsers = !resultSet.next();
        closeConnection(connection, preparedStatement, resultSet);
        return noUsers;
    }

    @Override
    public List<User> selectUsersByUsername(String username) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME);
        preparedStatement.setString(1, "%"+username+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<User> users =  getUserList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return users;
    }

    @Override
    public boolean isValidUser(String username, String password) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME_AND_PASSWORD);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<User> users =  getUserList(resultSet);
        boolean valid = !users.isEmpty();
        closeConnection(connection, preparedStatement, resultSet);
        return valid;
    }


    @Override
    public List<User> selectUsersByFullName(String fullName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_FULLNAME);
        preparedStatement.setString(1, "%"+fullName+"%");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<User> users =  getUserList(resultSet);
        closeConnection(connection, preparedStatement, resultSet);
        return users;
    }

    //
    // list getters
    //

    private static List<Book> getBookList(ResultSet resultSet) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (resultSet.next()) {
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            String isbn = resultSet.getString("isbn");
            int quantity = resultSet.getInt("quantity");
            books.add(new Book(title, author, isbn, quantity));
        }
        return books;
    }

    private static List<Member> getMemberList(ResultSet resultSet) throws SQLException {
        List<Member> members = new ArrayList<>();
        while (resultSet.next()) {
            int id = resultSet.getInt("member_id");
            String name = resultSet.getString("name");
            String surname = resultSet.getString("surname");
            String phoneNo = resultSet.getString("phone");
            String email = resultSet.getString("email");
            String address = resultSet.getString("address");
            String postcode = resultSet.getString("postcode");
            members.add(new Member(id, name, surname, phoneNo, email, address, postcode));
        }
        return members;
    }

    private static List<User> getUserList(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            String username = resultSet.getString("username");
            String fullName = resultSet.getString("full_name");
            String password = resultSet.getString("password");
            users.add(new User(username, fullName, password));
        }
        return users;
    }

    //
    // connection closers
    //

    private static void closeConnection(
            Connection connection, PreparedStatement preparedStatement, ResultSet resultSet
    ) throws SQLException {
        resultSet.close();
        closeConnection(connection, preparedStatement);
    }

    private static void closeConnection(
            Connection connection, PreparedStatement preparedStatement
    ) throws SQLException {
        preparedStatement.close();
        connection.close();
    }
}