package Forms;

import CellRenderers.BookCellRenderer;
import CellRenderers.BorrowerCellRenderer;
import CellRenderers.MemberCellRenderer;
import CellRenderers.UserCellRenderer;
import Dialogs.BookDialog;
import Dialogs.BorrowerDialog;
import Dialogs.MemberDialog;
import Dialogs.UserDialog;
import Entities.Book;
import Entities.Borrower;
import Entities.Member;
import Entities.User;
import Utils.EmailAddressChecker;
import Utils.HashGenerator;
import DataAccess.LibraryDatabase;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class responsible for displaying the main window and handling the majority of interactions with the user.
 */
public class MainWindowForm {

    private JComboBox menuComboBox;
    private JPanel mainPanel;
    private JButton CreateNewButton;
    private JComboBox searchByComboBox;
    private JTextField searchTextField;
    private JList<Book> bookList;
    private JPanel searchResultsParentCardPanel;
    private JPanel selectedParentCardPanel;
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
    private JButton createBorrowerButton;
    private JLabel idLabel;
    private JButton deleteUserButton;
    private JButton editUserButton;
    private JList<User> userList;
    private JLabel usernameLabel;
    private JLabel fullNameLabel;
    private JLabel totalMembersLabel;
    private JLabel totalBooksLabel;
    private JLabel numOfBooksAvailableLabel;
    private JLabel numOfBooksBorrowedLabel;
    private JLabel numOfSelectedBookAvailable;
    private JLabel numOfSelectedBookBorrowed;

    // dependencies
    private final HashGenerator hashGenerator;
    private final EmailAddressChecker emailAddressChecker;
    private final LibraryDatabase libraryDatabase;

