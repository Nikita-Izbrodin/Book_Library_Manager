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

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

}
