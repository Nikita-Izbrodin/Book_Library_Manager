package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for validation of an email address.
 * Implemented using a regular expression.
 */
public class RegExEmailAddressChecker implements EmailAddressChecker {

    private final Pattern pattern;

    public RegExEmailAddressChecker() {
        /*
         * The following regular expression defines a pattern that matches any string that follows these rules:
         * - has one '@'
         * - all characters permitted by RFC-5322 are allowed (https://www.rfc-editor.org/rfc/rfc5322)
         * - the domain name must include at least one dot
         * - both the local part and the domain name can contain one or more dots
         * - no two dots can appear right next to each other
         * - first and last characters in the local part and in the domain name must not be dots
         * - the part of the domain name after the last dot can only consist of letters
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