package Menus;
import Entities.Doctor;
import DAOs.DoctorDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class DoctorMenu {

    private final DoctorDAO doctorDAO;
    private final Connection connection;
    private final Scanner scanner;

    public DoctorMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.doctorDAO = new DoctorDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== DOCTOR MENU ===");
            System.out.println("1. List all doctors");
            System.out.println("2. Add new doctor");
            System.out.println("3. Update doctor");
            System.out.println("4. Delete doctor");
            System.out.println("5. Find doctor by ID");
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
                case 1 -> listAllDoctors();
                case 2 -> addDoctor();
                case 3 -> updateDoctor();
                case 4 -> deleteDoctor();
                case 5 -> findDoctorById();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ================= LIST =================
    private void listAllDoctors() {
        try {
            List<Doctor> doctors = doctorDAO.findAll(connection);
            if (doctors.isEmpty()) {
                System.out.println("No doctors found.");
                return;
            }
            for (Doctor d : doctors) {
                System.out.print(d);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing doctors: " + e.getMessage());
        }
    }

    // ================= INSERT =================
    private void addDoctor() {
        try {
            System.out.print("First name: ");
            String firstName = scanner.nextLine();

            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            System.out.print("Specialty: ");
            String specialty = scanner.nextLine();

            System.out.print("Phone (digits or empty for null): ");
            String phone = scanner.nextLine();

            System.out.print("Department ID (or empty for null): ");
            String deptInput = scanner.nextLine();

            Integer deptId = deptInput.isBlank() ? null : Integer.parseInt(deptInput);

            Doctor d = new Doctor(
                    firstName,
                    lastName,
                    specialty,
                    phone.isBlank() ? null : phone,
                    deptId
            );

            doctorDAO.insertDoctor(d, connection);

            System.out.println("Doctor inserted with ID = " + d.getDoctorId());
        } catch (SQLException e) {
            System.out.println("Database error while inserting doctor: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    // ================= UPDATE (dynamic) =================
    private void updateDoctor() {
        try {
            System.out.print("Enter doctor ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            var opt = doctorDAO.findById(id, connection);
            if (opt.isEmpty()) {
                System.out.println("Doctor not found.");
                return;
            }

            Doctor existing = opt.get();
            System.out.println("Updating doctor: " + existing.getFirstName() + " " + existing.getLastName());

            while (true) {
                System.out.println("\nChoose field to update:");
                System.out.println("1. First name");
                System.out.println("2. Last name");
                System.out.println("3. Specialty");
                System.out.println("4. Phone");
                System.out.println("5. Department ID");
                System.out.println("0. Finish");
                System.out.print("Choice: ");

                int choice = Integer.parseInt(scanner.nextLine());
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
                        field = "specialty";
                        System.out.print("New specialty: ");
                        value = scanner.nextLine();
                    }
                    case 4 -> {
                        field = "phone";
                        System.out.print("New phone (empty for null): ");
                        String p = scanner.nextLine();
                        value = p.isBlank() ? null : p;
                    }
                    case 5 -> {
                        field = "department_id";
                        System.out.print("New department id (empty for null): ");
                        String d = scanner.nextLine();
                        value = d.isBlank() ? null : Integer.parseInt(d);
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        continue;
                    }
                }

                int rows = doctorDAO.updateDoctorField(id, field, value, connection);
                if (rows == 0) System.out.println("Nothing updated.");
                else System.out.println("Updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating doctor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    // ================= DELETE =================
    private void deleteDoctor() {
        try {
            System.out.print("Enter doctor ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            int rows = doctorDAO.deleteById(id, connection);
            if (rows == 0) {
                System.out.println("No doctor deleted (not found).");
            } else {
                System.out.println("Doctor deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting doctor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    // ================= FIND BY ID =================
    private void findDoctorById() {
        try {
            System.out.print("Enter doctor ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            var opt = doctorDAO.findById(id, connection);
            if (opt.isEmpty()) {
                System.out.println("Doctor not found.");
            } else {
                System.out.print(opt.get());
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding doctor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }
}
