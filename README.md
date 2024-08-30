# Password Manager

![screenshot of program](https://github.com/tmjonker8/PasswordManager/blob/master/Images/main-screenshot.png)

## Description

This is a basic password manager.  It allows you to create multiple users, in case you and you significant other want to store your passwords separately.  When you log in, you have the ability to add passwords to your local password database. Currently, you have the ability to create passwords for WEBSITES, APPLICATIONS, OR GAMES.  Once you create a password it is encrypted, linked to your user profile, and stored in an encoded file on your hard drive in the /user folder (user.pm) that is created when you first run the program.  No decrypted password information is stored on your computer.  When you add a password in the program, you have the ability to let the program generate a password for you.  If you click the 'Generate Password' link, it will generate a random 20 character string consisting of uppercase and lowercase letters, numbers, and symbols. All of the passwords that you create are linked to your user profile.  If a different user logs in, they will not be able to access your passwords.  This program uses Google Tink AES-GCM encryption for all passwords.

## Getting Started

### Dependencies

* Google Tink
* JavaFX 14
* Maven

### Installing

* It can be unzipped into any folder on your computer.
* run 'mvn clean compile assembly:single' 


### Executing program

* cd into the 'target' directory
* Run 'java -jar passwordmanager-1.0-SNAPSHOT-jar-with-dependencies.jar'
 * When you start the program, it will create two folders in whatever folder you execute it from.  Those folders are /user and /config.  These folders will house your user and config files.


## Authors

Contributors names and contact info

* Tim Jonker
  - tmjonker1@outlook.com

