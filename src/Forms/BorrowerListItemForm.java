package Forms;

import Entities.Borrower;

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
        borrowerIDLabel.setText("ID: "+entry.memberID());
        borrowerFullNameLabel.setText("Full name: "+entry.fullName());
        returnDateLabel.setText("Return date: "+entry.returnDate());
    }
}