    public static void showMainWindow(
            HashGenerator hashGenerator,
            EmailAddressChecker emailAddressChecker,
            LibraryDatabase libraryDatabase)
    {
        assert hashGenerator != null;
        assert emailAddressChecker!= null;
        assert libraryDatabase != null;

        JFrame frame = new JFrame("Book Library Manager");
        MainWindowForm mainWindowForm = new MainWindowForm(hashGenerator, emailAddressChecker,libraryDatabase);
        frame.setContentPane(mainWindowForm.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1000, 800));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public MainWindowForm(
            HashGenerator hashGenerator,
            EmailAddressChecker emailAddressChecker,
            LibraryDatabase libraryDatabase)
    { // constructor

        this.hashGenerator = hashGenerator;
        this.emailAddressChecker = emailAddressChecker;
        this.libraryDatabase = libraryDatabase;

        selectBooksBy();
        updateTotalMembers();
        updateTotalBooks();
        updateNumOfBooksBorrowed();
        updateNumOfBooksAvailable();

        CreateNewButton.addActionListener(e -> {
            if (menuComboBox.getSelectedItem() != null) {
                switch (menuComboBox.getSelectedItem().toString()) {
                    case "Books" -> createNewBook();
                    case "Members" -> createNewMember();
                    case "Users" -> createNewUser();
                }
            }
        });

        createBorrowerButton.addActionListener(e -> {

            try {

                if (!titleLabel.getText().isBlank()) { // if a book is selected

                    if (libraryDatabase.noMembers()) {
                        JOptionPane.showMessageDialog(
                                null,
                                "There are no members in the database.",
                                "Cannot create borrower",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                        return;
                    }

                    if (Integer.parseInt(quantityLabel.getText()) - borrowerList.getModel().getSize() == 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "There are no available books.",
                                "Cannot create borrower",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    int bookID =  libraryDatabase.getBookID(
                            titleLabel.getText(),
                            authorLabel.getText(),
                            isbnLabel.getText()
                    );

                    Borrower newBorrower = BorrowerDialog.getBorrower(
                            BorrowerDialog.DialogType.CREATE,
                            -1,
                            null,
                            bookID,
                            libraryDatabase
                    );

                    if (newBorrower != null) { // can be null if cancel pressed on BorrowerDialog
                        libraryDatabase.createBorrower(newBorrower);

                        displayBorrowers();
                        updateStatisticsOnSelectedBook(this.libraryDatabase);
                        updateNumOfBooksBorrowed();
                        updateNumOfBooksAvailable();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        editBookButton.addActionListener(e -> {

            try {

                if (!titleLabel.getText().isBlank()) { // if a book is selected
                    Book editedBook = BookDialog.getBook(
                            BookDialog.DialogType.EDIT,
                            titleLabel.getText(),
                            authorLabel.getText(),
                            isbnLabel.getText(),
                            quantityLabel.getText(),
                            libraryDatabase
                    );

                    if (editedBook != null) { // can be null if cancel pressed on BookDialog
                        libraryDatabase.updateBook(
                                editedBook,
                                libraryDatabase.getBookID(
                                        titleLabel.getText(),
                                        authorLabel.getText(),
                                        isbnLabel.getText()
                                )
                        );

                        JOptionPane.showMessageDialog(
                                null,
                                "Book updated successfully.",
                                "Book update",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        titleLabel.setText(editedBook.title());
                        authorLabel.setText(editedBook.author());
                        isbnLabel.setText(String.valueOf(editedBook.isbn()));
                        quantityLabel.setText(String.valueOf(editedBook.quantity()));

                        updateStatisticsOnSelectedBook(this.libraryDatabase);
                        selectBooksBy();
                        updateTotalBooks();
                        updateNumOfBooksAvailable();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        editMemberButton.addActionListener(e -> {

            try {

                if (!nameLabel.getText().isBlank()) { // if a member is selected

                    Member editedMember = MemberDialog.getMember(
                            MemberDialog.DialogType.EDIT,
                            Integer.parseInt(idLabel.getText()),
                            nameLabel.getText(),
                            surnameLabel.getText(),
                            phoneNoLabel.getText(),
                            emailLabel.getText(),
                            addressLabel.getText(),
                            postcodeLabel.getText(),
                            emailAddressChecker,
                            this.libraryDatabase
                    );

                    if (editedMember != null) { // can be null if cancel pressed on MemberDialog
                        libraryDatabase.updateMember(editedMember, Integer.parseInt(idLabel.getText()));

                        JOptionPane.showMessageDialog(
                                null,
                                "Member updated successfully.",
                                "Member update",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        idLabel.setText(String.valueOf(editedMember.id()));
                        nameLabel.setText(editedMember.name());
                        surnameLabel.setText(editedMember.surname());
                        phoneNoLabel.setText(editedMember.phoneNo());
                        emailLabel.setText(editedMember.email());
                        addressLabel.setText(editedMember.address());
                        postcodeLabel.setText(editedMember.postcode());

                        selectMembersBy();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        editUserButton.addActionListener(e -> {

            try {

                if (!usernameLabel.getText().isBlank()) { // if a user is selected

                    String oldPassword = (
                            libraryDatabase.selectUsersByUsername(usernameLabel.getText())
                    ).get(0).password();

                    User editedUser = UserDialog.getUser(
                            UserDialog.DialogType.EDIT,
                            usernameLabel.getText(),
                            fullNameLabel.getText(),
                            hashGenerator
                    );

                    if (editedUser != null) { // can be null if cancel pressed on UserDialog
                        if (editedUser.password() == null) {
                            libraryDatabase.updateUser(
                                    editedUser.username(),
                                    editedUser.fullName(),
                                    oldPassword,
                                    usernameLabel.getText()
                            );
                        } else {
                            libraryDatabase.updateUser(
                                    editedUser.username(),
                                    editedUser.fullName(),
                                    editedUser.password(),
                                    usernameLabel.getText()
                            );
                        }

                        JOptionPane.showMessageDialog(
                                null,
                                "User updated successfully.",
                                "User update",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        usernameLabel.setText(editedUser.username());
                        fullNameLabel.setText(editedUser.fullName());

                        selectUsersBy();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        ("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteBookButton.addActionListener(e -> {

            try {

                if (!titleLabel.getText().isBlank()) { // if a book is selected
                    int answer = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to delete this book?" +
                            "\nTitle: "+titleLabel.getText() +
                            "\nAuthor: "+authorLabel.getText() +
                            "\nISBN: "+isbnLabel.getText() +
                            "\nQuantity: "+quantityLabel.getText(),
                            "Delete book", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                    );

                    if (answer == 0) { // if yes pressed

                        int bookID = libraryDatabase.getBookID(
                                titleLabel.getText(),
                                authorLabel.getText(),
                                isbnLabel.getText()
                        );

                        if (libraryDatabase.selectBorrowersByBookID(bookID).isEmpty()) { // if this book is not being borrowed

                            libraryDatabase.deleteBook(
                                    libraryDatabase.getBookID(
                                            titleLabel.getText(),
                                            authorLabel.getText(),
                                            isbnLabel.getText()
                                    )
                            );

                            titleLabel.setText("");
                            authorLabel.setText("");
                            isbnLabel.setText("");
                            quantityLabel.setText("");

                            selectBooksBy();
                            displayBorrowers();
                            updateTotalBooks();
                            updateNumOfBooksAvailable();
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Cannot delete this book.\nThis book is being borrowed.",
                                    "Delete failed",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteMemberButton.addActionListener(e -> {

            try {

                if (!nameLabel.getText().isBlank()) { // if a member is selected

                    int answer = JOptionPane.showConfirmDialog(
                            null,
                            "Are you sure you want to delete this member?" +
                            "\nName: "+nameLabel.getText() +
                            "\nSurname: "+surnameLabel.getText() +
                            "\nPhone No: "+phoneNoLabel.getText() +
                            "\nEmail: "+emailLabel.getText() +
                            "\nAddress: "+addressLabel.getText() +
                            "\nPostcode: "+postcodeLabel.getText(),
                            "Delete member",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

                    if (answer == 0) { // if yes pressed
                        if (libraryDatabase.hasMemberBorrowedBook(Integer.parseInt(idLabel.getText()))) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Cannot delete this member." +
                                    "\nThis member is borrowing a book.",
                                    "Delete failed",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {

                            libraryDatabase.deleteMember(Integer.parseInt(idLabel.getText()));

                            idLabel.setText("");
                            nameLabel.setText("");
                            surnameLabel.setText("");
                            phoneNoLabel.setText("");
                            emailLabel.setText("");
                            addressLabel.setText("");
                            postcodeLabel.setText("");

                            selectMembersBy();
                            updateTotalMembers();
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null,("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteUserButton.addActionListener(e -> {

            try {

                if (!usernameLabel.getText().isBlank()) { // if a user is selected
                    int answer = JOptionPane.showConfirmDialog(
                            null, "Are you sure you want to delete this user?" +
                            "\nUsername: "+usernameLabel.getText() +
                            "\nFull Name: "+fullNameLabel.getText(),
                            "Delete user",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                    );

                    if (answer == 0) { // if yes pressed

                        libraryDatabase.deleteUser(usernameLabel.getText());

                        usernameLabel.setText("");
                        fullNameLabel.setText("");

                        selectUsersBy();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        null, ("Database error\n\nDetails:\n" + ex),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        bookList.addMouseListener(new MouseAdapter() { // when item in book list is selected with cursor
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                try {
                    if (bookList.getSelectedValue() != null) {

                        titleLabel.setText(bookList.getSelectedValue().title());
                        authorLabel.setText(bookList.getSelectedValue().author());
                        isbnLabel.setText(bookList.getSelectedValue().isbn());
                        quantityLabel.setText(String.valueOf(bookList.getSelectedValue().quantity()));

                        updateStatisticsOnSelectedBook(libraryDatabase);

                        displayBorrowers();
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        memberList.addMouseListener(new MouseAdapter() { // when item in member list is selected with cursor
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (memberList.getSelectedValue() != null) {
                    idLabel.setText(String.valueOf(memberList.getSelectedValue().id()));
                    nameLabel.setText(memberList.getSelectedValue().name());
                    surnameLabel.setText(memberList.getSelectedValue().surname());
                    phoneNoLabel.setText(memberList.getSelectedValue().phoneNo());
                    emailLabel.setText(memberList.getSelectedValue().email());
                    addressLabel.setText(memberList.getSelectedValue().address());
                    postcodeLabel.setText(memberList.getSelectedValue().postcode());
                }
            }
        });

        userList.addMouseListener(new MouseAdapter() { // when item in user list is selected with cursor
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (userList.getSelectedValue() != null) {
                    usernameLabel.setText(userList.getSelectedValue().username());
                    fullNameLabel.setText(userList.getSelectedValue().fullName());
                }
            }
        });

        borrowerList.addMouseListener(new MouseAdapter() { // when item in borrower list is selected with cursor
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (borrowerList.getSelectedValue() != null) {

                    String[] options = {"Edit", "Book returned", "Cancel"};
                    int answer = JOptionPane.showOptionDialog(
                            null,
                            "Choose an option",
                            "Edit / Returned",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options, // adds custom buttons
                            options[2] // stated default button
                    );

                    switch (answer) {
                        case 0 ->  // if "Edit" pressed
                                editBorrower();
                        case 1 ->  // if "Book returned" pressed
                                returnBook();
                        case 2 -> // if "Cancel" pressed
                                {} // does nothing and dialog is closed
                    }
                }
            }
        });

        searchByComboBox.addActionListener(e -> { // updates list when the searchByComboBox value is changed

            assert menuComboBox.getSelectedItem() != null; /* method invocation 'toString' in switch expression
                                                              may produce NullPointerException if
                                                              menuComboBox.getSelectedItem() is null */
            switch (menuComboBox.getSelectedItem().toString()) {
                case "Books" -> selectBooksBy();
                case "Members" -> selectMembersBy();
                case "Users" -> selectUsersBy();
            }
        });


        menuComboBox.addActionListener(e -> { /* sets specific values in model of searchByComboBox
                                                 when menuComboBox value is changed & changes type of list displayed */

            selectedParentCardPanel.removeAll();
            searchResultsParentCardPanel.removeAll();

            if (menuComboBox.getSelectedItem() != null) {

                String menuOption = menuComboBox.getSelectedItem().toString();
                switch (menuOption) {
                    case "Books" -> {
                        showBookCardPanel();
                        CreateNewButton.setText("Create New Book");
                    }
                    case "Members" -> {
                        showMemberCardPanel();
                        CreateNewButton.setText("Create New Member");
                    }
                    case "Users" -> {
                        showUserCardPanel();
                        CreateNewButton.setText("Create New User");
                    }
                }
                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });

        searchTextField.addKeyListener(new KeyAdapter() { // updates list when user types in search bar
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);

                assert menuComboBox.getSelectedItem() != null; /* method invocation 'toString' in switch expression
                                                              may produce NullPointerException if
                                                              menuComboBox.getSelectedItem() is null */
                switch (menuComboBox.getSelectedItem().toString()) {
                    case "Books" -> selectBooksBy();
                    case "Members" -> selectMembersBy();
                    case "Users" -> selectUsersBy();
                }
            }
        });
    }

    private void showUserCardPanel() {

        selectedParentCardPanel.add(userCardPanel);
        searchResultsParentCardPanel.add(userSearchCardPanel);

        DefaultComboBoxModel userModel = (DefaultComboBoxModel) searchByComboBox.getModel();
        userModel.removeAllElements();
        ArrayList<String> types = new ArrayList<>(Arrays.asList(
                "Username",
                "Full Name"
        ));
        for (String type : types) {
            userModel.addElement(type);
        }
        searchByComboBox.setModel(userModel);

        selectUsersBy();
    }

    private void showMemberCardPanel() {

        selectedParentCardPanel.add(memberCardPanel);
        searchResultsParentCardPanel.add(memberSearchCardPanel);

        DefaultComboBoxModel memberModel = (DefaultComboBoxModel) searchByComboBox.getModel();
        memberModel.removeAllElements();
        ArrayList<String> types = new ArrayList<>(Arrays.asList(
                "Name",
                "Surname",
                "Phone No",
                "Email",
                "Address",
                "Postcode"
        ));
        for (String type : types) {
            memberModel.addElement(type);
        }
        searchByComboBox.setModel(memberModel);

        selectMembersBy();
    }

    private void showBookCardPanel() {

        selectedParentCardPanel.add(bookCardPanel);
        searchResultsParentCardPanel.add(bookSearchCardPanel);

        DefaultComboBoxModel bookModel = (DefaultComboBoxModel) searchByComboBox.getModel();
        bookModel.removeAllElements();
        ArrayList<String> types = new ArrayList<>(Arrays.asList(
                "Title",
                "Author",
                "ISBN"
        ));
        for (String type : types) {
            bookModel.addElement(type);
        }
        searchByComboBox.setModel(bookModel);

        selectBooksBy();
    }

    private void returnBook() {

        try {

            String fullName = this.libraryDatabase.selectMemberNameSurnameByMemberID(
                    borrowerList.getSelectedValue().memberID()
            );

            int bookReturned = JOptionPane.showConfirmDialog(
                    null,
                    "Has "+fullName+" returned "+titleLabel.getText()+"?",
                    "Book returned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (bookReturned == 0) { // if yes pressed

                this.libraryDatabase.deleteBorrower(
                        borrowerList.getSelectedValue().bookID(),
                        borrowerList.getSelectedValue().memberID()
                );

                displayBorrowers();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();

                updateStatisticsOnSelectedBook(libraryDatabase);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateStatisticsOnSelectedBook(LibraryDatabase libraryDatabase) throws SQLException {
        int bookID = libraryDatabase.getBookID(
                titleLabel.getText(),
                authorLabel.getText(),
                isbnLabel.getText()
        );

        numOfSelectedBookAvailable.setText(
                String.valueOf(Integer.parseInt(quantityLabel.getText()) -
                        libraryDatabase.selectBorrowersByBookID(bookID).size())
        );
        numOfSelectedBookBorrowed.setText(
                String.valueOf(libraryDatabase.selectBorrowersByBookID(bookID).size())
        );
    }

    private void editBorrower() {

        try {

            Borrower editedBorrower = BorrowerDialog.getBorrower(
                    BorrowerDialog.DialogType.EDIT,
                    borrowerList.getSelectedValue().memberID(),
                    borrowerList.getSelectedValue().returnDate(),
                    this.libraryDatabase.getBookID(
                            titleLabel.getText(),
                            authorLabel.getText(),
                            isbnLabel.getText()
                    ),
                    this.libraryDatabase
            );

            if (editedBorrower != null) { // can be null if cancel pressed on BorrowerDialog

                this.libraryDatabase.updateBorrower(
                        editedBorrower.memberID(),
                        editedBorrower.returnDate(),
                        this.libraryDatabase.getBookID(
                                titleLabel.getText(),
                                authorLabel.getText(),
                                isbnLabel.getText()
                        ),
                        borrowerList.getSelectedValue().memberID()
                );

                JOptionPane.showMessageDialog(
                        null,
                        "Borrow updated successfully.",
                        "Borrow update",
                        JOptionPane.INFORMATION_MESSAGE
                );

                displayBorrowers();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createNewUser() {

        try {

            User newUser = UserDialog.getUser(
                    UserDialog.DialogType.CREATE,
                    null,
                    null,
                    this.hashGenerator
            );

            if (newUser != null) { // can be null if cancel pressed on UserDialog

                this.libraryDatabase.createUser(newUser);

                JOptionPane.showMessageDialog(
                        null,
                        "User created successfully.",
                        "Create user",
                        JOptionPane.INFORMATION_MESSAGE
                );

                selectUsersBy();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                null,
                ("Database error\n\nDetails:\n" + ex),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createNewMember() {

        try {

            Member newMember = MemberDialog.getMember(
                    MemberDialog.DialogType.CREATE,
                    -1,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    this.emailAddressChecker,
                    this.libraryDatabase
            );

            if (newMember != null) { // can be null if cancel pressed on MemberDialog

                this.libraryDatabase.createMember(newMember);

                JOptionPane.showMessageDialog(
                        null,
                        "Member created successfully.",
                        "Create member",
                        JOptionPane.INFORMATION_MESSAGE
                );

                selectMembersBy();
                updateTotalMembers();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void createNewBook() {

        try {

            Book newBook = BookDialog.getBook(
                    BookDialog.DialogType.CREATE,
                    null,
                    null,
                    null,
                    null,
                    libraryDatabase
            );

            if (newBook != null) { // can be null if cancel pressed on BookDialog

                libraryDatabase.createBook(newBook);

                JOptionPane.showMessageDialog(
                        null,
                        "Book created successfully.",
                        "Create book",
                        JOptionPane.INFORMATION_MESSAGE
                );

                selectBooksBy();
                updateTotalBooks();
                updateNumOfBooksAvailable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void selectBooksBy() { // updates list of displayed books

        try {

            if (searchByComboBox.getSelectedItem() != null) { // TODO: maybe change to assert

                String searchBy = searchByComboBox.getSelectedItem().toString();
                List<Book> booksList = switch (searchBy) {
                    case "Title" -> this.libraryDatabase.selectBooksByTitle(searchTextField.getText());
                    case "Author" -> this.libraryDatabase.selectBooksByAuthor(searchTextField.getText());
                    case "ISBN" -> this.libraryDatabase.selectBooksByISBN(searchTextField.getText());
                    default -> null;
                };

                if (booksList != null) {
                    Book[] booksArray = booksList.toArray(new Book[0]);
                    bookList.setListData(booksArray);
                    bookList.setCellRenderer(new BookCellRenderer());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void selectMembersBy() { // updates list of displayed members

        try {

            if (searchByComboBox.getSelectedItem() != null) {

                String searchBy = searchByComboBox.getSelectedItem().toString();
                List<Member> membersList = switch (searchBy) {
                    case "Name" -> this.libraryDatabase.selectMembersByName(searchTextField.getText());
                    case "Surname" -> this.libraryDatabase.selectMembersBySurname(searchTextField.getText());
                    case "Phone No" -> this.libraryDatabase.selectMembersByPhoneNo(searchTextField.getText());
                    case "Email" -> this.libraryDatabase.selectMembersByEmail(searchTextField.getText());
                    case "Address" -> this.libraryDatabase.selectMembersByAddress(searchTextField.getText());
                    case "Postcode" -> this.libraryDatabase.selectMembersByPostcode(searchTextField.getText());
                    default -> null;
                };

                if (membersList != null) {
                    Member[] membersArray = membersList.toArray(new Member[0]);
                    memberList.setListData(membersArray);
                    memberList.setCellRenderer(new MemberCellRenderer());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void displayBorrowers() { // updates list of displayed borrowers

        try {

            int bookID = this.libraryDatabase.getBookID(
                    titleLabel.getText(),
                    authorLabel.getText(),
                    isbnLabel.getText()
            );
            List<Borrower> borrowersList = this.libraryDatabase.selectBorrowersByBookID(bookID); // TODO: can it start off as an array instead of a model?

            if (borrowersList != null) {
                Borrower[] borrowersArray = borrowersList.toArray(new Borrower[0]);
                borrowerList.setListData(borrowersArray);
                borrowerList.setCellRenderer(new BorrowerCellRenderer());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void selectUsersBy() { // updates list of displayed users

        try {

            if (searchByComboBox.getSelectedItem() != null) {

                String searchBy = searchByComboBox.getSelectedItem().toString();
                List<User> usersList = switch (searchBy) {
                    case "Username" -> this.libraryDatabase.selectUsersByUsername(searchTextField.getText());
                    case "Full Name" -> this.libraryDatabase.selectUsersByFullName(searchTextField.getText());
                    default -> null;
                };

                if (usersList != null) {
                    User[] usersArray = usersList.toArray(new User[0]);
                    userList.setListData(usersArray);
                    userList.setCellRenderer(new UserCellRenderer());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTotalMembers() {
        try {
            int totalMembers = this.libraryDatabase.countMembers();
            totalMembersLabel.setText(String.valueOf(totalMembers));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateTotalBooks() {
        try {
            int totalBooks = this.libraryDatabase.countBooks();
            totalBooksLabel.setText(String.valueOf(totalBooks));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateNumOfBooksBorrowed() {
        try {
            int numOfBooksBorrowed = this.libraryDatabase.countBorrowers();
            numOfBooksBorrowedLabel.setText(String.valueOf(numOfBooksBorrowed));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void updateNumOfBooksAvailable() {
        try {
            int totalBooks = this.libraryDatabase.countBooks();
            int numOfBooksBorrowed = this.libraryDatabase.countBorrowers();
            numOfBooksAvailableLabel.setText(String.valueOf(totalBooks - numOfBooksBorrowed));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,
                    ("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}