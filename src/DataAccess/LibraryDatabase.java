package DataAccess;

import Entities.Book;
import Entities.Borrower;
import Entities.Member;
import Entities.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface LibraryDatabase {
    //
    // start of book commands
    //
    void createBook(Book newBook) throws SQLException;

    void updateBook(String newTitle, String newAuthor, String newISBN, String newQuantity, int book_id) throws SQLException;

    void deleteBook(int book_id) throws SQLException;

    List<Book> selectBooksByTitle(String title) throws SQLException;

    List<Book> selectBooksByAuthor(String author) throws SQLException;

    List<Book> selectBooksByISBN(String isbn) throws SQLException;

    int getBookID(String title, String author, String isbn, String quantity) throws SQLException;

    int countBooks() throws SQLException;

    //
    // start of member commands
    //
    void createMember(Member newMember) throws SQLException;

    void updateMember(int newID, String newName, String newSurname,
                      String newPhoneNo, String newEmail, String newAddress, String newPostcode,
                      int oldID)
            throws SQLException;

    void deleteMember(int id) throws SQLException;

    boolean noMembers() throws SQLException;

    List<Member> selectMembersByName(String name) throws SQLException;

    List<Member> selectMembersBySurname(String surname) throws SQLException;

    List<Member> selectMembersByPhoneNo(String phoneNo) throws SQLException;

    List<Member> selectMembersByEmail(String email) throws SQLException;

    boolean isEmailUsed(String email) throws SQLException;

    List<Member> selectMembersByAddress(String address) throws SQLException;

    List<Member> selectMembersByPostcode(String postcode) throws SQLException;

    String selectMemberNameSurnameByMemberID(int memberID) throws SQLException;

    int countMembers() throws SQLException;

    //
    // start of borrower commands
    //
    void createBorrower(Borrower newBorrower) throws SQLException;

    void updateBorrower(int newMemberID, LocalDate newReturnDate, int bookID, int oldMemberID) throws SQLException;

    void deleteBorrower(int bookID, int memberID) throws SQLException;

    boolean hasMemberBorrowedBook(int memberID) throws SQLException;

    List<Borrower> selectBorrowersByBookID(int bookID) throws SQLException;

    boolean isBookBorrowedByMember(int bookID, int memberID) throws SQLException;

    int countBorrowers() throws SQLException;

    //
    // start of user commands
    //
    void createUser(User user) throws SQLException;

    void updateUser(String newUsername, String newFullName, String newPassword, String oldUsername) throws SQLException;

    void deleteUser(String username) throws SQLException;

    boolean noStaff() throws SQLException;

    List<User> selectUsersByUsername(String username) throws SQLException;

    boolean isValidUser(String username, String password) throws SQLException;

    List<User> selectUsersByFullName(String fullName) throws SQLException;
}
