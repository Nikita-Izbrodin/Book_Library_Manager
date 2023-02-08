package Forms;

import Entities.Borrower;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.time.LocalDate;

public class BorrowerListItemForm {
    private JLabel borrowerIDLabel;
    private JLabel returnDateLabel;
    private JPanel borrowerItemPanel;
    private JLabel borrowerFullNameLabel;

    public JPanel getPanel(){
        return borrowerItemPanel;
    }

    protected void setData(Borrower entry) {

        borrowerIDLabel.setText("ID: "+entry.memberID());
        borrowerFullNameLabel.setText("Full name: "+entry.fullName());
        returnDateLabel.setText("Return date: "+entry.returnDate());

        LocalDate currentDate = java.time.LocalDate.now();
        if (entry.returnDate().isBefore(currentDate) || entry.returnDate().equals(currentDate)) {
            borrowerIDLabel.setForeground(Color.RED);
            borrowerFullNameLabel.setForeground(Color.RED);
            returnDateLabel.setForeground(Color.RED);
        }
    }
}
