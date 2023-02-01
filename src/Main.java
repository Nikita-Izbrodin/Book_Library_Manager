import Dialogs.UserDialog;
import Forms.MainWindowForm;
import Entities.User;
import Utils.EmailAddressChecker;
import Utils.HashGenerator;
import DataAccess.MySqlLibraryDatabase;
import Utils.RegExEmailAddressChecker;
import Utils.Sha256HashGenerator;

import javax.swing.JOptionPane;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        HashGenerator hashGenerator = new Sha256HashGenerator();
        EmailAddressChecker emailAddressChecker = new RegExEmailAddressChecker();
        MySqlLibraryDatabase libraryDB = new MySqlLibraryDatabase();

        try {
            if (libraryDB.noStaff()){ // if there are no users in database
                User newUser = UserDialog.getUser(UserDialog.DialogType.CREATE, null, null, hashGenerator);
                if (newUser == null) { // if cancel pressed on userDialog
                    return; // exit from the application
                }
                libraryDB.createUser(newUser);
            } else { // if there are users in database
                boolean loginLoop = true;
                while (loginLoop) {
                    User user = UserDialog.getUser(UserDialog.DialogType.LOGIN, null, null, hashGenerator);
                    if (user == null) { // cancel pressed on userDialog
                        return; // exit from application
                    }
                    if (libraryDB.isValidUser(user.getUsername(), user.getPassword())) { // if username and password are correct, log in is successful
                        loginLoop = false;
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid username or password.", "Log In failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
                MainWindowForm.showMainWindow(hashGenerator, emailAddressChecker, libraryDB);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
