# Java test assignment

This repository contains code for the test assignment that I've done in April 2022 during
recruitment process to an IT company.

Keep in mind that test assignment was solved in a manner to demonstrate analytical solving,
coding and designing skills. It is not a production ready solution by any means but can
give you a glance on candidate's approach to problem-solving.

## Task

Original task was the following:

```
Included in this task are the following files:
* An interface called SaleObjectConsumer
* Three example files with different formats, but similar data: SaleObjects.csv, SaleObjects.xml,
SaleObjects.json

The goal of this fictive task is to write an application that reads the content from files
with content written in different formats ("data" directory), but containing similar data,
and reporting that data to our partner companies. As the data is similar, the content of
the files may be reported using the same interface, called "SaleObjectConsumer" (which is
included in this task).

To fulfill the task, you must:
● Accept the filenames that should be parsed as an argument to your application.
● Parse the data files, containing meta-data (called SaleObjects) describing houses and
apartments.
● Report all the parsed SaleObjects to our partners systems by using the interface in the
following order:
  1. Call getPriorityOrderAttribute() to get the attribute to order the SaleObjects by
  before reporting them.
  2. Call startSaleObjectTransaction() before reporting any SaleObjects.
  3. Call reportSaleObject() for each sale object in prioritized order.
  4. Call commitSaleObjectTransaction() when done reporting SaleObjects.
● Remember to read the documentation in the SaleObjectConsumer interface file.

Note! As of now we have not received any implementation of the SaleObjectConsumer-interface
from our partners, but we are confident that your solution will work regardless of their
chosen implementation.

Note! You may report any amount of SaleObjects to the interface after having called the
method startSaleObjectTransaction, but after calling commitSaleObjectTransaction you must
start over from step #2 if you wish to send additional SaleObjects.

There are three different file formats included: CSV, XML and Json. You do not have to
parse all of them. Please choose the two formats you are most comfortable parsing
(and feel free to use any third-party parsing library of your choosing if you want to).

Final words
We hope that you have a fun time coding this challenge! Think of your work as a good
starting point for a library that will be used and continued on by other developers.
Below are some hints of some of the aspects that we will look at in your solution
(not ordered):
● Have the task been solved?
● How easy would it be for other developers to:
  ● Understand and consume your API?
  ● Contribute further to your solution?
● How is the code structured in regards to Object Oriented Design?
● How error prone is it?
● How easy would it be to extend it with new input formats?
● e.t.c.
We recommend implementing the challenge using a console application, but you may solve
it using spring if you please.
```

## How to launch

Application can be interacted via its CLI. CLI accepts one command as program argument
`sendToPartners` and file paths relative to executable jar's directory.

Command `sendToPartners` was added for "possible" future scaling in mind if one needs to
add another command for different functionality.

Files are expected to be in XML or JSON formats with provided extension in file name (e.g.
"someFile.xml", "anotherFile.json").

In order to run this application, **maven** should be installed.

Run `mvn clean package` to build an executable jar, tests are executed during the `test`
build phase. To run a jar execute `java -jar ./code-challenge-1.0.0-SNAPSHOT.jar` with
provided parameters. Running script can be found in `run.sh`.
