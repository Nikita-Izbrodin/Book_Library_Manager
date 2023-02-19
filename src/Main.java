import DataAccess.LibraryDatabase;
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

/**
 * A class for login and signup (creating a user).
 * Determines whether to show login or signup dialog when the application is run.
 * Login contains an algorithm to weaken brute-force attacks.
 */
public class Main {

    public static void main(String[] args) {

        HashGenerator hashGenerator = new Sha256HashGenerator();
        EmailAddressChecker emailAddressChecker = new RegExEmailAddressChecker();
        LibraryDatabase libraryDatabase = new MySqlLibraryDatabase();

        try {

            if (libraryDatabase.noUsers()){ // if there are no users in database

                User newUser = UserDialog.getUser(
                        UserDialog.DialogType.CREATE,
                        null,
                        null,
                        hashGenerator
                );

                if (newUser == null) { // if cancel pressed on userDialog
                    return; // exit from the application
                }

                libraryDatabase.createUser(newUser);
            } else { // if there are users in database

                boolean loginLoop = true;

                while (loginLoop) {

                    User user = UserDialog.getUser(
                            UserDialog.DialogType.LOGIN,
                            null,
                            null,
                            hashGenerator
                    );

                    if (user == null) { // cancel pressed on userDialog
                        return; // exit from application
                    }

                    if (libraryDatabase.isValidUser(
                            user.username(),
                            user.password())
                    ) { // if username and password are correct, log in is successful
                        loginLoop = false;
                    } else {
                        Thread.sleep(3000); /* wait 3 seconds in case of unsuccessful login
                                                     to make brute force attacks less efficient */
                        JOptionPane.showMessageDialog(
                                null,
                                "Invalid username or password.",
                                "Log In failed",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
            MainWindowForm.showMainWindow(hashGenerator, emailAddressChecker, libraryDatabase);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
