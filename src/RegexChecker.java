import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexChecker {

    private final String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private boolean match;

    public RegexChecker(String email) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        match = matcher.matches();
    }

    public boolean matches() {
        return match;
    }
}
