import javax.swing.*;
import java.sql.SQLException;

public class BorrowerListItemForm {
    private JButton returnButton;
    private JButton editButton;
    private JLabel borrowerIDLabel;
    private JLabel returnDateLabel;
    private JPanel borrowerItemPanel;
    private JLabel borrowerFullNameLabel;

    public JPanel getPanel(){
        return borrowerItemPanel;
    }

    public void setData(Borrower entry) {
        borrowerIDLabel.setText("ID: "+entry.getMemberID());
        LibraryDB db = new LibraryDB();
        try {
            borrowerFullNameLabel.setText("Full name: "+db.selectMemberNameSurnameByMemberID(entry.getMemberID()));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        returnDateLabel.setText("Return date: "+entry.getReturnDate());
    }

}
