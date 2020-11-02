package com.ikwattro.neo4j.tx;

import org.neo4j.annotations.service.ServiceProvider;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.extension.ExtensionFactory;
import org.neo4j.kernel.extension.ExtensionType;
import org.neo4j.kernel.extension.context.ExtensionContext;
import org.neo4j.kernel.lifecycle.Lifecycle;
import org.neo4j.kernel.lifecycle.LifecycleAdapter;
import org.neo4j.logging.internal.LogService;

import static org.neo4j.configuration.GraphDatabaseSettings.SYSTEM_DATABASE_NAME;


@ServiceProvider
public class MyExtensionFactory extends ExtensionFactory<MyExtensionFactory.Dependencies> {

    private GraphDatabaseService db;
    private DatabaseManagementService managementService;
    private LogService log;

    public MyExtensionFactory() {
        super(ExtensionType.DATABASE, "MyExtensionFactory");
    }

    @Override
    public Lifecycle newInstance(ExtensionContext context, Dependencies dependencies) {
        db = dependencies.db();
        managementService = dependencies.databaseManagementService();
        log = dependencies.log();

        return new MyAdapter();
    }

    public class MyAdapter extends LifecycleAdapter {

        @Override
        public void start() throws Exception {
            if (!db.databaseName().equals(SYSTEM_DATABASE_NAME)) {
                log.getUserLog(MyExtensionFactory.class).info("Registering transaction event listener for database " + db.databaseName());
                managementService.registerTransactionEventListener(
                        db.databaseName(),
                        new MyTransactionEventListener(db, log)
                );
            } else {
                log.getUserLog(MyExtensionFactory.class).info("System database. Not registering transaction event listener");
            }
        }
    }

    interface Dependencies {
        GraphDatabaseService db();
        DatabaseManagementService databaseManagementService();
        LogService log();
    }
}
