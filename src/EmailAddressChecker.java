import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAddressChecker {

    private final String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private Pattern pattern;

    public EmailAddressChecker() {
        pattern = Pattern.compile(regex);
    }

    public boolean isValidEmailAddress(String emailAddress) {
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

}
