import javax.swing.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private JPanel searchResultsParentCardPanel;
    private JPanel selectedParentCardPanel;
    private JPanel searchPanel;
    private JPanel bookCardPanel;
    private JPanel memberCardPanel;
    private JPanel userCardPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;
    private JButton deleteBookButton;
    private JButton editBookButton;
    private JButton editMemberButton;
    private JButton deleteMemberButton;
    private JLabel postcodeLabel;
    private JLabel phoneNoLabel;
    private JLabel surnameLabel;
    private JLabel nameLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;
    private JPanel bookSearchCardPanel;
    private JPanel memberSearchCardPanel;
    private JPanel userSearchCardPanel;
    private JList<Member> memberList;
    private JList<Borrower> borrowerList;
    private JButton addBorrowerButton;
    private JLabel idLabel;
    private JButton deleteUserButton;
    private JButton editUserButton;
    private JList<User> userList;
    private JLabel usernameLabel;
    private JLabel fullNameLabel;
    private JList editBorrowerButtonList;

    public static void main(String[] args) {
        LibraryDB db = new LibraryDB();
        try {
            if (db.noStaff()){
                UserDialog userDialog = new UserDialog("create", null, null);
                userDialog.pack();
                userDialog.show();
                User newUser = userDialog.getUser();
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
        selectBooksBy();

        //
        // start of ActionListeners
        //
        addNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuComboBox.getSelectedItem() == null) {
                    return;
                }
                if (menuComboBox.getSelectedItem().toString().equals("Books")) {
                    BookDialog bookDialog = new BookDialog("create", null, null, null, null);
                    bookDialog.pack();
                    bookDialog.show();
                    Book newBook = bookDialog.getBook();
                    if (newBook == null) {
                        return;
                    }
                    try {
                        db.createBook(newBook);
                        JOptionPane.showMessageDialog(null, "Book created successfully.", "Book create", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    selectBooksBy();
                } else if (menuComboBox.getSelectedItem().toString().equals("Members")) {
                    MemberDialog memberDialog = new MemberDialog("create", -1, null, null, null, null, null, null);
                    memberDialog.pack();
                    memberDialog.show();
                    Member newMember = memberDialog.getMember();
                    if (newMember == null) {
                        return;
                    }
                    try {
                        db.createMember(newMember);
                        JOptionPane.showMessageDialog(null, "Member created successfully.", "Member create", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    selectMembersBy();
                } else if (menuComboBox.getSelectedItem().toString().equals("Users")) {
                    UserDialog userDialog = new UserDialog("create", null, null);
                    userDialog.pack();
                    userDialog.show();
                    User newUser = userDialog.getUser();
                    if (newUser == null) {
                        return;
                    }
                    try {
                        db.createUser(newUser);
                        JOptionPane.showMessageDialog(null, "User created successfully.", "User create", JOptionPane.INFORMATION_MESSAGE);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    selectUsersBy();
                }
            }
        });

        addBorrowerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                try {
                    if (db.noMembers()) { // checks if there are any members
                        JOptionPane.showMessageDialog(null, "There are no members in the database.", "", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                BorrowerDialog borrowerDialog = new BorrowerDialog("create", -1, null);
                borrowerDialog.pack();
                borrowerDialog.show();
                Borrower newBorrower = borrowerDialog.getBorrower();
                if (newBorrower == null) {
                    return;
                }
                try {
                    int bookID = db.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                    newBorrower.setBookID(bookID);
                    db.createBorrower(newBorrower);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                displayBorrowers();
            }
        });

        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isBlank()) { // if nothing is selected
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
                    db.updateBook(editedBook.getTitle(), editedBook.getAuthor(), String.valueOf(editedBook.getIsbn()), String.valueOf(editedBook.getQuantity()), titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                    JOptionPane.showMessageDialog(null, "Book updated successfully.", "Book update", JOptionPane.INFORMATION_MESSAGE);
                    titleLabel.setText(editedBook.getTitle());
                    authorLabel.setText(editedBook.getAuthor());
                    isbnLabel.setText(String.valueOf(editedBook.getIsbn()));
                    quantityLabel.setText(String.valueOf(editedBook.getQuantity()));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectBooksBy();
            }
        });

        deleteBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?\nTitle: "+titleLabel.getText()+"\nAuthor: "+authorLabel.getText()+"\nISBN: "+isbnLabel.getText()+"\nQuantity: "+quantityLabel.getText(), "Delete book", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                    return;
                }
                try {
                    int bookID = db.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                    db.deleteBorrowerByBookID(bookID); // removes foreign key to allow deletion
                    db.deleteBook(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectBooksBy();
                displayBorrowers();
                titleLabel.setText("");
                authorLabel.setText("");
                isbnLabel.setText("");
                quantityLabel.setText("");
            }
        });

        editMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                MemberDialog memberDialog = new MemberDialog("edit", Integer.parseInt(idLabel.getText()),nameLabel.getText(), surnameLabel.getText(), phoneNoLabel.getText(), emailLabel.getText(), addressLabel.getText(), postcodeLabel.getText());
                memberDialog.pack();
                memberDialog.show();
                Member editedMember = memberDialog.getMember();
                if (editedMember == null) { // if cancel pressed
                    return;
                }
                try {
                    db.updateMember(editedMember.getID(), editedMember.getName(), editedMember.getSurname(), editedMember.getPhoneNo(), editedMember.getEmail(), editedMember.getAddress(), editedMember.getPostcode(), Integer.parseInt(idLabel.getText()), nameLabel.getText(), surnameLabel.getText(), phoneNoLabel.getText(), emailLabel.getText(), addressLabel.getText(), postcodeLabel.getText());
                    JOptionPane.showMessageDialog(null, "Member updated successfully.", "Member update", JOptionPane.INFORMATION_MESSAGE);
                    idLabel.setText(String.valueOf(editedMember.getID()));
                    nameLabel.setText(editedMember.getName());
                    surnameLabel.setText(editedMember.getSurname());
                    phoneNoLabel.setText(editedMember.getPhoneNo());
                    emailLabel.setText(editedMember.getEmail());
                    addressLabel.setText(editedMember.getAddress());
                    postcodeLabel.setText(editedMember.getPostcode());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectMembersBy();
            }
        });

        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nameLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }
                int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this member?\nName: "+nameLabel.getText()+"\nSurname: "+surnameLabel.getText()+"\nPhone No: "+phoneNoLabel.getText()+"\nEmail: "+emailLabel.getText()+"\nAddress: "+addressLabel.getText()+"\nPostcode: "+postcodeLabel.getText(), "Delete member", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (answer == -1 || answer == 1) { // if cross pressed or if "no" pressed
                    return;
                }
                try {
                    db.deleteBorrowerByMemberID(Integer.parseInt(idLabel.getText())); // removes foreign key to allow deletion
                    db.deleteMember(Integer.parseInt(idLabel.getText()), nameLabel.getText(), surnameLabel.getText(), phoneNoLabel.getText(), emailLabel.getText(), addressLabel.getText(), postcodeLabel.getText());
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                }
                selectMembersBy();
                idLabel.setText("");
                nameLabel.setText("");
                surnameLabel.setText("");
                phoneNoLabel.setText("");
                emailLabel.setText("");
                addressLabel.setText("");
                postcodeLabel.setText("");
            }
        });

        bookList.addMouseListener(new MouseAdapter() { // when item in list is selected
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                // TODO: make selected item highlighted
                if (bookList.getSelectedValue() == null) {
                    return;
                }
                titleLabel.setText(bookList.getSelectedValue().getTitle());
                authorLabel.setText(bookList.getSelectedValue().getAuthor());
                isbnLabel.setText(bookList.getSelectedValue().getIsbn());
                quantityLabel.setText(String.valueOf(bookList.getSelectedValue().getQuantity()));
                displayBorrowers();
            }
        });

        memberList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (memberList.getSelectedValue() == null) {
                    return;
                }
                idLabel.setText(String.valueOf(memberList.getSelectedValue().getID()));
                nameLabel.setText(memberList.getSelectedValue().getName());
                surnameLabel.setText(memberList.getSelectedValue().getSurname());
                phoneNoLabel.setText(memberList.getSelectedValue().getPhoneNo());
                emailLabel.setText(memberList.getSelectedValue().getEmail());
                addressLabel.setText(memberList.getSelectedValue().getAddress());
                postcodeLabel.setText(memberList.getSelectedValue().getPostcode());
            }
        });

        borrowerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (borrowerList.getSelectedValue() == null) {
                    return;
                }
                String[] options = {"Edit", "Book returned"};
                int answer = JOptionPane.showOptionDialog(
                        null,
                        "Choose an option",
                        "Edit / Returned",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options, // custom buttons
                        options[1] // default button
                );
                if (answer == 0) { // if "Edit" pressed
                    BorrowerDialog borrowerDialog = new BorrowerDialog("edit", borrowerList.getSelectedValue().getMemberID(), borrowerList.getSelectedValue().getReturnDate());
                    borrowerDialog.pack();
                    borrowerDialog.show();
                    Borrower editedBorrower = borrowerDialog.getBorrower();
                    if (editedBorrower == null) {
                        return;
                    }
                    LibraryDB db = new LibraryDB();
                    try {
                        int bookID = db.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
                        editedBorrower.setBookID(bookID);
                        db.updateBorrower(editedBorrower.getMemberID(), editedBorrower.getReturnDate(), editedBorrower.getBookID(), borrowerList.getSelectedValue().getMemberID(), borrowerList.getSelectedValue().getReturnDate());
                        JOptionPane.showMessageDialog(null, "Borrow updated successfully.", "Borrow update", JOptionPane.INFORMATION_MESSAGE);
                        displayBorrowers();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (answer == 1) { // if "Book returned" pressed
                    try {
                        String fullName = db.selectMemberNameSurnameByMemberID(borrowerList.getSelectedValue().getMemberID());
                        int bookReturned = JOptionPane.showConfirmDialog(null, "Has "+fullName+" returned "+titleLabel.getText()+"?", "Book returned", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                        if (bookReturned == 0) { // if "Yes" pressed
                            db.deleteBorrower(borrowerList.getSelectedValue().getBookID(), borrowerList.getSelectedValue().getMemberID(), borrowerList.getSelectedValue().getReturnDate());
                            displayBorrowers();
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                //super.keyTyped(e); // not sure why this line was added. might remove
                selectBooksBy();
                selectMembersBy();
                selectUsersBy();
            }
        });

        searchByComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*System.out.println(searchByComboBox.getSelectedItem().toString());
                System.out.println(menuComboBox.getSelectedItem().toString());*/
                selectBooksBy();
                selectMembersBy();
                /*if (menuComboBox.equals("Books")) {
                    selectBooksBy();
                } else if (menuComboBox.equals("Members")) {
                    selectMembersBy();
                } else if (menuComboBox.equals("Users")) {

                }*/
            }
        });

        menuComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedParentCardPanel.removeAll();
                searchResultsParentCardPanel.removeAll();
                if (menuComboBox.getSelectedItem() == null) {
                    return;
                }
                String menuOption = menuComboBox.getSelectedItem().toString();
                if (menuOption.equals("Books")) {
                    selectedParentCardPanel.add(bookCardPanel);
                    searchResultsParentCardPanel.add(bookSearchCardPanel);
                    DefaultComboBoxModel bookModel = (DefaultComboBoxModel) searchByComboBox.getModel();
                    bookModel.removeAllElements();
                    ArrayList<String> types = new ArrayList<>(Arrays.asList("Title", "Author", "ISBN"));
                    for (String type : types) {
                        bookModel.addElement(type);
                    }
                    searchByComboBox.setModel(bookModel);
                    selectBooksBy();
                    displayBorrowers();
                } else if (menuOption.equals("Members")) {
                    selectedParentCardPanel.add(memberCardPanel);
                    searchResultsParentCardPanel.add(memberSearchCardPanel);
                    DefaultComboBoxModel memberModel = (DefaultComboBoxModel) searchByComboBox.getModel();
                    memberModel.removeAllElements();
                    ArrayList<String> types = new ArrayList<>(Arrays.asList("Name", "Surname", "Phone No", "Email", "Address", "Postcode"));
                    for (String type : types) {
                        memberModel.addElement(type);
                    }
                    searchByComboBox.setModel(memberModel);
                    selectMembersBy();
                } else if (menuOption.equals("Users")) {
                    selectedParentCardPanel.add(userCardPanel);
                    searchResultsParentCardPanel.add(userSearchCardPanel);
                    DefaultComboBoxModel userModel = (DefaultComboBoxModel) searchByComboBox.getModel();
                    userModel.removeAllElements();
                    ArrayList<String> types = new ArrayList<>(Arrays.asList("Username", "Full Name"));
                    for (String type : types) {
                        userModel.addElement(type);
                    }
                    searchByComboBox.setModel(userModel);
                    selectUsersBy();
                }
                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });
        //
        // end of ActionListeners
        //

    }

    // updates list of displayed books
    private void selectBooksBy() {
        LibraryDB db = new LibraryDB();
        List<Book> booksList = null;
        if (searchByComboBox.getSelectedItem() == null) { // not sure why this is needed
            return;
        }
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Title")) {
                booksList = db.selectBooksByTitle(searchTextField.getText());

            } else if (searchBy.equals("Author")) {
                booksList = db.selectBooksByAuthor(searchTextField.getText());

            } else if (searchBy.equals("ISBN")) {
                booksList = db.selectBooksByISBN(searchTextField.getText());

            }
            if (booksList == null) {
                return;
            }
            Book[] booksArray = booksList.toArray(new Book[booksList.size()]);
            bookList.setListData(booksArray);
            bookList.setCellRenderer(new BookCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // updates list of displayed members
    private void selectMembersBy() {
        LibraryDB db = new LibraryDB();
        List<Member> membersList = null;
        if (searchByComboBox.getSelectedItem() == null) { // not sure why this is needed
            return;
        }
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Name")) {
                membersList = db.selectMembersByName(searchTextField.getText());
            } else if (searchBy.equals("Surname")) {
                membersList = db.selectMembersBySurname(searchTextField.getText());
            } else if (searchBy.equals("Phone No")) {
                membersList = db.selectMembersByPhoneNo(searchTextField.getText());
            } else if (searchBy.equals("Email")) {
                membersList = db.selectMembersByEmail(searchTextField.getText());
            } else if (searchBy.equals("Address")) {
                membersList = db.selectMembersByAddress(searchTextField.getText());
            } else if (searchBy.equals("Postcode")) {
                membersList = db.selectMembersByPostcode(searchTextField.getText());
            }
            if (membersList == null) {
                return;
            }
            Member[] membersArray = membersList.toArray(new Member[membersList.size()]);
            memberList.setListData(membersArray);
            memberList.setCellRenderer(new MemberCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // updates list of displayed borrowers
    private void displayBorrowers() {
        LibraryDB db = new LibraryDB();
        List<Borrower> borrowersList = null;
        try {
            int bookID = db.getBookID(titleLabel.getText(), authorLabel.getText(), isbnLabel.getText(), quantityLabel.getText());
            borrowersList = db.selectBorrowers(bookID);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (borrowersList == null) {
            return;
        }
        Borrower[] borrowersArray = borrowersList.toArray(new Borrower[borrowersList.size()]);
        borrowerList.setListData(borrowersArray);
        borrowerList.setCellRenderer(new BorrowerCellRenderer());
    }

    // updates list of displayed users
    private void selectUsersBy() {
        LibraryDB db = new LibraryDB();
        List<User> usersList = null;
        if (searchByComboBox.getSelectedItem() == null) {
            return;
        }
        String searchBy = searchByComboBox.getSelectedItem().toString();
        try {
            if (searchBy.equals("Username")) {
                usersList = db.selectUsersByUsername(searchTextField.getText());
            } else if (searchBy.equals("Full Name")) {
                usersList = db.selectUsersByFullName(searchTextField.getText());
            }
            if (usersList == null) {
                return;
            }
            User[] usersArray = usersList.toArray(new User[usersList.size()]);
            userList.setListData(usersArray);
            userList.setCellRenderer(new UserCellRenderer());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,("Database error\n\nDetails:\n" + ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
