package demo;

import org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressParser;

public class TestClass {
	/* quick test some email addresses
	 * </p>
	 * lists taken from: http://stackoverflow.com/a/297494/441662 and http://haacked.com/archive/2007/08/21/i-knew-how-to-validate-an-email-address-until-i.aspx/
	 */
	public static void main(String[] args) {
		assertEmail("me@example.com", true);
		assertEmail("a.nonymous@example.com", true);
		assertEmail("name+tag@example.com", true);
		assertEmail("!#$%&'+-/=.?^`{|}~@[1.0.0.127]", true);
		assertEmail("!#$%&'+-/=.?^`{|}~@[IPv6:0123:4567:89AB:CDEF:0123:4567:89AB:CDEF]", true);
		assertEmail("me(this is a comment)@example.com", true); // comments are discouraged but not prohibited by RFC2822.
		assertEmail("me.example@com", true);
		assertEmail("309d4696df38ff12c023600e3bc2bd4b@fakedomain.com", true);
		assertEmail("ewiuhdghiufduhdvjhbajbkerwukhgjhvxbhvbsejskuadukfhgskjebf@gmail.net", true);

		assertEmail("NotAnEmail", false);
		assertEmail("me@", false);
		assertEmail("@example.com", false);
		assertEmail(".me@example.com", false);
		assertEmail("me@example..com", false);
		assertEmail("me\\@example.com", false);

		assertEmail("\"ßoµ\" <notifications@example.com>", false);

		assertParseable("\"ßoµ\" <notifications@example.com>", false);
		assertParseable("\"ßoµ\" <notifications@example.com>".replaceAll("[^\\x00-\\x7F]", ""), true);

		System.out.println("yay, validations successful!");
	}

	private static void assertEmail(String emailaddress, boolean expected) {
		final boolean isValid = EmailAddressValidator.isValid(emailaddress, EmailAddressCriteria.RFC_COMPLIANT);
		if (isValid != expected) {
			throw new IllegalArgumentException(String.format("%s (expected: %s, but was: %s)", emailaddress, expected, isValid));
		}
	}

	private static void assertParseable(String emailaddress, boolean expected) {
		String [] parts = EmailAddressParser.getAddressParts(emailaddress, EmailAddressCriteria.RFC_COMPLIANT, false);
		if (parts == null || parts.length <= 0) {
			if (expected) throw new IllegalArgumentException(String.format("%s (expected: %s, but was: %s)", emailaddress, expected, false));
			else return;
		}

		if (!expected)
			throw new IllegalArgumentException(String.format("%s (expected: %s, but was: %s)", emailaddress, expected, true));
	}

}
