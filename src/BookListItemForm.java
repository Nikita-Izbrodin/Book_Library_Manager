import javax.swing.JLabel;
import javax.swing.JPanel;

public class BookListItemForm {
    private JPanel bookItemPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel isbnLabel;
    private JLabel quantityLabel;

    public JPanel getPanel() {
        return bookItemPanel;
    }

    public void setData(Book entry) {
        titleLabel.setText("Title: "+ entry.getTitle());
        authorLabel.setText("Author: "+ entry.getAuthor());
        isbnLabel.setText("ISBN: "+ entry.getIsbn());
        quantityLabel.setText("Quantity: "+ entry.getQuantity());
    }

}
