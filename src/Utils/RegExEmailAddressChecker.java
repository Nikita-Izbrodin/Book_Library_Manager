package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for validation of an email address.
 * Implemented using a regular expression.
 */
public class RegExEmailAddressChecker implements EmailAddressChecker {

    // TODO: detailed explanation what does this regex do!
    private final Pattern pattern;

    public RegExEmailAddressChecker() { // TODO: update comment
        /*
         * The following regular expression defines a pattern that matches any string that:
         * - has '@' in the middle
         * - start with ...
         * - asa
         */
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean isValidEmailAddress(String emailAddress) {
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

}
