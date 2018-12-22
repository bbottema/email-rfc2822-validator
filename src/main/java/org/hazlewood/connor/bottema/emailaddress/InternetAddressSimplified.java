package org.hazlewood.connor.bottema.emailaddress;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BEncoderStream;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import static java.lang.String.format;


/**
 * Replacement for {@code javax.mail.internet.InternetAddress}, making the entire javax.mail dependency obsolete.
 * <p>
 * This class only replicates the encoding part (as-is) of the original InternetAddress and MimeUtility classes, which is the only functionality
 * used of the entire javax.mail library in the rfc2822 validation library.
 */
class InternetAddressSimplified {
	
	private static final int ALL_ASCII = 1;
	private static final int MOSTLY_ASCII = 2;
	private static final int MOSTLY_NONASCII = 3;
	private static Boolean foldEncodedWords;
	private static String defaultMIMECharset;
	
	private final String personal;
	private final String address;
	
	InternetAddressSimplified(String personal, String address) throws UnsupportedEncodingException {
		this.personal = personal;
		this.address = address;
		if (this.personal != null) {
			// the encoded personal is assigned but never actually used in the original
			// InternetAddress class (except in toString()).
			verifyEncoding(personal);
		}
	}
	
	private void verifyEncoding(String string) throws UnsupportedEncodingException {
		// If 'string' contains only US-ASCII characters, just
		// return it.
		int ascii = checkAscii(string);
		if (ascii == ALL_ASCII)
			return;
		
		// Else, apply the specified charset conversion.
		String jcharset;
		// use default charset
		jcharset = MimeUtility.getDefaultJavaCharset(); // the java charset
		String charset = getDefaultMIMECharset(); // the MIME equivalent
		
		// If no transfer-encoding is specified, figure one out.
		String encoding = ascii != MOSTLY_NONASCII ? "Q" : "B";
		
		boolean b64 = encoding.equalsIgnoreCase("B");
		
		doEncode(string, b64, jcharset,
				// As per RFC 2047, size of an encoded string should not
				// exceed 75 bytes.
				// 7 = size of "=?", '?', 'B'/'Q', '?', "?="
				75 - 7 - charset.length(), // the available space
				"=?" + charset + "?" + encoding + "?", // prefix
				true, true, new StringBuilder());
	}
	
	private static void doEncode(String string, boolean b64,
								 String jcharset, int avail, String prefix,
								 boolean first, boolean encodingWord, StringBuilder buf)
			throws UnsupportedEncodingException {
		
		// First find out what the length of the encoded version of 'string' would be.
		byte[] bytes = string.getBytes(jcharset);
		int len = b64 ? BEncoderStream.encodedLength(bytes) : QEncoderStream.encodedLength(bytes, encodingWord);
		
		int size;
		if ((len > avail) && ((size = string.length()) > 1)) {
			// If the length is greater than 'avail', split 'string'
			// into two and recurse.
			// Have to make sure not to split a Unicode surrogate pair.
			int split = size / 2;
			if (Character.isHighSurrogate(string.charAt(split - 1)))
				split--;
			if (split > 0)
				doEncode(string.substring(0, split), b64, jcharset,
						avail, prefix, first, encodingWord, buf);
			doEncode(string.substring(split, size), b64, jcharset,
					avail, prefix, false, encodingWord, buf);
		} else {
			// length <= than 'avail'. Encode the given string
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			OutputStream eos; // the encoder
			if (b64) // "B" encoding
				eos = new BEncoderStream(os);
			else // "Q" encoding
				eos = new QEncoderStream(os, encodingWord);
			
			try { // do the encoding
				eos.write(bytes);
				eos.close();
			} catch (IOException ioex) {
				// ignore
			}
			
			byte[] encodedBytes = os.toByteArray(); // the encoded stuff
			// Now write out the encoded (all ASCII) bytes into our
			// StringBuilder
			if (!first) // not the first line of this sequence
				if (getFoldencodedwords())
					buf.append("\r\n "); // start a continuation line
				else
					buf.append(" "); // line will be folded later
			
			buf.append(prefix);
			for (byte encodedByte : encodedBytes) {
				buf.append((char) encodedByte);
			}
			buf.append("?="); // terminate the current sequence
		}
	}
	
	private int checkAscii(String personal) {
		int ascii = 0, non_ascii = 0;
		int l = personal.length();
		
		for (int i = 0; i < l; i++) {
			if (isNonascii(personal, i)) // non-ascii
				non_ascii++;
			else
				ascii++;
		}
		
		if (non_ascii == 0)
			return ALL_ASCII;
		if (ascii > non_ascii)
			return MOSTLY_ASCII;
		
		return MOSTLY_NONASCII;
	}
	
	@SuppressWarnings("OctalInteger")
	private boolean isNonascii(String personal, int i) {
		int b = (int) personal.charAt(i);
		return b >= 0177 || (b < 040 && b != '\r' && b != '\n' && b != '\t');
	}
	
	private String getDefaultMIMECharset() {
		if (defaultMIMECharset == null) {
			try {
				defaultMIMECharset = System.getProperty("mail.mime.charset");
			} catch (SecurityException ex) {
				// ignore it
			}
		}
		if (defaultMIMECharset == null)
			defaultMIMECharset = MimeUtility.mimeCharset(MimeUtility.getDefaultJavaCharset());
		return defaultMIMECharset;
	}
	
	private static boolean getFoldencodedwords() {
		if (foldEncodedWords == null) {
			Object result = null;
			try {
				Properties props = System.getProperties();
				Object val = props.get("mail.mime.foldencodedwords");
				result = val != null ? val : props.getProperty("mail.mime.foldencodedwords");
			} catch (SecurityException s1) {
				try {
					result = System.getProperty("mail.mime.foldencodedwords");
				} catch (SecurityException s2) {
					// ignore
				}
			}
			
			foldEncodedWords = result != null &&
					(result instanceof String && ((String) result).equalsIgnoreCase("true")) ||
					(result instanceof Boolean && (Boolean) result);
		}
		return foldEncodedWords;
	}
	
	@Override
	public String toString() {
		return format("InternetAddressSimplified{personal='%s', address='%s'}", personal, address);
	}
	
	@SuppressWarnings("unused")
	public String getPersonal() {
		return personal;
	}
	
	String getAddress() {
		return address;
	}
}