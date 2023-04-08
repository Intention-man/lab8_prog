package functional_classes.commands_executors;

import movies_classes.Movies;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * @deprecated
 *    Deprecated class (early, it worked with collection from xml file)
 * */


public class FileWorker {

    // initialization

    private final String xmlFileName = System.getenv("FileWithCollection");
    private final Path path = Paths.get(xmlFileName);
    private final Serializer serializer = new Persister();
    private Scanner scanner;


    public FileWorker() throws IOException {
        this.scanner = new Scanner(path);
    }

    // common


    public Movies fill() {
        try {
            return serializer.read(Movies.class, new File(xmlFileName));
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    // commands execution

    public boolean save(Movies movies) {
        try {
            serializer.write(movies, new File(xmlFileName));
        } catch (Exception e) {
            System.out.println("Ошибка при сохранении коллекции в файл. Узнайте у разработчика в чем дело)");
        }
        return true;
    }
}