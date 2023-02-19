package DataAccess;

import Entities.Book;
import Entities.Borrower;
import Entities.Member;
import Entities.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * An interface for interacting with a database.
 */
public interface LibraryDatabase {

    //
    // book methods
    //
    void createBook(Book newBook) throws SQLException;

    void updateBook(Book book, int book_id) throws SQLException;

    void deleteBook(int book_id) throws SQLException;

    List<Book> selectBooksByTitle(String title) throws SQLException;

    List<Book> selectBooksByAuthor(String author) throws SQLException;

    List<Book> selectBooksByISBN(String isbn) throws SQLException;

    int getBookID(String title, String author, String isbn) throws SQLException;

    boolean doesBookExist(String title, String author, String isbn) throws SQLException;

    int countBooks() throws SQLException;

    //
    // member methods
    //
    void createMember(Member member) throws SQLException;

    void updateMember(Member member, int oldID) throws SQLException;

    void deleteMember(int id) throws SQLException;

    boolean noMembers() throws SQLException;

    List<Member> selectMembersByName(String name) throws SQLException;

    List<Member> selectMembersBySurname(String surname) throws SQLException;

    List<Member> selectMembersByPhoneNo(String phoneNo) throws SQLException;

    List<Member> selectMembersByEmail(String email) throws SQLException;

    List<Member> selectMembersByAddress(String address) throws SQLException;

    List<Member> selectMembersByPostcode(String postcode) throws SQLException;

    boolean isMemberIDUsed(int memberID) throws  SQLException;

    String selectMemberNameSurnameByMemberID(int memberID) throws SQLException;

    int countMembers() throws SQLException;

    //
    // borrower methods
    //
    void createBorrower(Borrower newBorrower) throws SQLException;

    void updateBorrower(int newMemberID, LocalDate newReturnDate, int bookID, int oldMemberID) throws SQLException;

    void deleteBorrower(int bookID, int memberID) throws SQLException;

    boolean isBorrower(int memberID) throws SQLException;

    List<Borrower> selectBorrowersByBookID(int bookID) throws SQLException;

    boolean isBookBorrowedByMember(int bookID, int memberID) throws SQLException;

    int countBorrowers() throws SQLException;

    //
    // user methods
    //
    void createUser(User user) throws SQLException;

    void updateUser(String newUsername, String newFullName, String newPassword, String oldUsername) throws SQLException;

    void deleteUser(String username) throws SQLException;

    boolean noUsers() throws SQLException;

    List<User> selectUsersByUsername(String username) throws SQLException;

    boolean isValidUser(String username, String password) throws SQLException;

    List<User> selectUsersByFullName(String fullName) throws SQLException;
}
