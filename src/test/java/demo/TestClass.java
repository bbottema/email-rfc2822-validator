package demo;

import org.hazlewood.connor.bottema.emailaddress.EmailAddressParser;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.RFC_COMPLIANT;

public class TestClass {
	/* quick test some email addresses
	 * </p>
	 * lists taken from: http://stackoverflow.com/a/297494/441662 and http://haacked.com/archive/2007/08/21/i-knew-how-to-validate-an-email-address-until-i.aspx/
	 */
	@Test
	public void testEmailAddresses() {
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
		assertThat(EmailAddressParser.getAddressParts("\"ßoµ\" <notifications@example.com>", RFC_COMPLIANT, false)).isNullOrEmpty();
		String emailaddress = "\"ßoµ\" <notifications@example.com>".replaceAll("[^\\x00-\\x7F]", "");
		assertThat(EmailAddressParser.getAddressParts(emailaddress, RFC_COMPLIANT, false)).isNotEmpty();
	}
	
	private static void assertEmail(String emailaddress, boolean expected) {
		assertThat(EmailAddressValidator.isValid(emailaddress, RFC_COMPLIANT))
				.as("assert %s is a valid address", emailaddress)
				.isEqualTo(expected);
	}
	
}