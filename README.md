# Implementation of FTP client and server
This repository hosts a project that implements the **FTP Client and Server** as part of the coursework for Computer Networks (CNT5106C) under the department of Computer Science of the University of Florida.

## Problem Statement
In this project, you will implement a simple version of ftp client/server software. It consists of two programs: ftpclient and ftpserver. First, ftpserver is started on a computer.
hen, ftpclient is executed on the same computer; the server’s port number are supplied in the command line, for example, “ftpclient 5106”.
The user can issue a command at the client side: “get <filename>”, <br/>
which is to retrieve a file from the server, or “upload < filename>”, which is to upload a
file to the server. Because the file could be arbitrarily large, you are required to split the
file into chunks of 1K bytes and use a loop to send the chunks, each time one chunk. <br/>

Programming language:
Java

We can issue three commands at the client side: 
- `exit` is to end the connection, 
- `get <filename>` is to retrieve a file from the server, 
- `upload < filename>` is to upload a file to the server. 

## Execution

Compilation: <br/> 
- Server ->  `javac server.java` <br/>
- Client -> `javac client.java` <br/>


Run: <br/>
- Server ->  `java server.java` <br/>
- Client -> `java client.java` <br/>

## Test Cases
<details>
  <summary>Click to expand</summary>
    
  - Start the server
  - Start the client
  - Command `ftpclient <port>` with correct port number. (Type in port number as 4000, as the server will start on 4000).
  - Try one of the valid commands `get <filename>` and `upload <filename>` and exit to stop.
</details>
