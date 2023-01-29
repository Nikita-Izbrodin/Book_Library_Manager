import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlLibraryDatabase implements LibraryDatabase {

    private static final String jdbcURL = "jdbc:mysql://localhost:3306/booklibrary";
    private static final String jdbcUsername = "dbmanager";
    private static final String jdbcPassword = "manager7349";

    private static final String INSERT_BOOK = "INSERT INTO books" + " (title, author, isbn, quantity) VALUES " + " (?,?,?,?);";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, isbn = ?, quantity = ? WHERE book_id = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE book_id = ?";
    private static final String SELECT_BOOK_BY_TITLE = "SELECT * FROM books WHERE title LIKE ?";
    private static final String SELECT_BOOK_BY_AUTHOR = "SELECT * FROM books WHERE author LIKE ?";
    private static final String SELECT_BOOK_BY_ISBN = "SELECT * FROM books WHERE isbn LIKE ?";
    private static final String SELECT_BOOK_BY_TITLE_AND_AUTHOR_AND_ISBN = "SELECT * FROM books WHERE title = ? AND author = ? AND isbn = ?";
    private static final String SELECT_BOOK_ID = "SELECT book_id FROM books WHERE title = ? AND author = ? AND isbn = ? AND quantity = ?";
    private static final String COUNT_BOOKS = "SELECT SUM(quantity) FROM books";

    private static final String INSERT_MEMBER = "INSERT INTO members" + " (member_id, name, surname, phone, email, address, postcode) VALUES " + "(?,?,?,?,?,?,?);";
    private static final String UPDATE_MEMBER = "UPDATE members SET member_id = ?, name = ?, surname = ?, phone = ?, email = ?, address = ?, postcode = ? WHERE member_id = ?";
    private static final String DELETE_MEMBER = "DELETE FROM members WHERE member_id = ?";
    private static final String SELECT_ALL_MEMBERS = "SELECT * FROM members";
    private static final String SELECT_MEMBER_BY_NAME = "SELECT * FROM members WHERE name LIKE ?";
    private static final String SELECT_MEMBER_BY_SURNAME = "SELECT * FROM members WHERE surname LIKE ?";
    private static final String SELECT_MEMBER_BY_PHONENO = "SELECT * FROM members WHERE phone LIKE ?";
    private static final String SELECT_MEMBER_BY_EMAIL = "SELECT * FROM members WHERE email LIKE ?";
    private static final String SELECT_MEMBER_BY_ADDRESS = "SELECT * FROM members WHERE address LIKE ?";
    private static final String SELECT_MEMBER_BY_POSTCODE = "SELECT * FROM members WHERE postcode LIKE ?";
    private static final String SELECT_MEMBER_NAME_AND_SURNAME_BY_MEMBER_ID = "SELECT name, surname FROM members WHERE member_id = ?";
    private static final String COUNT_MEMBERS = "SELECT COUNT(*) FROM members";

    private static final String INSERT_BORROWER = "INSERT INTO borrowed_books" + " (book_id, member_id, return_date) VALUES " + "(?,?,?);";
    private static final String UPDATE_BORROWER = "UPDATE borrowed_books SET member_id = ?, return_date = ? WHERE book_id = ? AND member_id = ?";
    private static final String DELETE_BORROWER = "DELETE FROM borrowed_books WHERE book_id = ? AND member_id = ?";
    private static final String DELETE_BORROWER_BY_BOOK_ID = "DELETE FROM borrowed_books WHERE book_id = ?";
    private static final String DELETE_BORROWER_BY_MEMBER_ID = "DELETE FROM borrowed_books WHERE member_id = ?";
    private static final String SELECT_BORROWERS_BY_BOOK_ID = "SELECT book_id, borrowed_books.member_id, return_date, members.name, members.surname FROM borrowed_books INNER JOIN members on borrowed_books.member_id = members.member_id WHERE book_id = ?";
    private static final String SELECT_BORROWERS_BY_BOOK_ID_AND_MEMBER_ID = "SELECT * FROM borrowed_books WHERE book_id = ? AND member_id = ?";
    private static final String COUNT_BORROWERS = "SELECT COUNT(*) FROM borrowed_books";

    private static final String INSERT_USER = "INSERT INTO staff" + " (username, full_name, password) VALUES " + " (?,?,?);";
    private static final String UPDATE_USER = "UPDATE staff SET username = ?, full_name = ?, password = ? WHERE username = ?";
    private static final String DELETE_USER = "DELETE FROM staff WHERE username = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM staff";
    private static final String SELECT_USER_BY_USERNAME = "SELECT * FROM staff WHERE username LIKE ?";
    private static final String SELECT_USER_BY_USERNAME_AND_PASSWORD = "SELECT * FROM staff WHERE username = BINARY ? AND password = BINARY ?";
    private static final String SELECT_USER_BY_FULLNAME = "SELECT * FROM staff WHERE full_name LIKE ?";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
    }

    //
    // start of book commands
    //
    @Override
    public void createBook(Book newBook) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK);
        preparedStatement.setString(1, newBook.getTitle());
        preparedStatement.setString(2, newBook.getAuthor());
        preparedStatement.setString(3, newBook.getIsbn());
        preparedStatement.setInt(4, newBook.getQuantity());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void updateBook(String newTitle, String newAuthor, String newISBN, String newQuantity, int book_id) throws SQLException {

        // TODO: executeUpdate(UPDATE_BOOK, new Object[]{newTitle, newAuthor,...});

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK);
        preparedStatement.setString(1, newTitle);
        preparedStatement.setString(2, newAuthor);
        preparedStatement.setString(3, newISBN);
        preparedStatement.setString(4, newQuantity);
        preparedStatement.setInt(5, book_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void deleteBook(int book_id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BOOK);
        preparedStatement.setInt(1, book_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
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

    @Override
    public List<Book> selectBooksByAuthor(String author) throws SQLException {
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

    @Override
    public List<Book> selectBooksByISBN(String isbn) throws SQLException {
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

    @Override
    public boolean doesBookExist(String title, String author, String isbn) throws SQLException {

        // TODO: ResultSet rs = executeQuery(SELECT_BOOK_BY_TITLE_AND_AUTHOR_AND_ISBN, new Object[]{title, author, ...});

        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_BY_TITLE_AND_AUTHOR_AND_ISBN);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.setString(3, isbn);
        ResultSet rs = preparedStatement.executeQuery();
        List<Book> books = getBookList(rs);
        boolean bookExists = !books.isEmpty();
        return bookExists;
    }

    @Override
    public int getBookID(String title, String author, String isbn, String quantity) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BOOK_ID);
        preparedStatement.setString(1, title);
        preparedStatement.setString(2, author);
        preparedStatement.setString(3, isbn);
        preparedStatement.setString(4, quantity);
        ResultSet rs = preparedStatement.executeQuery();
        int bookID = -1;
        if (rs.next()) { // to check is rs is empty
            bookID = rs.getInt("book_id");
        }
        rs.close();
        preparedStatement.close();
        connection.close();
        return bookID;
    }

    @Override
    public int countBooks() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BOOKS);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        int totalBooks = rs.getInt(1);
        rs.close();
        preparedStatement.close();
        connection.close();
        return totalBooks;
    }
    //
    // end of book commands
    //

    //
    // start of member commands
    //
    @Override
    public void createMember(Member newMember) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MEMBER);
        preparedStatement.setInt(1, newMember.getID());
        preparedStatement.setString(2, newMember.getName());
        preparedStatement.setString(3, newMember.getSurname());
        preparedStatement.setString(4, newMember.getPhoneNo());
        preparedStatement.setString(5, newMember.getEmail());
        preparedStatement.setString(6, newMember.getAddress());
        preparedStatement.setString(7, newMember.getPostcode());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void updateMember(int newID, String newName, String newSurname, String newPhoneNo, String newEmail, String newAddress, String newPostcode, int oldID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MEMBER);
        preparedStatement.setInt(1, newID);
        preparedStatement.setString(2, newName);
        preparedStatement.setString(3, newSurname);
        preparedStatement.setString(4, newPhoneNo);
        preparedStatement.setString(5, newEmail);
        preparedStatement.setString(6, newAddress);
        preparedStatement.setString(7, newPostcode);
        preparedStatement.setInt(8, oldID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void deleteMember(int id) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_MEMBER);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public boolean noMembers() throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_MEMBERS)) {
            ResultSet rs = preparedStatement.executeQuery();
            boolean isEmptyResultSet = !rs.next();
            rs.close();
            preparedStatement.close();
            connection.close();
            return isEmptyResultSet;
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public String selectMemberNameSurnameByMemberID(int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_MEMBER_NAME_AND_SURNAME_BY_MEMBER_ID);
        preparedStatement.setInt(1, memberID);
        ResultSet rs = preparedStatement.executeQuery();
        String name = null;
        String surname = null;
        if (rs.next()) { // to check is rs is empty
            name = rs.getString("name");
            surname = rs.getString("surname");
        }
        rs.close();
        preparedStatement.close();
        connection.close();
        return name+" "+surname;
    }

    @Override
    public int countMembers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(COUNT_MEMBERS);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        int totalMembers = rs.getInt(1);
        rs.close();
        preparedStatement.close();
        connection.close();
        return totalMembers;
    }
    //
    // end of member commands
    //

    //
    // start of borrower commands
    //
    @Override
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

    @Override
    public void updateBorrower(int newMemberID, String newReturnDate, int bookID, int oldMemberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BORROWER);
        preparedStatement.setInt(1, newMemberID);
        preparedStatement.setString(2, newReturnDate);
        preparedStatement.setInt(3, bookID);
        preparedStatement.setInt(4, oldMemberID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void deleteBorrower(int bookID, int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BORROWER);
        preparedStatement.setInt(1, bookID);
        preparedStatement.setInt(2, memberID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void deleteBorrowerByBookID(int bookID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BORROWER_BY_BOOK_ID);
        preparedStatement.setInt(1, bookID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void deleteBorrowerByMemberID(int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BORROWER_BY_MEMBER_ID);
        preparedStatement.setInt(1, memberID);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public List<Borrower> selectBorrowersByBookID(int bookID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BORROWERS_BY_BOOK_ID);
        preparedStatement.setInt(1, bookID);
        ResultSet rs = preparedStatement.executeQuery();
        List<Borrower> borrowers = new ArrayList<>();
        while (rs.next()) {
            int memberID = rs.getInt("member_id");
            String returnDate = rs.getString("return_date");
            String fullName = rs.getString("name") + " " + rs.getString("surname");
            borrowers.add(new Borrower(bookID, memberID, fullName, returnDate));
        }
        rs.close();
        preparedStatement.close();
        connection.close();
        return borrowers;
    }

    @Override
    public boolean isBookBorrowedByMember(int bookID, int memberID) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BORROWERS_BY_BOOK_ID_AND_MEMBER_ID);
        preparedStatement.setInt(1, bookID);
        preparedStatement.setInt(2, memberID);
        ResultSet rs = preparedStatement.executeQuery();
        boolean isBorrowed = rs.next();
        rs.close();
        preparedStatement.close();
        connection.close();
        return isBorrowed;
    }

    @Override
    public int countBorrowers() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(COUNT_BORROWERS);
        ResultSet rs = preparedStatement.executeQuery();
        rs.next();
        int numOfBooksBorrowed = rs.getInt(1);
        rs.close();
        preparedStatement.close();
        connection.close();
        return numOfBooksBorrowed;
    }
    //
    // end of borrower commands
    //

    //
    // start of user commands
    //
    @Override
    public void createUser(User user) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER);
        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getFullName());
        preparedStatement.setString(3, user.getPassword());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void updateUser(String newUsername, String newFullName, String newPassword, String oldUsername) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);
        preparedStatement.setString(1, newUsername);
        preparedStatement.setString(2, newFullName);
        preparedStatement.setString(3, newPassword);
        preparedStatement.setString(4, oldUsername);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public void deleteUser(String username) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER);
        preparedStatement.setString(1, username);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    @Override
    public boolean noStaff() throws SQLException {
        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = preparedStatement.executeQuery();
            boolean isEmptyResultSet = !rs.next();
            rs.close();
            preparedStatement.close();
            connection.close();
            return isEmptyResultSet;
        }
    }

    @Override
    public List<User> selectUsersByUsername(String username) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME);
        preparedStatement.setString(1, "%"+username+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<User> users =  getUserList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return users;
    }

    @Override
    public boolean isValidUser(String username, String password) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_USERNAME_AND_PASSWORD);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet rs = preparedStatement.executeQuery();
        List<User> users =  getUserList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        boolean found = !users.isEmpty();
        return found;
    }


    @Override
    public List<User> selectUsersByFullName(String fullName) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_FULLNAME);
        preparedStatement.setString(1, "%"+fullName+"%");
        ResultSet rs = preparedStatement.executeQuery();
        List<User> users =  getUserList(rs);
        rs.close();
        preparedStatement.close();
        connection.close();
        return users;
    }
    //
    // end of user commands
    //

    //
    // start of list getters
    //
    private static List<Book> getBookList(ResultSet rs) throws SQLException {
        List<Book> books = new ArrayList<>();
        while (rs.next()) {
            String title = rs.getString("title");
            String author = rs.getString("author");
            String isbn = rs.getString("isbn");
            int quantity = rs.getInt("quantity");
            books.add(new Book(title, author, isbn, quantity));
        }
        return books;
    }

    private static List<Member> getMemberList(ResultSet rs) throws SQLException {
        List<Member> members = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("member_id");
            String name = rs.getString("name");
            String surname = rs.getString("surname");
            String phoneNo = rs.getString("phone");
            String email = rs.getString("email");
            String address = rs.getString("address");
            String postcode = rs.getString("postcode");
            members.add(new Member(id, name, surname, phoneNo, email, address, postcode));
        }
        return members;
    }

    private static List<User> getUserList(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            String username = rs.getString("username");
            String fullName = rs.getString("full_name");
            String password = rs.getString("password");
            users.add(new User(username, fullName, password));
        }
        return users;
    }
    //
    // end of list getters
    //

}
