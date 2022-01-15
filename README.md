# Neo4j Transaction Event Listener Extension example code

## Build this extension

```bash
mvn clean package
```

## Install the plugin

Copy the jar into the `/plugins` directory of your Neo4j server

```bash
copy target/*.jar <NEO4J_HOME>/plugins
```

Restart Neo4j

```shell
./bin/neo4j restart
```

## Test

This plugin will add some attribute to every create node

```cypher
CREATE (n:Person {name: "Chris"})
```

Then check the data : 

```cypher
MATCH (n:Person)
RETURN n{.*}

//Result
╒═══════════════════════════════════════════════╕
│"n"                                            │
╞═══════════════════════════════════════════════╡
│{"name":"Chris","setByTxListener":"some value"}│
└───────────────────────────────────────────────┘
```

A line is also logged during the afterCommit phase

```shell
2022-01-15 17:09:19.814+0000 INFO  [neo4j/3bfca502] Logging after commit on transaction with ID 7
```