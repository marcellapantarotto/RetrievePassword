# RetrievePassword
Java program that generantes a password, encrypts it and, latter, can retrieve the password in plain text.

For execution, the compilation must have the following arguments:
(Each step must be executed separately, in the following order)

#### 1) To list the name of all of the installed providers:
```
--list
```
> Choose a provider

#### 2) To create a generate a Password:
```
--generate-password <provider>
```
Example:
```
--generate-password SUN DSA
```
> Get the ciphered password

#### 3) To retrieve the Password in plain text:
```
--retrieve-password <cipheredPassword> <provider>
```
Example:
```
--retrieve-password 6C77EA999706D81037E154CA1FE29575 SUN DSA
```
