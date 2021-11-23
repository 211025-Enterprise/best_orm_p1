import Persistence.genericDAO;
import Utility.Connection1;
import model_example.Human;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Driver {
    public static void main(String[] args) throws SQLException {
        System.out.println("create works so far");
        Human levan2 = new Human(2, "levan2", 25, "software engineer");
        Connection con = Connection1.getConnection();
        genericDAO orm = new genericDAO();
        List<Human> people = new ArrayList<>();
        //orm.create(Human.class, levan2, con);
        people = orm.getAll(Human.class, con);
        System.out.println(people.toString());
    }
}
