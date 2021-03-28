# Demo Guide

## 1. System Preparation 

To test the application and all its components, it is necessary to prepare an environment with data to proceed with the verification of the tests.

### 1.1. Start the Zookeper Registry
First, it is necessary to start the zookeeper registry. To do this, go to the zookeeper/bin directory and type the following command:
```
$ ./zkServer.sh start
```

### 1.1 Compile the Project
It is also necessary to install the necessary dependencies for the silo and the clients (eye and spotter) and compile these components. To do this, go to the project's root directory and type the following command:
```
$ mvn clean install -DskipTests
```
With this command it is already possible to analyze whether the project compiles.

### 1.2. Silo
To carry out the tests, at least one replica of the silo server must be running. In this guide we will run 2 replicas of the silo server. To do this, just open 2 terminals in the silo-server directory and type in each one:
```
$ ./target/appassembler/bin/silo-server localhost 2181 1 localhost 8081 2
```
```
$ ./target/appassembler/bin/silo-server localhost 2181 2 localhost 8082 2
```

This commands will place the 2 silo server replicas at the address localhost and at port 8081, 8082 respectively. Furthermore, the replicas will know that there are 2 more replicas besides them.

### 1.3. Eye
We will register one camera in the replica 1 of the silo server and do the respective observations. The camera will have its own input file with observations already defined. To do this,just open the terminal in the eye directory and type the following command:
```
$ eye localhost 2181 LamegoHouse 20 20 1 < ../demo/lamegoHouse.txt
```

***Note:*** To run the eye script it is necessary to do ```mvn install``` and add it to PATH or directly use the executables generated in the directory ```target/appassembler/bin/```.

### 1.4 Spotter
We will also register in the replica 1 of the silo server one spotter. To do this, just open the terminal in the spotter directory and type the following command:
```
$ spotter localhost 2181 1
```

***Note:*** To run the spotter script it is necessary to do ```mvn install``` and add it to PATH or directly use the executables generated in the directory ```target/appassembler/bin/```.

After executing the commands above, we already have what is needed to test the system.


## 2. Operations Testing
In this section we will run the necessary commands to test all operations. Each subsection is related to each operation present in the silo and some extra operations that are done locally.

### 2.0 help
Test the help operation. This operation tells the user the commands that can be used in the spotter. For this just go to the spotter terminal already opened and type the following command:
```
> help
```

### 2.1 cam_join
This operation was already tested in the preparation of the environment, however it is still necessary to test some restrictions.

2.1.1. Test the join of cameras with the same name but with differente coordinates. The server must reject this operation. For this just go to the eye terminal (not running) and type the following command:
```
$ eye localhost 2181 LamegoHouse 10 10 1
Wrong coordinates for the camera given
Closing eye
```

2.1.2. Test the join of cameras with big names. The server must reject this operation. For this just go to the eye terminal (not running) and type the following command:
```
$ eye localhost 2181 PoncesHasABigHouseName 20 20 1
The name of the camera can not be accepted
Closing eye
```

2.1.3. Test the join of cameras with wrong coordinates. The server must reject this operation. For this just go to the eye terminal (not running) and type the following command:
```
$ eye localhost 2181 DongaHouse 200 200 1
The coordinates given can not be accepted
Closing eye
```

### 2.2 cam_info
Assuming that the command is called info and it receives a name, we run the following tests in the spotter terminal: 

2.2.1. Test if one camera exists. The server must answer with the coordinates of the camera (20,20)
```
> info LamegoHouse
20.0 20.0
```

2.2.2. Test for a camera that does not exist. The server must reject this operation.
```
> info PoncesHouse
The name of the camera not found.
```

### 2.3 report
This operation was already tested in the preparation of the environment, however it is still necessary to test some restrictions. To do this, go to the eye terminal (not running) and type the following command:
```
$ eye localhost 2181 LamegoHouse 20 20 1
```
This operation will be tested using the report command with a type and an id after a comma in the eye terminal.

2.3.1. Test with a non existent type. The server must reject this operation.
```
> bike,11AA22

The type is invalid.
```

2.3.2. Test with person type but with a wrong id for person. type. The server must reject this operation.
```
> person,AAA

The id is invalid.
```

