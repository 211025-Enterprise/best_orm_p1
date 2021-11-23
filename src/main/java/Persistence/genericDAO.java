package Persistence;
import Annotations.NoNull;
import Annotations.PKey;
import Annotations.Unique;

import java.lang.reflect.*;
import java.util.*;
import java.sql.*;


//  generic DAO interface with crud functionality using reflection
//  accesses or stores information in database

public class genericDAO<T> {
//    CREATE
    /**
     * create a table with class name + s if it doesn't exist or insert new rows into the table
     * @param clazz, the object class to create table of or insert into an existing table
     * @param tObj, object of T class to insert into rows
     * @return number of rows that were edited
     */

    public T create(Class<T> clazz, T tObj, Connection connection) throws SQLException {
        String tableName = clazz.getSimpleName();
        tableName = tableName.toLowerCase();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder table = new StringBuilder(); // building a query string for table creation
        StringBuilder col = new StringBuilder(); // query string for all the columns in the table
        StringBuilder values = new StringBuilder(); // values to insert in each column
        // starting the table creation sql query
        table.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
//        System.out.println(table);
//      query so far "create table if not exists human (
//      all lower case for class names and fields so no need for escape characters
        boolean pk = false;
//        initialize pk to false because we dont have a primary key for the table yet
//        whenever the annotation PKey is read, pk will be set to true so we cant have any other primary keys
        for (Field field: fields){
            String columnName = field.getName().toLowerCase();
            table.append(columnName).append(" ");
            col.append(columnName);
//            System.out.println(field.getType() + " into java type " + javaToSqlType(field.getType()));
            table.append(javaToSqlType(field.getType())).append(" ");
            if (field.isAnnotationPresent(NoNull.class)){
                table.append("NOT NULL");
            }
            if (!pk && field.isAnnotationPresent(PKey.class)){
                table.append("primary key");
                pk = true;
            }
            if (field.isAnnotationPresent(Unique.class)){
                table.append("UNIQUE");
            }
            values.append("?").append(",");
            col.append(",");
            table.append(",\n");
        }
//        delete the trailing , ? \n
        table.deleteCharAt(table.length()-1);
        table.deleteCharAt(table.length()-1).append("\n)");
        col.deleteCharAt(col.length()-1);
        values.deleteCharAt(values.length()-1);
        String create = table.toString();
        String ins = "insert into " + tableName + " (" + col + ")" + " values(" + values + ")";
//        System.out.println(table + " \n "+ ins);
        try (PreparedStatement stmt1 = connection.prepareStatement(create); PreparedStatement stmt2 = connection.prepareStatement(ins)){
            stmt1.executeUpdate();
            int index = 1;
            for (Field field:fields){
                field.setAccessible(true);
                stmt2.setObject(index, field.get(tObj));
                index++;
            }
            if (stmt2.executeUpdate()>0) return tObj;
            else return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

//    READ

    /**
     * takes in the class(table) and returns a list of objects inside this table
     * @param clazz the class we want to read all the objects from
     * @return list of objects
     */
    public List<T> getAll(Class<T> clazz, Connection connection){
        List<T> objList = new ArrayList<>();
        String tableName = clazz.getSimpleName().toLowerCase();
        StringBuilder reader = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        reader.append("select * from ").append(tableName).append(";");
        try(PreparedStatement stmt = connection.prepareStatement(reader.toString())) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
//                find default constructor from declared constructors and initiate an object
                Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors()).
                        filter(cons -> cons.getParameterCount() == 0).findFirst().orElse(null);
                constructor.setAccessible(true);

                T tObj =(T) constructor.newInstance();
//                initialize index to 1 to set objects from resultset
                int index = 1;
                for (Field field : fields){
                    field.setAccessible(true);
                    field.set(tObj,rs.getObject(index));
                    index++;
                }
                objList.add(tObj);
            }
        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return objList;
    }

