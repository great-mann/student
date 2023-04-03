package jdbc;

import java.sql.*;

public class DBVerbindung {
    private Connection con=null;
    private String dbAdresse="localhost"; //localhost
    private String dbInstanz="hochschule"; //
    public void verbinden(String nutzer, String passwort){
        try {

            con=DriverManager
                    .getConnection("jdbc:mariadb://"	+	dbAdresse	+	"/"
                            +	dbInstanz,	nutzer,	passwort);
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }
    }
    private void ausnahmeAusgeben(SQLException e){
        while (e != null){
            System.err.println("Fehlercode: "+e.getErrorCode());
            System.err.println("SQL State: "+e.getSQLState());
            System.err.println(e);
            e = e.getNextException();
        }
    }
    public void verbindungTrennen(){
        if (con == null){
            System.out.println("eh keine Verbindung vorhanden");
            return;
        }
        try {
            con.close();
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }
    }
    public void verbindungAnalysieren(){
        if (con==null){
            System.out.println("keine Verbindung vorhanden");
            return;
        }
        try {DatabaseMetaData dbmd=con.getMetaData();
            System.out.println("DB-Name: "+ dbmd.getDatabaseProductName()
                    +"\nDB-Version: "+dbmd.getDatabaseMajorVersion()
                    +"\nDB-Release: "+dbmd.getDriverMinorVersion()
                    +"\nTransaktionen erlaubt: "+dbmd.supportsTransactions()
                    +"\nbeachtet GroßKlein: "+dbmd.storesMixedCaseIdentifiers()
                    +"\nunterstützt UNION: "+dbmd.supportsUnion()
                    +"\nmax. Prozedurname: "+dbmd.getMaxProcedureNameLength());
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }
    }
    public void anfragen(String anfrage){
        if (con==null){
            System.out.println("keine Verbindung");
            return;
        }
        try {
            Statement stmt=con.createStatement();
            ResultSet rs= stmt.executeQuery(anfrage);
//Metadaten des Anfrageergebnisses
            ResultSetMetaData rsmd= rs.getMetaData();
            int spalten=rsmd.getColumnCount();

//Ergebnisausgabe
            while(rs.next()){
                for(int i=1;i<=spalten; i++)
                    System.out.print(rs.getString(i)+" ");
                System.out.print("\n");
            }
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }
    }
    public void neueTabelle(String name,
                            String[] spalten, int zeilen){
        if (con==null){
            System.out.println("keine Verbindung");
            return;
        }
        try {

            Statement stmt=con.createStatement();
            StringBuffer sb=
                    new StringBuffer("CREATE TABLE ");
            sb.append(name);
            sb.append("(");

            sb.append(spalten[0]);
            sb.append(" INTEGER,");


            sb.append(spalten[1]);
            sb.append(" VARCHAR(16),");

            sb.append(spalten[spalten.length-1]);
            sb.append(" VARCHAR(16),");

            sb.append(" CONSTRAINT PK_Student PRIMARY KEY(Matnr))");
            System.out.println(sb); // zur Info

            stmt.executeUpdate(sb.toString());

            con.commit();
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }}
    public void StudErg(int matnr, String name, String Semester){
        if (con==null){
            System.out.println("keine Verbindung");
            return;
        }
        try {
            Statement stmt=con.createStatement();

            StringBuffer in=
                    new StringBuffer("INSERT INTO ");
            in.append("Student");
            in.append(" VALUES(");
            in.append(matnr);
            in.append(", ");
            in.append("'");
            in.append(name);
            in.append("'");
            in.append(", ");
            in.append("'");
            in.append(Semester);
            in.append("'");
            in.append(")");



            System.out.println(in); // zur Info
            stmt.executeUpdate(in.toString());
            con.commit();
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }
    }
    public void StudierendenAendern(int matnr, String name) {
        if (con == null) {
            System.out.println("keine Verbindung");
            return;
        }
        try {
            String update = "UPDATE Student "
                    + "SET name = ? "
                    + "WHERE matnr = ?";

            PreparedStatement prep=con.prepareStatement(update);
            prep.setString(1, name);
            prep.setInt(2, matnr);
            prep.executeUpdate();
            con.commit();

        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }
    }
    public void StudierendenLoeschen() {
        if (con == null) {
            System.out.println("keine Verbindung");
            return;
        }
        try {
            Statement stmt=con.createStatement();

            StringBuffer in =
                    new StringBuffer("TRUNCATE TABLE Student ");
            stmt.executeUpdate(in.toString());
            con.commit();
            System.out.println("Student Tabelle Geloescht");

        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }


    }
    public boolean tableExists(String tableName){
        if (con==null){
            System.out.println("keine Verbindung vorhanden");
            return false;
        }
        try {DatabaseMetaData dbmd=con.getMetaData();
            ResultSet rs =  dbmd.getTables(null, null, tableName, null);
            return rs.next();
        } catch (SQLException e) {
            ausnahmeAusgeben(e);
        }


        return false;
    }






    public static void main(String[] s){
        DBVerbindung db=new DBVerbindung();
        db.verbinden("hr", "abcdef!$39");




        if(db.tableExists("Student") == false) {
            String spalten[] = {"Matnr", "Name", "Semester"};
            db.neueTabelle("Student", spalten, 2);
            db.verbindungAnalysieren();
        }


        int antwort = -1;

        while(antwort != 0){
        System.out.println("Was wollen Sie?");
        System.out.println("Program beenden (0)");
        System.out.println("neuen Studierenden hinzufuegen (1)");
        System.out.println("alle Studierenden zeigen (2)");
        System.out.println("Namen eines Studierenden aendern (3)");
        System.out.println("alle Studierenden loeschen (4)");
        antwort = Input.getInt();

        switch(antwort) {
            case 0: break;
            case 1: System.out.println("Matrikel Nummer: ");
                    int matnr = Input.getInt();
                    System.out.println("Name: ");
                    String name = Input.getString();
                    System.out.println("Semester: ");
                    String semester = Input.getString();
                    db.StudErg(matnr, name, semester);
                    System.out.println("Studierenden Hinzugefuegt: ");

                    break;
            case 2: db.anfragen("SELECT name FROM Student");
                    break;
            case 3: System.out.println("Matrikel Nummer: ");
                    int matnr2 = Input.getInt();
                    System.out.println("neuer Name: ");
                    String name2 = Input.getString();
                    db.StudierendenAendern(matnr2, name2);
                    break;
            case 4: db.StudierendenLoeschen();
                    break;


            }
        }





        db.verbindungTrennen();
    }
}
