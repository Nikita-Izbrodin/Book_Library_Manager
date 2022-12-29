import javax.swing.*;
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
    private JComboBox searchByComboBox;
    private JTextField searchTextField;
    private JList<Book> bookList;
    private JScrollBar scrollBar1;
    private JPanel allContentsPanel;
    private JPanel selectedParentCardPanel;
    private JPanel searchPanel;
    private JPanel bookCardPanel;
    private JPanel memberCardPanel;
    private JPanel staffCardPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;
    private JButton deleteBookButton;
    private JButton editBookButton;

    public static void main(String[] args) {
        LibraryDB db = new LibraryDB();
        try {
            if (db.noStaff()){
                CreateUserDialog createUserDialog = new CreateUserDialog();
                createUserDialog.pack();
                createUserDialog.show();
                User newUser = createUserDialog.getNewUser();
                if (newUser == null) {
                    return; // exit from the application
                }
                db.createUser(newUser);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        showMainWindow();
    }

    private static void showMainWindow() {
        JFrame frame = new JFrame("Book Library Manager");
        MainWindow mainWindow = new MainWindow();
        frame.setContentPane(mainWindow.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public MainWindow() {
        LibraryDB db = new LibraryDB();
        selectBooksBy(); // to display all books

        // start of actionlisteners
        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BookDialog bookDialog = new BookDialog("create", null, null, null, null);
                bookDialog.pack();
                bookDialog.show();
                Book newBook = bookDialog.getBook();
                if (newBook == null) {
                    return;
                }
                try {
                    db.createBook(newBook);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectBooksBy(); // to update displayed list of books
            }
        });

        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isEmpty()) { // if nothing is selected
                    return;
                }
                BookDialog bookDialog = new BookDialog("edit", titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                bookDialog.pack();
                bookDialog.show();
                Book editedBook = bookDialog.getBook();
                if (editedBook == null) { // if cancel pressed
                    return;
                }
                try {
                    db.updateBook(editedBook.getTitle(), editedBook.getAuthor(), String.valueOf(editedBook.getIsbn()), String.valueOf(editedBook.getQuantity()), bookList.getSelectedValue().getTitle(), bookList.getSelectedValue().getAuthor(), String.valueOf(bookList.getSelectedValue().getIsbn()), String.valueOf(bookList.getSelectedValue().getQuantity()));
                    JOptionPane.showMessageDialog(null, "Book updated successfully.", "Book update", JOptionPane.INFORMATION_MESSAGE);
                    titleLabel.setText(editedBook.getTitle());
                    authorLabel.setText(editedBook.getAuthor());
                    isbnLabel.setText(String.valueOf(editedBook.getIsbn()));
                    quantityLabel.setText(String.valueOf(editedBook.getQuantity()));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectBooksBy(); // to update displayed list of books
            }
        });

        bookList.addMouseListener(new MouseAdapter() { // when item in list is selected
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // TODO: make selected item highlighted
                titleLabel.setText(bookList.getSelectedValue().getTitle());
                authorLabel.setText(bookList.getSelectedValue().getAuthor());
                isbnLabel.setText(String.valueOf(bookList.getSelectedValue().getIsbn()));
                quantityLabel.setText(String.valueOf(bookList.getSelectedValue().getQuantity()));
            }
        });

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //super.keyTyped(e); // not sure why this line was added. might remove
                selectBooksBy();
            }
        });

        searchByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectBooksBy();
            }
        });

        menuComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedParentCardPanel.removeAll();
                String menuOption = menuComboBox.getSelectedItem().toString();
                if (menuOption.equals("Books")) {
                    selectedParentCardPanel.add(bookCardPanel);
                } else if (menuOption.equals("Members")) {
                    selectedParentCardPanel.add(memberCardPanel);
                } else if (menuOption.equals("Staff")) {
                    selectedParentCardPanel.add(staffCardPanel);
                }
                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });
        // end of actionlisteners
    }

    private void selectBooksBy() {
        LibraryDB db = new LibraryDB();
        List<Book> booksList = null;
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Title")) {
                booksList = db.selectBooksByTitle(searchTextField.getText());

            } else if (searchBy.equals("Author")) {
                booksList = db.selectBooksByAuthor(searchTextField.getText());

            } else if (searchBy.equals("ISBN")) {
                booksList = db.selectBooksByISBN(searchTextField.getText());

            }
            Book[] booksArray = booksList.toArray(new Book[booksList.size()]);
            bookList.setListData(booksArray);
            bookList.setCellRenderer(new BookCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
