package Forms;

import Entities.Member;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class MemberListItemForm {
    private JPanel memberItemPanel;
    private JLabel nameLabel;
    private JLabel surnameLabel;
    private JLabel phoneNoLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;
    private JLabel postcodeLabel;
    private JLabel idLabel;

    public JPanel getPanel(){
        return memberItemPanel;
    }

    public void setData(Member member) {
        idLabel.setText("ID: "+member.getID());
        nameLabel.setText("Name: "+member.getName());
        surnameLabel.setText("Surname: "+member.getSurname());
        phoneNoLabel.setText("Phone No: "+member.getPhoneNo());
        emailLabel.setText("Email: "+member.getEmail());
        addressLabel.setText("Address: "+member.getAddress());
        postcodeLabel.setText("Postcode: "+member.getPostcode());
    }

}
