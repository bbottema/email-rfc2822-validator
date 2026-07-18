package demo;

import org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressParser;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;
import org.junit.Ignore;
import org.junit.Test;

import javax.mail.internet.InternetAddress;
import java.util.EnumSet;

import static java.util.EnumSet.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.ALLOW_DOT_IN_A_TEXT;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.ALLOW_PARENS_IN_LOCALPART;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.ALLOW_QUOTED_IDENTIFIERS;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.ALLOW_SQUARE_BRACKETS_IN_A_TEXT;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.RECOMMENDED;
import static org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria.RFC_COMPLIANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestClass {
	/**
	 * quick test some email addresses
	 * <p>
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

	@Test
	public void testEmailAddressThai(){
		assertEmail("email-test@universal-acceptance-test.international", true);
		assertEmail("email-test@universal-acceptance-test.icu", true);
		assertEmail("อีเมลทดสอบ@ยูเอทดสอบ.ไทย", true);
		assertEmail("อีเมลทดสอบ@ทีเอชนิค.องค์กร.ไทย", true);
	}
	
	@Test
	public void testAddressGithub18() {
		String email = "?UTF-8?Q?Gesellschaft_fC3BCr_Freiheitsrechte_e2EV=2E? <info@freiheitsrechte.org>";
		EnumSet<EmailAddressCriteria> criteria = of(ALLOW_SQUARE_BRACKETS_IN_A_TEXT, ALLOW_QUOTED_IDENTIFIERS);
		assertThat(EmailAddressValidator.isValid(email, criteria)).isTrue();
	}
	
	private static void assertEmail(String emailaddress, boolean expected) {
		assertThat(EmailAddressValidator.isValid(emailaddress, RFC_COMPLIANT))
				.as("assert %s is a valid address", emailaddress)
				.isEqualTo(expected);
	}
	
	@Test
	public void testIt() {
		InternetAddress address = EmailAddressParser.getInternetAddress("\"Bob\" <bob@hi.com>", RECOMMENDED, /* cfws */ true);
		assertThat(address.getPersonal()).isEqualTo("Bob");
		assertThat(address.getAddress()).isEqualTo("bob@hi.com");
	}
	
	@Test
	public void allowDotInaText() throws Exception {
		// these email address validations should normally fail
		assertFalse(EmailAddressValidator.isValid("Kayaks.org <kayaks@kayaks.org>"));
		
		// but with a configuration they could be allowed
		assertTrue(EmailAddressValidator.isValid("Kayaks.org <kayaks@kayaks.org>",
				EnumSet.of(ALLOW_DOT_IN_A_TEXT, ALLOW_QUOTED_IDENTIFIERS)));
		
	}
	
	@Test
	public void allowSquareBracketsInaText() throws Exception {
		// these email address validations should normally fail
		assertFalse(EmailAddressValidator.isValid("[Kayaks] <kayaks@kayaks.org>"));
		
		// but with a configuration they could be allowed
		assertTrue(EmailAddressValidator.isValid("[Kayaks] <kayaks@kayaks.org>",
				EnumSet.of(ALLOW_SQUARE_BRACKETS_IN_A_TEXT, ALLOW_QUOTED_IDENTIFIERS)));
		
	}
	
	@Test
	public void allowParensInLocalPart() throws Exception {
		// these email address validations should normally fail
		assertTrue(EmailAddressValidator.isValid("\"bob(hi)smith\"@test.com"));
		
		// but with a configuration they could be allowed
		assertTrue(EmailAddressValidator.isValid("\"bob(hi)smith\"@test.com",
				EnumSet.of(ALLOW_PARENS_IN_LOCALPART, ALLOW_QUOTED_IDENTIFIERS)));
	}
	
	@Test
	public void allowQuotedIdentifiers() throws Exception {
		// these email address validations should normally fail
		assertFalse(EmailAddressValidator.isValid("\"John Smith\" <john.smith@somewhere.com>",
				EnumSet.noneOf(EmailAddressCriteria.class)));
		
		// but with a configuration they could be allowed
		assertTrue(EmailAddressValidator.isValid("\"John Smith\" <john.smith@somewhere.com>",
				EnumSet.of(ALLOW_QUOTED_IDENTIFIERS)));
	}
	
	@Test
	public void testIllegalCharacters() {
		String email = "test Mail <noreply@testmail.com>";
		
		assertThat(EmailAddressParser.getAddressParts(email, RECOMMENDED, true))
				.containsExactlyInAnyOrder(
						"test Mail",
						"noreply",
						"testmail.com"
				);
	}
}