2.3.3. Test with car type but with a wrong id for car. type. The server must reject this operation.
```
> car,111

The id is invalid.
```

### 2.4. track
This operation will be tested using the spot command with an id in the spotter terminal.

2.4.1. Test with a non existent person. Should return empty.
```
> spot person 100

```

2.4.2. Test with a existent person.
```
> spot person 1234567
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
```

2.4.3. Test with a non existent car. Should return empty.
```
> spot car BB00BB

```

2.4.4. Test with a existant car.
```
> spot car AA10BB
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
```

### 2.5. trackMatch
This operation will be tested using the spot command with a fragment of an id in the spotter terminal.

2.5.1. Test with a non existent person. Should return empty.
```
> spot person 2*

```

2.5.2. Test with a existent person.
```
> spot person 97*
person,97023,<Timestamp>,LamegoHouse,20.0,20.0

> spot person *23
person,97023,<Timestamp>,LamegoHouse,20.0,20.0

> spot person *9*02*3
person,97023,<Timestamp>,LamegoHouse,20.0,20.0
```

2.5.3. Test with two or more people.
```
> spot person 123*
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
person,12345671,<Timestamp>,LamegoHouse,20.0,20.0
person,12345672,<Timestamp>,LamegoHouse,20.0,20.0
person,12345673,<Timestamp>,LamegoHouse,20.0,20.0

> spot person *345*
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
person,12345671,<Timestamp>,LamegoHouse,20.0,20.0
person,12345672,<Timestamp>,LamegoHouse,20.0,20.0
person,12345673,<Timestamp>,LamegoHouse,20.0,20.0
```

2.5.4. Test with a non existent car. Should return empty.
```
> spot car BB*

```

2.5.5. Test with a existent car.
```
> spot car 36*
car,36JP70,<Timestamp>,LamegoHouse,20.0,20.0

> spot car *70
car,36JP70,<Timestamp>,LamegoHouse,20.0,20.0

> spot car *JP*
car,36JP70,<Timestamp>,LamegoHouse,20.0,20.0
```

2.5.6. Test with two or more cars.
```
> spot car AA*
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA11BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA22BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA66CC,<Timestamp>,LamegoHouse,20.0,20.0
car,AA77CC,<Timestamp>,LamegoHouse,20.0,20.0

> spot car *CC
car,AA66CC,<Timestamp>,LamegoHouse,20.0,20.0
car,AA77CC,<Timestamp>,LamegoHouse,20.0,20.0
car,DD44CC,<Timestamp>,LamegoHouse,20.0,20.0
car,DD55CC,<Timestamp>,LamegoHouse,20.0,20.0

> spot car DD*C*
car,DD44CC,<Timestamp>,LamegoHouse,20.0,20.0
car,DD55CC,<Timestamp>,LamegoHouse,20.0,20.0
```

### 2.6. trace
This operation will be tested using the trail command with an id in the spotter terminal.

2.6.1. Test with a non existent person. Should return empty.
```
> trail person 123

```

2.6.2. Test with a existent person.
```
> trail person 1234567
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
```

2.6.3. Test with a non existent car. Should return empty.
```
> trail car 00AA00

```

2.6.4. Test with a existent car
```
> trail car AA10BB
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
```

### 2.7 ctrl operations 
This operations will be tested using 3 ctrl commands in the spotter terminal.

2.7.1. ctrl_ping

This operation send a message to the silo server replica and wait for the answer.
```
> ctrl_ping
Hello Spotter!
```
 
