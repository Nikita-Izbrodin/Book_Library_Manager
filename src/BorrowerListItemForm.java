import javax.swing.JLabel;
import javax.swing.JPanel;

public class BorrowerListItemForm {
    private JLabel borrowerIDLabel;
    private JLabel returnDateLabel;
    private JPanel borrowerItemPanel;
    private JLabel borrowerFullNameLabel;

    public JPanel getPanel(){
        return borrowerItemPanel;
    }

    public void setData(Borrower entry) {
        borrowerIDLabel.setText("ID: "+entry.getMemberID());
        borrowerFullNameLabel.setText("Full name: "+entry.getFullName());
        returnDateLabel.setText("Return date: "+entry.getReturnDate());
    }

}
