import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn != null) {
                System.out.println("Connected successfully!");
                String sqlCommand = "SELECT * FROM patient;";
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(sqlCommand);
                System.out.printf("%50s" ,"*** PATIENTS ***");
                System.out.println("\n");
                System.out.printf("%-4s %-15s %-15s %-12s %-8s %-15s %-15s%n",
                        "ID", "First Name", "Last Name", "DOB", "Gender", "Phone", "AMKA");
                System.out.println("------------------------------------------" +
                        "--------------------------------------------");

                while (rs.next()) {
                    int id = rs.getInt("patient_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    Date dob = rs.getDate("date_of_birth");
                    String gender = rs.getString("gender");
                    String phone = rs.getString("phone");
                    String amka = rs.getString("amka");

                    System.out.printf("%-4d %-15s %-15s %-12s %-8s %-15s %-15s%n",
                            id, firstName, lastName, dob, gender, phone, amka);
                }

                /*PatientDAO pDao = new PatientDAO();
                Patient p = new Patient();
                p.setAmka("121221");
                p.setFirstName("HELLO");
                p.setLastName("SS23");
                p.setGender("M");
                p.setPhone("6979584323");
                LocalDate ld = LocalDate.of(2000, 3, 16);
                p.setDateOfBirth(ld);


                Patient patient = pDao.insertPatient(p, conn);
                if(patient == null) {
                    System.out.println("FAILURE");
                }
                else {
                    System.out.println("Patient : " + p.toString() + " insterted\nBRAVOOO");
                }*/

                PatientDAO pDao = new PatientDAO();
                System.out.println("Please enter the id of the patient you would like to update -> ");
                Scanner sc = new Scanner(System.in);
                int id = sc.nextInt();
                Patient p = pDao.findByID(id, conn);
                if(p==null) {
                    System.out.println("Invalid ID!");
                }
                else {
                    System.out.println("Patient with ID:" + id + " "+ p.toString());
                }
                Map<String, Object> fields = new HashMap<>();
                System.out.println("What data would you like to update:\n" +
                        "1. First Name\n" +
                                "2. Last Name\n" +
                                "3. Date Of Birth\n" +
                                "4. Gender\n" +
                                "5. Phone\n" +
                                "6. AMKA");
                int option = sc.nextInt();
                switch (option) {
                    case 1 -> {
                        System.out.println("Enter new first name: ");
                        Scanner scanner = new Scanner(System.in);
                        String s = scanner.nextLine();
                        fields.put("first_name", s);
                    }

                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sqlCommand = "Select * From Patients;";
        HashMap<String, Object> map = new HashMap<>();
        java.util.Date date = new java.util.Date();

        map.put("Serres" , date);
        map.put("KAVALA" , "paok");
        map.put("MORDOR" , 796445);

        System.out.println(map.get("Serres").toString());
        System.out.println(map.get("KAVALA").toString());
        System.out.println(map.get("MORDOR").toString());


    }
}