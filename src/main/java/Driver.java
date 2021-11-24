import Persistence.genericDAO;
import Utility.Connection1;
import model_example.Human;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Driver {
    public static void main(String[] args) throws SQLException, NoSuchFieldException {
        System.out.println("create works so far, so does getAll, and getByPK");
//        created 2 humans for now for testing
        Human levan1 = new Human(1, "levan", 25, "software engineer");
        Human levan2 = new Human(2, "levan2", 25, "software engineer"); // creates the Human objects with this values
        Human levan3 = new Human(3, "levan3", 40, "unemployed");
        Connection con = Connection1.getConnection(); // get the connection to database to persist changes or read data
        genericDAO orm = new genericDAO(); // object of dao class to use the methods
        List<Human> people; // list of Human objects already in the database
        List<Human> person;
//        TESTING CREATE - WORKS
//        orm.create(Human.class, levan2, con);
//        orm.create(Human.class, levan3, con); // creates table if it doesnt exist or inserts values  into existing table

//        TESTING READALL - WORKS
        people = orm.getAll(Human.class, con); // get the list of Human objects into the people list
        System.out.println(people.toString()); // use the tostring method to write the contents of people list in a readable way

//        TESTING READBYPK - WORKS
        person = orm.getByPK(Human.class, "3", con);
        System.out.println(person.toString());

//        TESTING UPDATE - works
        Object[] values = {3, "levan3", 40, "unemployed"};
        Field[] columns = (Human.class.getDeclaredFields());
        Object[] newValues = {2, "juka", 20, "banker"};
        Object[] morevalues = {3, "kaxa", 21, "kaci"};
        //orm.update(Human.class,values,columns,morevalues,columns,con);
        //System.out.println(people);
//        TESTING DELETE - WORKS

        orm.delete(Human.class, newValues, columns, con);

    }
}
