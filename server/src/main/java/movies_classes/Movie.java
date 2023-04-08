package movies_classes;

import enums.MovieGenre;
import enums.MpaaRating;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 *    Movie - the main custom class
 *    @param  {int} id
 *    @param  {String} name
 *    @param  {Coordinates} coordinates
 *    @param  {java.util.Date creationDate} creationDate
 *    @param  {long} oscarsCount
 *    @param  {long} length
 *    @param  {MovieGenre} genre
 *    @param  {MpaaRating} mpaaRating
 *    @param  {Person operator} operator
 * */

public class Movie implements Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private long oscarsCount; //Значение поля должно быть больше 0
    private long length; //Значение поля должно быть больше 0

    private MovieGenre genre; //Поле может быть null
    private MpaaRating mpaaRating; //Поле может быть null
    private Person operator; //Поле может быть null
    private String creator; 


//    public Movie(){}

    public Movie(int id, String name, Coordinates coordinates, Date creationDate, long oscarsCount, long length, MovieGenre genre, MpaaRating mpaaRating, Person operator, String creator)
    {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.oscarsCount = oscarsCount;
        this.length = length;
        this.genre = genre;
        this.mpaaRating = mpaaRating;
        this.operator = operator;
        this.creator = creator;
    }

    public Movie(int id, HashMap data)
    {
        System.out.println(data);
        this.id = id;
        this.name = (String) data.get(0);
        this.coordinates = new Coordinates(Integer.parseInt(data.get(1).toString()), Integer.parseInt(data.get(2).toString()));
        this.creationDate = new Date();
        this.oscarsCount = Long.parseLong(data.get(3).toString());
        this.length = Long.parseLong(data.get(4).toString());
        this.genre = (data.get(5) == null ?  null : MovieGenre.valueOf(data.get(5).toString()));
        this.mpaaRating = (data.get(6) == null ?  null : MpaaRating.valueOf(data.get(6).toString()));
        this.operator = new Person(data.get(7).toString(), data.get(8).toString(), (data.get(9) == null ? null : data.get(9)),
                new Location(data.get(10) == null ? null : data.get(10),
                        (data.get(11) == null ? null : data.get(11)),
                        (data.get(12) == null ? null : data.get(12))));
        this.creator = creator;
    }

    public void update(HashMap data){
        System.out.println(data);
        this.name = (String) data.get(0);
        this.coordinates = new Coordinates(Integer.parseInt((String) data.get(1)), Integer.parseInt((String) data.get(2)));
        this.creationDate = new Date();
        this.oscarsCount = Long.parseLong((String) data.get(3));
        this.length = Long.parseLong((String) data.get(4));
        this.genre = (data.get(5) == null ?  null : MovieGenre.valueOf((String) data.get(5)));
        this.mpaaRating = (data.get(6) == null ?  null : MpaaRating.valueOf((String) data.get(6)));
        this.operator = new Person((String) data.get(7), (String) data.get(8), (data.get(9) == null ? null : data.get(9)),
                new Location(data.get(10) == null ? null : data.get(10),
                        (data.get(11) == null ? null : data.get(11)),
                        (data.get(12) == null ? null : data.get(12))));
    }

    // getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public MpaaRating getMpaaRating() {
        return mpaaRating;
    }

    public Person getOperator() {
        return operator;
    }

    public long getLength() {
        return length;
    }

    public long getOscarsCount() {
        return oscarsCount;
    }

    public String getCreator() {
        return creator;
    }

    public String[] getInstance() {
        return new String[]{"id: " + id, "name: " + name, "coordinates:", "[x:" +
                coordinates.getCoordX(), "y: " + coordinates.getCoordY() + "], ", "creationDate: " + creationDate,
                "oscarsCount: " + oscarsCount, "length: " + length, "genre: " + genre, "mpaaRating: " + mpaaRating,
                "operator: [", "name: " + operator.getName(), "passportID: " + operator.getPassportID(),
                "nationality: " + operator.getNationality(), "location: ", "[x: " + operator.getLocation().getLocX(),
                "y: " + operator.getLocation().getLocY(), "z: " + operator.getLocation().getLocZ() + "] " + "login: " + creator};
    }
}