# course_work_parallel_computing
University course work about creating an inverted index using parallel computing

## Folder Structure

The workspace contains two folders by default, where:
- `java`: the folder to maintain java sources
    - `src`: the folder to maintain sources
        - `Server`: server implementation
        - `Client`: client implementation
        - `Test`: test cases
- `python`: the folder to maintain python sources
## Running the program
To run the program on a personal computer, you must clone a repository with the source code of the program first:
```
git clone https://github.com/lammaxcool/course_work_parallel_computing.git
```
Next, you need to go to the folder with the project and create a folder named `data` in it, go to this folder and create another one in it, name it `2000`. In this folder you need to pass the text files that need to be indexed.

Next, you need to compile the code. To do this, execute the following command (being in the project folder):
```
javac -d out/production java/src/*/*.java
```
Starting the program: first you need to start the server, then start the client:
- To start the server, run the following command:
```
java -cp out/production Server.Server
```
If desired, you can specify the port on which to start the server (default value - 2021):
```
java -cp out/production Server.Server 2121
```
After starting, we will receive a message with the port number that the server is listening on.
- To start the client, run the following command:
```
java -cp out/production Client.Client
```
If desired, you can specify the ip and port on which the server is running (the default value is localhost 2021):
```
java -cp out/production Client.Client localhost 2121
```
Important! The client must connect to the port on which the server is running.
After startup, you can see the status of the connection to the server. After the `index:` prompt appears, you can write commands.
Two commands are available 
- `/find word` to search. To search, you can enter one or several words (separated with spaces).
- `/exit` to exit. After entering the exit command, the program will shut down.

- To run the test, run the following command:
```
java -cp out / production Test.IndexerTest
```
When the test is complete, a `result.txt` file will be created in the project folder, which will contain the test results.
Important! To test, you need to create two more folders in the data folder:
- `10000`
- `100000`
Regardless of the folder names, you can enter any number of files there.
