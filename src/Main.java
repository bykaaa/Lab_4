import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void testClassloader(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        ClassLoaderTest cl = new ClassLoaderTest();
        Class<?> c1 = cl.findClass("ClassTest");
        Object ob = c1.newInstance();
        Method method = c1.getMethod("classLoaded", String.class);
        method.invoke(ob, "ClassTest");
    }

    private static void Print_db(Main_db db, ResultSet rs) {
        if (rs != null) {
            try {
                System.out.println();
                while (rs.next()) {
                    System.out.println("ID = "+rs.getInt(1)+"; Name = "+rs.getString(2) + "; Counter = " + rs.getInt(3));
                }
            } catch (SQLException e) {
                db.close();
                e.printStackTrace();
            }
        }
    }

    private static void test_db() {
        Main_db db = new Main_db();
        db.connect();

        db.createTable("test", "(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, counter INTEGER DEFAULT 0)");
        db.update("INSERT INTO test (name, counter) VALUES ('Egor', 5)");
        db.update("INSERT INTO test (name, counter) VALUES ('Kolya', 55)");
        db.commit();

        ResultSet rs = db.select("SELECT * from test");
        Print_db(db, rs);

        db.update("UPDATE test SET counter = 555 WHERE name = 'Kolya'");
        rs = db.select("SELECT * from test");
        Print_db(db, rs);

        db.update("DELETE FROM test WHERE name = 'Egor'");
        rs = db.select("SELECT * from test");
        Print_db(db, rs);
        db.commit();

        //Откат транзакции
        db.update("DELETE FROM test WHERE name = 'Egor'");
        db.rollback();
        rs = db.select("SELECT * from test");
        Print_db(db, rs);

        //Некорректный запрос
        db.update("DELETE FROM test WHERE name = 'Kolya");

        db.close();
    }

    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        testClassloader(args);
        test_db();
    }
}