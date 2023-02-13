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

public class MainWindowForm {

    private JComboBox menuComboBox;
    private JPanel mainPanel;
    private JButton addNewButton;
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
    private JButton addBorrowerButton;
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

    // dependencies
    private final HashGenerator hashGenerator;
    private final EmailAddressChecker emailAddressChecker;
    private final LibraryDatabase libraryDB;

    public static void showMainWindow(
            HashGenerator hashGenerator,
            EmailAddressChecker emailAddressChecker,
            LibraryDatabase libraryDB)
    {
        assert hashGenerator != null;
        assert emailAddressChecker!= null;
        assert libraryDB != null;

        JFrame frame = new JFrame("Book Library Manager");
        MainWindowForm mainWindowForm = new MainWindowForm(hashGenerator, emailAddressChecker,libraryDB);
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
            LibraryDatabase libraryDB) // TODO: change to libraryDatabase
    { // constructor

        this.hashGenerator = hashGenerator;
        this.emailAddressChecker = emailAddressChecker;
        this.libraryDB = libraryDB;

        selectBooksBy();
        updateTotalMembers();
        updateTotalBooks();
        updateNumOfBooksBorrowed();
        updateNumOfBooksAvailable();

        addNewButton.addActionListener(e -> {
            if (menuComboBox.getSelectedItem() != null) {
                switch (menuComboBox.getSelectedItem().toString()) {
                    case "Books" -> createNewBook();
                    case "Members" -> createNewMember();
                    case "Users" -> createNewUser();
                }
            }
        });

        addBorrowerButton.addActionListener(e -> {

            try {

                if (titleLabel.getText().isBlank()) { // if nothing is selected
                    return;
                }

                if (libraryDB.noMembers()) {
                    JOptionPane.showMessageDialog(
                            null,
                            "There are no members in the database.",
                            "Cannot add borrower",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                if (Integer.parseInt(quantityLabel.getText()) - borrowerList.getModel().getSize() == 0) {
                    JOptionPane.showMessageDialog(
                            null,
                            "There are no available books.",
                            "Cannot add borrower",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                int bookID =  libraryDB.getBookID(
                        titleLabel.getText(),
                        authorLabel.getText(),
                        isbnLabel.getText(),
                        quantityLabel.getText()
                );

                Borrower newBorrower = BorrowerDialog.getBorrower(
                        BorrowerDialog.DialogType.CREATE,
                        -1, // TODO: does it have to be -1?
                        null,
                        bookID,
                        libraryDB
                );

                if (newBorrower != null) { // can be null if cancel pressed on BorrowerDialog
                    libraryDB.createBorrower(newBorrower);

                    displayBorrowers();
                    updateNumOfBooksBorrowed();
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
        });

        editBookButton.addActionListener(e -> {

            try {

                if (!titleLabel.getText().isBlank()) { // if a book is selected
                    Book editedBook = BookDialog.getBook(
                            BookDialog.DialogType.EDIT,
                            titleLabel.getText(),
                            authorLabel.getText(),
                            isbnLabel.getText(),
                            quantityLabel.getText()
                    );

                    if (editedBook != null) { // can be null if cancel pressed on BookDialog
                        libraryDB.updateBook(
                                editedBook.title(),
                                editedBook.author(),
                                String.valueOf(editedBook.isbn()),
                                String.valueOf(editedBook.quantity()),
                                libraryDB.getBookID(titleLabel.getText(),
                                        authorLabel.getText(),
                                        isbnLabel.getText(),
                                        quantityLabel.getText())
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
                            this.libraryDB
                    );

                    if (editedMember != null) { // can be null if cancel pressed on MemberDialog
                        libraryDB.updateMember(
                                editedMember.id(),
                                editedMember.name(),
                                editedMember.surname(),
                                editedMember.phoneNo(),
                                editedMember.email(),
                                editedMember.address(),
                                editedMember.postcode(),
                                Integer.parseInt(idLabel.getText())
                        );

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

                    // TODO: change!
                    String oldPassword = (libraryDB.selectUsersByUsername(usernameLabel.getText())).get(0).password();
                    User editedUser = UserDialog.getUser(
                            UserDialog.DialogType.EDIT,
                            usernameLabel.getText(),
                            fullNameLabel.getText(),
                            hashGenerator
                    );

                    if (editedUser != null) { // can be null if cancel pressed on UserDialog
                        if (editedUser.password() == null) { // TODO: check this change of removing setPassword works
                            libraryDB.updateUser(
                                    editedUser.username(),
                                    editedUser.fullName(),
                                    oldPassword,
                                    usernameLabel.getText()
                            );
                        } else {
                            libraryDB.updateUser(
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

                        int bookID = libraryDB.getBookID(
                                titleLabel.getText(),
                                authorLabel.getText(),
                                isbnLabel.getText(),
                                quantityLabel.getText()
                        );

                        if (libraryDB.selectBorrowersByBookID(bookID).isEmpty()) { // if this book is not being borrowed

                            libraryDB.deleteBook(
                                    libraryDB.getBookID(
                                            titleLabel.getText(),
                                            authorLabel.getText(),
                                            isbnLabel.getText(),
                                            quantityLabel.getText()
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
                        if (libraryDB.hasMemberBorrowedBook(Integer.parseInt(idLabel.getText()))) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Cannot delete this member." +
                                            "\nThis member is borrowing a book.",
                                    "Delete failed",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {

                            libraryDB.deleteMember(Integer.parseInt(idLabel.getText()));

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

                        libraryDB.deleteUser(usernameLabel.getText());

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
                if (bookList.getSelectedValue() != null) {
                    titleLabel.setText(bookList.getSelectedValue().title());
                    authorLabel.setText(bookList.getSelectedValue().author());
                    isbnLabel.setText(bookList.getSelectedValue().isbn());
                    quantityLabel.setText(String.valueOf(bookList.getSelectedValue().quantity()));
                    displayBorrowers();
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

                    String[] options = {"Edit", "Book returned"};
                    int answer = JOptionPane.showOptionDialog(
                            null,
                            "Choose an option",
                            "Edit / Returned",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options, // adds custom buttons
                            options[1] // stated default button
                    );

                    switch (answer) {
                        case 0 ->  // if "Edit" pressed
                                editBorrower();
                        case 1 ->  // if "Book returned" pressed
                                returnBook();
                    }
                }
            }
        });

        // TODO: change so that not everything is updated
        searchByComboBox.addActionListener(e -> { // updates list when the searchByComboBox value is changed
            selectBooksBy();
            selectMembersBy();
            selectUsersBy();
        });


        menuComboBox.addActionListener(e -> { /* sets specific values in model of searchByComboBox
                                                 when menuComboBox value is changed & changes type of list displayed */

            selectedParentCardPanel.removeAll();
            searchResultsParentCardPanel.removeAll();

            if (menuComboBox.getSelectedItem() != null) {

                String menuOption = menuComboBox.getSelectedItem().toString();
                switch (menuOption) {
                    case "Books" -> showBookCardPanel();
                    case "Members" -> showMemberCardPanel();
                    case "Users" -> showUserCardPanel();
                }

                selectedParentCardPanel.repaint();
                selectedParentCardPanel.revalidate();
            }
        });

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) { // TODO: change so that not everything is updated
                //super.keyTyped(e); // TODO: not sure why this line was added. might remove
                selectBooksBy();
                selectMembersBy();
                selectUsersBy();
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

            String fullName = this.libraryDB.selectMemberNameSurnameByMemberID(
                    borrowerList.getSelectedValue().memberID()
            );

            int bookReturned = JOptionPane.showConfirmDialog(
                    null,
                    "Has "+fullName+" returned "+titleLabel.getText()+"?",
                    "Book returned",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (bookReturned == 0) { // if yes pressed

                this.libraryDB.deleteBorrower(
                        borrowerList.getSelectedValue().bookID(),
                        borrowerList.getSelectedValue().memberID()
                );

                displayBorrowers();
                updateNumOfBooksBorrowed();
                updateNumOfBooksAvailable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    null,("Database error\n\nDetails:\n" + ex),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void editBorrower() {

        try {

            Borrower editedBorrower = BorrowerDialog.getBorrower(
                    BorrowerDialog.DialogType.EDIT,
                    borrowerList.getSelectedValue().memberID(),
                    borrowerList.getSelectedValue().returnDate(),
                    this.libraryDB.getBookID(
                            titleLabel.getText(),
                            authorLabel.getText(),
                            isbnLabel.getText(),
                            quantityLabel.getText()
                    ),
                    this.libraryDB
            );

            if (editedBorrower != null) { // can be null if cancel pressed on BorrowerDialog

                this.libraryDB.updateBorrower(
                        editedBorrower.memberID(),
                        editedBorrower.returnDate(),
                        this.libraryDB.getBookID(titleLabel.getText(),
                                authorLabel.getText(),
                                isbnLabel.getText(),
                                quantityLabel.getText()),
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
            ); // TODO: "get" for other entities

            if (newUser != null) { // can be null if cancel pressed on UserDialog

                this.libraryDB.createUser(newUser);

                JOptionPane.showMessageDialog(
                        null,
                        "User created successfully.",
                        "User create",
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
                    -1, // TODO: does it have to be -1
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    this.emailAddressChecker,
                    this.libraryDB
            );

            if (newMember != null) { // can be null if cancel pressed on MemberDialog

                this.libraryDB.createMember(newMember);

                JOptionPane.showMessageDialog(
                        null,
                        "Member created successfully.",
                        "Member create",
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
                    null
            );

            if (newBook != null) { // can be null if cancel pressed on BookDialog

                libraryDB.createBook(newBook);

                JOptionPane.showMessageDialog(
                        null,
                        "Book created successfully.",
                        "Book create",
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

            if (searchByComboBox.getSelectedItem() != null) {

                String searchBy = searchByComboBox.getSelectedItem().toString();
                List<Book> booksList = switch (searchBy) {
                    case "Title" -> this.libraryDB.selectBooksByTitle(searchTextField.getText());
                    case "Author" -> this.libraryDB.selectBooksByAuthor(searchTextField.getText());
                    case "ISBN" -> this.libraryDB.selectBooksByISBN(searchTextField.getText());
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
                    case "Name" -> this.libraryDB.selectMembersByName(searchTextField.getText());
                    case "Surname" -> this.libraryDB.selectMembersBySurname(searchTextField.getText());
                    case "Phone No" -> this.libraryDB.selectMembersByPhoneNo(searchTextField.getText());
                    case "Email" -> this.libraryDB.selectMembersByEmail(searchTextField.getText());
                    case "Address" -> this.libraryDB.selectMembersByAddress(searchTextField.getText());
                    case "Postcode" -> this.libraryDB.selectMembersByPostcode(searchTextField.getText());
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

            int bookID = this.libraryDB.getBookID(
                    titleLabel.getText(),
                    authorLabel.getText(),
                    isbnLabel.getText(),
                    quantityLabel.getText()
            );
            List<Borrower> borrowersList = this.libraryDB.selectBorrowersByBookID(bookID); // TODO: can it start off as an array instead of a model?

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
                    case "Username" -> this.libraryDB.selectUsersByUsername(searchTextField.getText());
                    case "Full Name" -> this.libraryDB.selectUsersByFullName(searchTextField.getText());
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
            int totalMembers = this.libraryDB.countMembers();
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
            int totalBooks = this.libraryDB.countBooks();
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
            int numOfBooksBorrowed = this.libraryDB.countBorrowers();
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
            int totalBooks = this.libraryDB.countBooks();
            int numOfBooksBorrowed = this.libraryDB.countBorrowers();
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