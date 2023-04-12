package functional_classes;

import functional_classes.commands_executors.CollectionAnalyzer;
import functional_classes.commands_executors.CommandDistributor;
import functional_classes.database.DBCollectionHandler;
import functional_classes.database.DBConnector;
import functional_classes.database.DBUserHandler;
import functional_classes.threads.ServerReader;
import functional_classes.threads.ServerSerializer;
import movies_classes.Movies;
import org.postgresql.util.PSQLException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.*;

public class Server {
    private final Properties properties;
    static ExecutorService executor = Executors.newFixedThreadPool(1); // Создаем пул из 1 потока
    static ForkJoinPool forkJoinPool = new ForkJoinPool(); // Создаем пул для многопоточной отправки ответ
    DBConnector dbConnector;
    DBCollectionHandler dbCollectionHandler;
    DBUserHandler dbUserHandler;
    ServerSerializer serverSerializer;


    public Server(Properties properties) {
        this.properties = properties;
    }

    public void serverStartup() {
        try {
            dbConnector = new DBConnector(properties);
            dbConnector.createConnection();
            dbCollectionHandler = new DBCollectionHandler(dbConnector.getConnection());
            Movies movies = new Movies();
            movies.setMoviesList(dbCollectionHandler.getAllMovies());
            dbUserHandler = new DBUserHandler(dbConnector.getConnection());
            CollectionAnalyzer collectionAnalyzer = new CollectionAnalyzer(movies, dbCollectionHandler);
            CommandDistributor commandDistributor = new CommandDistributor();
            commandDistributor.addExecutor("CollectionAnalyzer", collectionAnalyzer);
            commandDistributor.addExecutor("DBCollectionHandler", dbCollectionHandler);
            commandDistributor.addExecutor("DBUserHandler", dbUserHandler);

            ServerReader serverReader = new ServerReader(commandDistributor);
            serverSerializer = new ServerSerializer(commandDistributor);
            collectionAnalyzer.setServerSerializer(serverSerializer);

            Thread t1 = new Thread(() -> {
                try {
                    serverReader.readConsole(this);
                } catch (SQLException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
            Thread t2 = new Thread(() -> {
                while (true) {
                    serverSerializer.waitForRequest();
                    start(serverSerializer);
                }
            });

            t2.start();
            t1.start();

        } catch (PSQLException e) {
            System.out.println("Проблема с соответствием столбцов в БД и в памяти");
        } catch (Exception | ExceptionInInitializerError e) {
            System.out.println(e);
        }
    }

    public static void start(ServerSerializer serverSerializer) {
        while (!Objects.equals(serverSerializer.getStage(), "get")) {
            executor.submit(() -> {
                        if (Objects.equals(serverSerializer.getStage(), "execute")) {
                            serverSerializer.executeCommand();
                            System.out.println(serverSerializer.getStage());
                            forkJoinPool.submit(() -> {
                                try {
                                    serverSerializer.sendResponse();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }
            );
        }
    }

    public void closeAllAndExit() throws SQLException, IOException {
        serverSerializer.setStage("exit");
        serverSerializer.close();
        dbCollectionHandler.close();
        dbUserHandler.close();
        dbConnector.close();
        System.exit(0);
    }
}