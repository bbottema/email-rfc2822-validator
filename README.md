[![APACHE v2 License](https://img.shields.io/badge/license-apachev2-blue.svg?style=flat)](LICENSE-2.0.txt) [![Latest Release](https://img.shields.io/maven-central/v/com.github.bbottema/emailaddress-rfc2822.svg?style=flat)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.bbottema%22%20AND%20a%3A%22emailaddress-rfc2822%22) [![Build Status](https://img.shields.io/badge/CircleCI-build-brightgreen.svg?style=flat)](https://circleci.com/gh/bbottema/email-rfc2822-validator) [![Codacy](https://img.shields.io/codacy/grade/7cf43e32227f443780e7b16018542e24.svg?style=flat)](https://www.codacy.com/app/b-bottema/email-rfc2822-validator)

# email-rfc2822-validator #

The world's only more-or-less-2822-compliant Java-based email address extractor / verifier

* Author: http://stackoverflow.com/a/13133880/441662
  * Based on: [Les Hazlewood's java regex version](http://leshazlewood.com/2006/11/06/emailaddress-java-class/comment-page-1/#comment_count), code: https://github.com/lhazlewood/jeav
* Origin: http://lacinato.com/cm/software/emailrelated/emailaddress
* Used in: https://github.com/bbottema/simple-java-mail

email-rfc2822-validator is available in Maven Central:

```
<dependency>
    <groupId>com.github.bbottema</groupId>
    <artifactId>emailaddress-rfc2822</artifactId>
    <version>2.1.4</version>
</dependency>
```

And just to show you that this stuff is hard, here's JavaMail's [official parser's](https://searchcode.com/codesearch/view/63668224/) javadoc on the subject (line 669):

```
    /*
     * RFC822 Address parser.
     *
     * XXX - This is complex enough that it ought to be a real parser,
     *       not this ad-hoc mess, and because of that, this is not perfect.
     *
     * XXX - Deal with encoded Headers too.
     */
    @SuppressWarnings("fallthrough")
    private static InternetAddress[] parse(String s, boolean strict,
				    boolean parseHdr) throws AddressException {
```

## Usage

There are two classes available, EmailaddressValidator and EmailAddressParser. The second is used to extract data from (complex / mangled) email strings.

For both of these, you use the EmailAddressCriteria enumeration to control RFC strictness.

Here's an example for validating an email address:

```java
boolean isValid = EmailAddressValidator.isValid(emailaddress);
boolean isValid = EmailAddressValidator.isValid(emailaddress, EmailAddressCriteria.RECOMMENDED);
boolean isValid = EmailAddressValidator.isValid(emailaddress, EmailAddressCriteria.RFC_COMPLIANT);
boolean isValid = EmailAddressValidator.isValid(emailaddress, EnumSet.of(ALLOW_DOT_IN_A_TEXT, ALLOW_SQUARE_BRACKETS_IN_A_TEXT));
```


---


### Latest Progress ###

v2.1.4

- [#17](https://github.com/bbottema/email-rfc2822-validator/issues/17): IllegalArgumentException when passing null to EmailAddressValidator.isValid(String)


v2.1.3

- [#14](https://github.com/bbottema/email-rfc2822-validator/issues/14): Update project to Java 1.7 and Jakarta Mail


v1.1.3

- [#13](https://github.com/bbottema/email-rfc2822-validator/issues/13): Fixed TLD limitation for domains longer than six


v1.1.2

- Fixed regression bug where name and address were switched around


v1.1.1

- This library can now be used with any javax.mail dependency from 1.5.5 and upwards.


v1.1.0

- [#7](https://github.com/bbottema/email-rfc2822-validator/issues/7): Clarified validation modes (default vs strictly rfc compliant)

**NOTE**: EmailAddressValidator.isValid() now validates using EmailAddressCriteria.DEFAULT rather than EmailAddressCriteria.RFC_COMPLIANT. Use
EmailAddressValidator.isValidStrict() for RFC compliant validation.


v1.0.1

Initial release