    /**
     * method to get only certain rows that have the matching pk value
     * @param clazz the class/table name that we want to read from
     * @return object with the matching pk value
     */
    public List<T> getByPK(Class<T> clazz, String pk, Connection connection){
        String tableName = clazz.getSimpleName().toLowerCase();
        StringBuilder gbp = new StringBuilder();
        List<T> pkRow = new ArrayList<>();
        T obj = null;
        gbp.append("select * from ").append(tableName).append(" where ");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            if (field.isAnnotationPresent(PKey.class)){
                gbp.append(field.getName());
            }
        }
        gbp.append(" = ").append(pk);
//        System.out.println(gbp);
        try(PreparedStatement stmt = connection.prepareStatement(gbp.toString())){
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors()).filter(cons -> cons.getParameterCount() == 0).findFirst().orElse(null);
                constructor.setAccessible(true);
                obj = (T) constructor.newInstance();
                int index = 1;
                for (Field field:fields){
                    field.setAccessible(true);
//                    System.out.println(field.getName());
//                    System.out.println(rs.getObject(index));
                    field.set(obj,rs.getObject(index++));
//                    System.out.println(obj);
                }
                pkRow.add(obj);
            }
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return pkRow;
    }
//    UPDATE

    /**
     * method to update a current row in a table
     * @param clazz the class/table that we want to update the objects values of
     * @param values the values that we want to update
     * @param columns columns for the values we want to update
     * @param newValues values that we want to update old values to
     * @param newValueColumns columns that correspond to the new values
     * @param connection connection to the database to execute the sql queries here
     * @return boolean true if a row was updated or false if not
     */
    public boolean update(Class<T> clazz, Object[] values, Field[] columns, Object[] newValues, Field[] newValueColumns,
                          Connection connection){
        String tableName = clazz.getSimpleName().toLowerCase();
//        String builders to build the queries to be executed
        StringBuilder upQuery = new StringBuilder();
        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
//      if the size of the array of columns that we want to change our current data to, start the queries with
//      an opening parenthesis
        if (newValueColumns.length > 1){
            cols.append("(");
            vals.append("(");
        }
//      go through each field in the current columns and append column = ? to the query string
        for (Field col : columns){
            String fName = col.getName().toLowerCase();
            if (upQuery.length() > 0){ upQuery.append(" and ");}
            upQuery.append(fName).append("=?");
        }
//      going through each field in the new columns
        for (Field col : newValueColumns){
            String columnName = col.getName().toLowerCase();
            cols.append(columnName).append(",");
            vals.append("?").append(",");
        }
//      delete trailing commas, last characters in cols and vals
        cols.deleteCharAt(cols.length()-1);
        vals.deleteCharAt(vals.length()-1);
        if (newValueColumns.length > 1){
            cols.append(")");
            vals.append(")");
        }
//      sql query string
        String sql = "update "+tableName+" set "+cols+" = "+vals+" where "+upQuery.toString();
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            int index = 1;
            for (Object tObj : newValues){
                stmt.setObject(index, tObj);
                index ++;
            }
            for (Object tObj : values){
                stmt.setObject(index, tObj);
                index++;
            }
            int rowsaffected = stmt.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
//    DELETE

    /**
     * delete a row from the class table with the corresponding values
     * @param clazz class we want to delete values from
     * @param values rows we want to delete
     * @param fieldNames column names that correspond to the values
     * @param connection get connection and execute query
     * @return boolean, true if deleted, false if not
     */
    public boolean delete(Class<?> clazz,Object[] values, Field[] fieldNames, Connection connection){
        boolean retValue = false;
        if (values.length != fieldNames.length){
            System.out.println("no can do");
            return false;
        }
        String tableName = clazz.getSimpleName().toLowerCase();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder delQuery = new StringBuilder();

        for (Field field:fieldNames){
            if (delQuery.length() > 0)  delQuery.append(" and ");
            String name = field.getName().toLowerCase();
            delQuery.append(name).append(" =?");
        }
        String deleteQuery = "delete from " + tableName + " where " + delQuery;
        try(PreparedStatement stmt = connection.prepareStatement(deleteQuery)){
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
//    service method
    public String javaToSqlType(Type type){
        String javaT = type.getTypeName();
        String sqlT = null;
        switch (javaT){
            case "char":
                sqlT = "varchar(1)";
                break;
            case "short":
                sqlT = "smallint";
                break;
            case "int":
                sqlT = "int";
                break;
            case "long":
                sqlT = "bigint";
                break;
            case "float":
                sqlT = "real";
                break;
            case "double":
                sqlT = "double precision";
                break;
            case "boolean":
                sqlT = "bool";
                break;
            case "java.lang.String":
                sqlT = "varchar(50)";// we wont need strings longer than 50 characters
                break;
            default:
                System.out.println("well well well, look who's using fancy data types");
                break;
        }
        return sqlT;
    }
}
