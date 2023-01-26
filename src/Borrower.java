public class Borrower {

    private int bookID;
    private int memberID;
    private String returnDate;

    public Borrower(int bookID, int memberID, String returnDate) {
        this.bookID = bookID;
        this.memberID = memberID;
        this.returnDate = returnDate;
    }

    public int getBookID() {
        return bookID;
    }

    public int getMemberID() {
        return memberID;
    }

    public String getReturnDate() {
        return returnDate;
    }

}
