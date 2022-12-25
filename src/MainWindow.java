import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainWindow {

    private JPanel MainWindow;
    private JPanel statsPanel;
    private JPanel toolbarPanel;
    private JComboBox menuComboBox;
    private JPanel mainPanel;
    private JButton addNewButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JButton searchButton;
    private JList<Book> bookList;
    private JScrollBar scrollBar1;
    private JPanel allContentsPanel;
    private JPanel selectedContentsPanel;
    private JPanel searchPanel;

    public static void main(String[] args) {
        LibraryDB db = new LibraryDB();

        try {

            if (db.noStaff()){
                CreateUserDialog createUserDialog = new CreateUserDialog();
                createUserDialog.pack();
                createUserDialog.show();
                User newUser = createUserDialog.getNewUser();

                if (newUser == null) {
                    // exit from the application
                    return;
                }

                db.createUser(newUser);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }

        showMainWindow();
    }

    private static void showMainWindow() {
        JFrame frame = new JFrame("MainWindow");
        MainWindow mainWindow = new MainWindow();
        frame.setContentPane(mainWindow.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MainWindow() {
        LibraryDB db = new LibraryDB();

        showAllBooks();

        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateBookDialog createBookDialog = new CreateBookDialog();
                createBookDialog.pack();
                createBookDialog.show();
                Book newBook = createBookDialog.getNewBook();

                if (newBook == null) {
                    // exit from the application
                    return;
                }

                try {
                    db.createBook(newBook);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        bookList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                //System.out.println(bookList.getSelectedValue());
                // TODO: make selected item highlighted
            }
        });

        textField1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                System.out.println("type");
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Test");
            }
        });
    }

    private void showAllBooks() {

        LibraryDB db = new LibraryDB();

        try {
            List<Book> booksList = db.selectAllBooks();

            Book[] booksArray = booksList.toArray(new Book[booksList.size()]);
            bookList.setListData(booksArray);
            bookList.setCellRenderer(new BookCellRenderer());

            // TODO: filter JList with Text Field

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

}
