[![APACHE v2 License](https://img.shields.io/badge/license-apachev2-blue.svg?style=flat)](LICENSE) [![Latest Release](https://img.shields.io/maven-central/v/com.github.bbottema/emailaddress-rfc2822.svg?style=flat)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.bbottema%22%20AND%20a%3A%22emailaddress-rfc2822%22) [![Build Status](https://img.shields.io/travis/bbottema/email-rfc2822-validator.svg?style=flat)](https://travis-ci.org/bbottema/email-rfc2822-validator) [![Codacy](https://img.shields.io/codacy/7cf43e32227f443780e7b16018542e24.svg?style=flat)](https://www.codacy.com/app/b-bottema/email-rfc2822-validator)


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
    <version>1.0.0</version>
</dependency>
```

## Usage

There are two classes available, EmailaddressValidator and EmailAddressParser. The second is used to extract data from (complex / mangled) email strings.

For both of these, you use the EmailAddressCriteria enumeration to control RFC strictness.

Here's an example for validating an email address:

```java
boolean isValid = EmailAddressValidator.isValid(emailaddress);
boolean isValid = EmailAddressValidator.isValid(emailaddress, EmailAddressCriteria.DEFAULT);
boolean isValid = EmailAddressValidator.isValid(emailaddress, EmailAddressCriteria.RFC_COMPLIANT);
boolean isValid = EmailAddressValidator.isValid(emailaddress, EnumSet.of(ALLOW_DOT_IN_A_TEXT, ALLOW_SQUARE_BRACKETS_IN_A_TEXT));
```


---


### Latest Progress ###

v1.0.0

Initial release