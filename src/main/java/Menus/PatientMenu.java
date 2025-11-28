package Menus;

import DAOs.PatientDAO;
import Entities.Patient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PatientMenu {

    private final PatientDAO patientDAO;
    private final Connection connection;
    private final Scanner scanner;

    public PatientMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.patientDAO = new PatientDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== PATIENT MENU ===");
            System.out.println("1. List all patients");
            System.out.println("2. Add new patient");
            System.out.println("3. Update patient");
            System.out.println("4. Delete patient");
            System.out.println("5. Find patient by ID");
            System.out.println("6. Find patient by AMKA");
            System.out.println("7. Count patients");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
                continue;
            }

            switch (choice) {
                case 1 -> listAllPatients();
                case 2 -> addPatient();
                case 3 -> updatePatient();
                case 4 -> deletePatient();
                case 5 -> findPatientById();
                case 6 -> findPatientByAmka();
                case 7 -> countPatients();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listAllPatients() {
        try {
            List<Patient> patients = patientDAO.findAll(connection);
            if (patients.isEmpty()) {
                System.out.println("No patients found.");
                return;
            }
            for (Patient p : patients) {
                System.out.print(p);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing patients: " + e.getMessage());
        }
    }

    private void addPatient() {
        try {
            System.out.print("First name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Date of birth (yyyy-MM-dd, empty for null): ");
            String dobStr = scanner.nextLine();

            System.out.print("Gender (M/F/OTHER or empty for null): ");
            String gender = scanner.nextLine();

            System.out.print("Phone (digits or empty for null): ");
            String phone = scanner.nextLine();

            System.out.print("AMKA (digits, required): ");
            String amka = scanner.nextLine();

            Patient p = new Patient();
            p.setFirstName(firstName);
            p.setLastName(lastName);
            if (!dobStr.isBlank()) {
                p.setDateOfBirth(java.time.LocalDate.parse(dobStr));
            }
            p.setGender(gender.isBlank() ? null : gender);
            p.setPhone(phone.isBlank() ? null : phone);
            p.setAmka(amka);

            patientDAO.insertPatient(p, connection);

            System.out.println("Inserted patient with ID = " + p.getPatientId());
        } catch (SQLException e) {
            System.out.println("Database error while inserting patient: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void updatePatient() {
        try {
            System.out.print("Enter patient ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Patient existing = patientDAO.findByID(id, connection);
            if (existing == null) {
                System.out.println("Patient not found.");
                return;
            }

            System.out.println("Updating patient: " + existing.getFirstName() + " " + existing.getLastName());

            while (true) {
                System.out.println("\nChoose field to update:");
                System.out.println("1. First name");
                System.out.println("2. Last name");
                System.out.println("3. Date of birth");
                System.out.println("4. Gender");
                System.out.println("5. Phone");
                System.out.println("6. AMKA");
                System.out.println("0. Finish");
                System.out.print("Choice: ");

                String input = scanner.nextLine();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number.");
                    continue;
                }

                if (choice == 0) {
                    System.out.println("Done updating.");
                    return;
                }

                String field = null;
                Object value = null;

                switch (choice) {
                    case 1 -> {
                        field = "first_name";
                        System.out.print("New first name: ");
                        value = scanner.nextLine();
                    }
                    case 2 -> {
                        field = "last_name";
                        System.out.print("New last name: ");
                        value = scanner.nextLine();
                    }
                    case 3 -> {
                        field = "date_of_birth";
                        System.out.print("New date of birth (yyyy-MM-dd, empty for null): ");
                        String d = scanner.nextLine();
                        value = d.isBlank() ? null : Date.valueOf(d);
                    }
                    case 4 -> {
                        field = "gender";
                        System.out.print("New gender (M/F/OTHER or empty for null): ");
                        String g = scanner.nextLine();
                        value = g.isBlank() ? null : g;
                    }
                    case 5 -> {
                        field = "phone";
                        System.out.print("New phone (digits or empty for null): ");
                        String ph = scanner.nextLine();
                        value = ph.isBlank() ? null : ph;
                    }
                    case 6 -> {
                        field = "amka";
                        System.out.print("New AMKA (digits): ");
                        value = scanner.nextLine();
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        continue;
                    }
                }

                int rows = patientDAO.updatePatientField(id, field, value, connection);
                if (rows == 0) {
                    System.out.println("Nothing updated.");
                } else {
                    System.out.println("Field updated successfully.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void deletePatient() {
        try {
            System.out.print("Enter patient ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());
            int rows = patientDAO.deletePatientById(id, connection);
            if (rows == 0) {
                System.out.println("No patient deleted (not found or FK constraint).");
            } else {
                System.out.println("Patient deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void findPatientById() {
        try {
            System.out.print("Enter patient ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Patient p = patientDAO.findByID(id, connection);
            if (p == null) {
                System.out.println("Patient not found.");
            } else {
                System.out.print(p);
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void findPatientByAmka() {
        try {
            System.out.print("Enter AMKA: ");
            String amka = scanner.nextLine();

            Patient p = patientDAO.findByAMKA(amka, connection);
            if (p == null) {
                System.out.println("Patient not found.");
            } else {
                System.out.print(p);
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding patient by AMKA: " + e.getMessage());
        }
    }

    private void countPatients() {
        try {
            int count = patientDAO.numberOfPatients(connection);
            System.out.println("Total patients: " + count);
        } catch (SQLException e) {
            System.out.println("Database error while counting patients: " + e.getMessage());
        }
    }
}
