package Entities;

public class Borrower {

    private final int bookID;
    private final int memberID;
    private final String fullName;
    private final String returnDate;

    public Borrower(int bookID, int memberID, String fullName, String returnDate) {
        this.bookID = bookID;
        this.memberID = memberID;
        this.fullName = fullName;
        this.returnDate = returnDate;
    }

    public int getBookID() {
        return bookID;
    }

    public int getMemberID() {
        return memberID;
    }

    public String getFullName() {
        return fullName;
    }

    public String getReturnDate() {
        return returnDate;
    }
}