2.7.2. ctrl_init
```
> ctrl_init
```
This operation will add some car and person observations. Too see the new observations you can type the following commands: 
```
> spot person *
person,1234567,<Timestamp>,LamegoHouse,20.0,20.0
person,12345671,<Timestamp>,LamegoHouse,20.0,20.0
person,12345672,<Timestamp>,LamegoHouse,20.0,20.0
person,12345673,<Timestamp>,LamegoHouse,20.0,20.0
person,97023,<Timestamp>,LamegoHouse,20.0,20.0
person,991006,<Timestamp>,alameda,38.737613,9.303164
person,991556,<Timestamp>,alameda,38.737613,9.303164

> spot car *
car,36JP70,<Timestamp>,LamegoHouse,20.0,20.0
car,AA10BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA11BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA22BB,<Timestamp>,LamegoHouse,20.0,20.0
car,AA66CC,<Timestamp>,LamegoHouse,20.0,20.0
car,AA77CC,<Timestamp>,LamegoHouse,20.0,20.0
car,DD33BB,<Timestamp>,LamegoHouse,20.0,20.0
car,DD44CC,<Timestamp>,LamegoHouse,20.0,20.0
car,DD55CC,<Timestamp>,LamegoHouse,20.0,20.0
car,IM94LA,<Timestamp>,alameda,38.737613,9.303164
car,RA00TH,<Timestamp>,alameda,38.737613,9.303164
car,RA41OH,<Timestamp>,alameda,38.737613,9.303164
```

### 2.8 eye special operations
This operations will be tested using the commands ```#``` (cardinal) and ```zzz``` in the eye terminal.

2.8.1. # (comment)
```
> # This is a comment!
```

This command does nothing, the eye will ignore it.

2.8.2. zzz (pause in data processing for x milliseconds)
```
> zzz,10000
> bike,123
The type is invalid.
```
The server must reject the report operation, but the error message will only be presented in the terminal after some delay.

## 3. Replication and Fault Toleration
In this part of the guide it will be tested the new features of replication and fault tolerance of the silo-server. Before this part close all the terminals that were open including the replicas (close with enter).

To carry out the tests, run 3 replicas of the silo server. To do this, just open 3 terminals in the silo-server directory and type in each one:
```
$ ./target/appassembler/bin/silo-server localhost 2181 1 localhost 8081 3
```
```
$ ./target/appassembler/bin/silo-server localhost 2181 2 localhost 8082 3
```
```
$ ./target/appassembler/bin/silo-server localhost 2181 3 localhost 8083 3
```

Run an eye. To do this, open a terminar in the eye directory and type:
```
$ eye localhost 2181 LamegoHouse 20 20 1 
```

Run two spotters. To do this, open two terminals in the spotter directory and type:
```
$ spotter localhost 2181 1
```
```
$ spotter localhost 2181 2 
```
The spotter in the first terminal is gonna be referred to as spotter 1 and the one in the second as spotter 2.


Insert a report into the eye, in the following way:
```
> person,1234

```

Go to the spotter 2 and insert the command:
```
> spot person 1234

```
It will return nothing.

Now wait 30 seconds for the gossip to happen after inserting the command. 

Insert the command once again and it will return the information corresponding to the person, which shows that the gossip is working because you are connected to a replica that is reading information that was sent to another.
```
> spot person 1234
person,1234,<Timestamp>,LamegoHouse,20.0,20.0
```

Now, go to the second replica and insert Ctrl+z(which will pause the process) or press enter(which will kill the process). Following that, go to the spotter 2 and insert:
```
> spot person 1234
person,1234,<Timestamp>,LamegoHouse,20.0,20.0
```
You will receive information because even though the spotter 2 could not connect himself to the replica that was closed it will connect to another one.

If you did Ctrl+z go to the terminal where you stopped the replica 2 and run it again:
```
$ fg
```
This will make the process start again.

If you pressed enter go to the terminal and run the replica 2 again.

Now go to the spotter 2 and kill it with Ctrl+c and run it again. If you ask for the following information after the gossip request:
```
> spot person 1234
person,1234,<Timestamp>,LamegoHouse,20.0,20.0
```
The information will be returned to you again.

Go to the eye terminal that is connected to the first replica and insert:
```
> car,88YY77

```

Go to the spotter 1 and do the following and there will be something in return:
```
>spot car 88YY77
car,88YY77,<Timestamp>,LamegoHouse,20.0,20.0

```

If you do Ctrl+z or enter as referred to before in the first replica before the gossip occurs and then you do the following in the spotter 1:
```
>spot car 88YY77
car,88YY77,<Timestamp>,LamegoHouse,20.0,20.0
```
The same information will be returned since it was saved in the cache, even though it is not saved in any replica.

