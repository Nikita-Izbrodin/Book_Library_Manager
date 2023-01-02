import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class BorrowerListItemForm {
    private JButton editOrReturnedButton;
    private JLabel borrowerIDLabel;
    private JLabel returnDateLabel;
    private JPanel borrowerItemPanel;
    private JLabel borrowerFullNameLabel;

    /*public BorrowerListItemForm() {
        editOrReturnedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BorrowerDialog borrowerDialog = new BorrowerDialog("edit", Integer.parseInt(borrowerIDLabel.getText()), returnDateLabel.getText());
                borrowerDialog.pack();
                borrowerDialog.show();
                Borrower editedBorrower = borrowerDialog.getBorrower();
                if (editedBorrower == null) {
                    return;
                }
                LibraryDB db = new LibraryDB();
                try {
                    db.updateBorrower(editedBorrower.getMemberID(), editedBorrower.getReturnDate(), editedBorrower.getBookID(), Integer.parseInt(borrowerIDLabel.getText()), returnDateLabel.getText());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                JOptionPane.showMessageDialog(null, "Borrow updated successfully.", "Borrow update", JOptionPane.INFORMATION_MESSAGE);
                MainWindow temp = new MainWindow(); // only used to call displayBorrowers method
                temp.displayBorrowers();
            }
        });
    }*/

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
