package Forms;

import Entities.Member;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

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

    protected void setBackground(boolean isSelected) {
        memberItemPanel.setBackground(isSelected ? Color.blue : Color.lightGray);

        idLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        nameLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        surnameLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        phoneNoLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        emailLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        addressLabel.setForeground(isSelected ? Color.white : Color.BLACK);
        postcodeLabel.setForeground(isSelected ? Color.white : Color.BLACK);
    }
}
