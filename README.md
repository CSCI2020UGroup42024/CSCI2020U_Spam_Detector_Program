# CSCI2020U Assignment 01 - Spam Detector Application

## Project Information
### Overview
The goal of this program is to recognize whether an email is spam or not. This will be done by first giving the program a large amount of test data so that the program can use the data to complete the calculations needed to tell whether the email is spam. The calculations use the words in the email to find a percentage chance of the word being in a spam folder. The program then adds up all the percentages and if the total exceeds 50%, the email is labeled as spam, otherwise it will be labeled as ham. The precision and accuracy are shown as a percentage.
<img width="1721" alt="a" src="https://github.com/OntarioTech-CS-program/w24-csci2020u-assignment01-group4/assets/114099046/3cbf50b4-68ac-4956-8a6c-00add997176f">


### List Of Group 4 Participants
> Victor Ryzak<br>
> Tobenna Nnaobi<br>
> Jonathan Mathew<br>
> Asjad Ansari<br>


## Improvements
- Revamped the client website interface to enhance user experience with improved navigation and aesthetics.


## How-To Run Application
- The first step is to procure a large amount of data the program will utilize to train itself.

- The second step involves the program using said data to train itself. This process is to verify the application function as intended. For the spam resource files you should change the access allow control to your local host.

- The third step is to send the program files that users wish to check to see if the file is spam or ham.

- The final output will result in the program providing its guess at whether the program is spam or not based on the data provided by the user.


## Other Utilized Resources
The other utilized resources used to create the program is:

- Jackson Databind: used for data-binding and object serialization/deserialization

- Jackson Annotations: to control the serialization and deserialization of Java objects to and from JSON.
