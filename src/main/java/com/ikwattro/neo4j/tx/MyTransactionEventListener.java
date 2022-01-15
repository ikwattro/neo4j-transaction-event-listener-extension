package com.ikwattro.neo4j.tx;

import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.event.TransactionEventListenerAdapter;
import org.neo4j.logging.internal.LogService;

public class MyTransactionEventListener extends TransactionEventListenerAdapter<Void> {

    private final GraphDatabaseService db;
    private final LogService logsvc;

    public MyTransactionEventListener(GraphDatabaseService graphDatabaseService, LogService logsvc) {
        db = graphDatabaseService;
        this.logsvc = logsvc;
    }

    @Override
    public Void beforeCommit(TransactionData data, Transaction transaction, GraphDatabaseService databaseService) throws Exception {
        data.createdNodes().forEach(node -> {
            node.setProperty("setByTxListener", "some value");
        });

        return null;
    }

    @Override
    public void afterCommit(TransactionData data, Void state, GraphDatabaseService databaseService) {
        logsvc
                .getUserLog(MyTransactionEventListener.class)
                .info("Logging after commit on transaction with ID %s for database %s", data.getTransactionId(), db.databaseName());
    }
}