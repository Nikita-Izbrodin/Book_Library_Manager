import javax.swing.*;

public class memberListitemForm {
    private JPanel memberItemPanel;
    private JLabel nameLabel;
    private JLabel surnameLabel;
    private JLabel phoneNoLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;
    private JLabel postcodeLabel;

    public JPanel getPanel(){
        return memberItemPanel;
    }

    public void setData(Member member) {
        nameLabel.setText("Name: "+member.getName());
        surnameLabel.setText("Surname: "+member.getSurname());
        phoneNoLabel.setText("Phone No: "+member.getPhoneNo());
        emailLabel.setText("Email: "+member.getEmail());
        addressLabel.setText("Address: "+member.getAddress());
        postcodeLabel.setText("Postcode: "+member.getPostcode());
    }

}
