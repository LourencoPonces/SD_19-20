# Sauron

[Distributed Systems](https://fenix.tecnico.ulisboa.pt/disciplinas/SDis126451113264/2019-2020/2-semestre/pagina-inicial) 2019-2020, 2nd semester project


## Authors

**Group A21**

### Team members


| Number | Name              | User                                | Email                                       |
| -------|-------------------|-------------------------------------| --------------------------------------------|
| 89498  | Beatriz Martins   | <https://github.com/apollee>        | <mailto:maria.d.martins@tecnico.ulisboa.pt> |
| 89526  | Pedro Lamego      | <https://github.com/pedro-lamego>   | <mailto:pedrownlamego@tecnico.ulisboa.pt>   |
| 97023  | Lourenço Ponces   | <https://github.com/LourencoPonces> | <mailto:lourenco.duarte@tecnico.ulisboa.pt> |

### Task leaders


| Task set | To-Do                         | Leader              |
| ---------|-------------------------------| --------------------|
| core     | protocol buffers, silo-client | _(whole team)_      |
| T1       | cam_join, cam_info, eye       | Lourenço Ponces     |
| T2       | report, spotter               | Pedro Lamego        |
| T3       | track, trackMatch, trace      | Beatriz Martins     |
| T4       | test T1                       | Beatriz Martins     |
| T5       | test T2                       | Lourenço Ponces     |
| T6       | test T3                       | Pedro Lamego        |


## Getting Started

The overall system is composed of multiple modules.
The main server is the _silo_.
The clients are the _eye_ and _spotter_.

See the [project statement](https://github.com/tecnico-distsys/Sauron/blob/master/README.md) for a full description of the domain and the system.

### Prerequisites

Java Developer Kit 11 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```

### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The integration tests are skipped because they require the servers to be running.

### Running the Program

Open a terminal in the root directory and type:
```
cd silo-server
mvn compile exec:java
```

Then, open another terminal also in the root directory and type:
```
cd eye
./target/appassembler/bin/eye localhost 8080 <Name of the Camera> <Longitude of the Camera> <Latitude of the Camera>
```

Open one last terminal in the root directory and type:
```
cd spotter
./target/appassembler/bin/spotter localhost 8080
```

## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [gRPC](https://grpc.io/) - RPC framework


## Versioning

We use [SemVer](http://semver.org/) for versioning. 
