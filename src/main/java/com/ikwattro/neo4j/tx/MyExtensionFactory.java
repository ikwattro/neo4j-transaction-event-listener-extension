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

    public MyExtensionFactory() {
        super(ExtensionType.DATABASE, "MyExtensionFactory");
    }

    @Override
    public Lifecycle newInstance(ExtensionContext context, Dependencies dependencies) {
        var db = dependencies.db();
        var managementService = dependencies.databaseManagementService();
        var log = dependencies.log();

        return new MyAdapter(db, managementService, log);
    }

    public static class MyAdapter extends LifecycleAdapter {
        private GraphDatabaseService db;
        private DatabaseManagementService managementService;
        private LogService log;

        public MyAdapter(GraphDatabaseService db, DatabaseManagementService managementService,
                LogService log) {
            this.db = db;
            this.managementService = managementService;
            this.log = log;
        }

        @Override
        public void start() throws Exception {
            if (!db.databaseName().equals(SYSTEM_DATABASE_NAME)) {
                log.getUserLog(MyExtensionFactory.class).info(
                        "Registering transaction event listener for database " + db.databaseName());
                managementService.registerTransactionEventListener(db.databaseName(),
                        new MyTransactionEventListener(db, log));
            } else {
                log.getUserLog(MyExtensionFactory.class)
                        .info("System database. Not registering transaction event listener");
            }
        }
    }

    interface Dependencies {
        GraphDatabaseService db();

        DatabaseManagementService databaseManagementService();

        LogService log();
    }
}
