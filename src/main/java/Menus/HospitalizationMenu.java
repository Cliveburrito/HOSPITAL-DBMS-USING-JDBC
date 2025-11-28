package Menus;

import DAOs.HospitalizationDAO;
import Entities.Hospitalization;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class HospitalizationMenu {

    private final HospitalizationDAO hospitalizationDAO;
    private final Connection connection;
    private final Scanner scanner;

    public HospitalizationMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.hospitalizationDAO = new HospitalizationDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== HOSPITALIZATION MENU ===");
            System.out.println("1. List all hospitalizations");
            System.out.println("2. List current hospitalizations");
            System.out.println("3. List hospitalizations by patient ID");
            System.out.println("4. Find current hospitalization by patient AMKA");
            System.out.println("5. Admit new hospitalization");
            System.out.println("6. Update hospitalization (single field)");
            System.out.println("7. Discharge hospitalization");
            System.out.println("8. Delete hospitalization");
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
                case 1 -> listAll();
                case 2 -> listCurrent();
                case 3 -> listByPatientId();
                case 4 -> findCurrentByAmka();
                case 5 -> admit();
                case 6 -> updateHospitalization();
                case 7 -> discharge();
                case 8 -> deleteHospitalization();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listAll() {
        try {
            List<Hospitalization> list = hospitalizationDAO.findAll(connection);
            if (list.isEmpty()) {
                System.out.println("No hospitalizations found.");
                return;
            }
            for (Hospitalization h : list) {
                System.out.println(h);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing hospitalizations: " + e.getMessage());
        }
    }

    private void listCurrent() {
        try {
            List<Hospitalization> list = hospitalizationDAO.findCurrentHospitalizations(connection);
            if (list.isEmpty()) {
                System.out.println("No currently admitted patients.");
                return;
            }
            for (Hospitalization h : list) {
                System.out.println(h);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing current hospitalizations: " + e.getMessage());
        }
    }

    private void listByPatientId() {
        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            List<Hospitalization> list = hospitalizationDAO.findByPatientId(patientId, connection);
            if (list.isEmpty()) {
                System.out.println("No hospitalizations for this patient.");
                return;
            }
            for (Hospitalization h : list) {
                System.out.println(h);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing by patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void findCurrentByAmka() {
        try {
            System.out.print("Patient AMKA: ");
            String amka = scanner.nextLine();

            CurrentHospitalizationSummary summary =
                    hospitalizationDAO.findCurrentlyHospitalizedByPatientAmka(amka, connection);

            if (summary == null) {
                System.out.println("Patient is not currently hospitalized.");
            } else {
                System.out.println(summary); // assuming toString or print fields manually
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding current hospitalization: " + e.getMessage());
        }
    }

    private void admit() {
        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            System.out.print("Department ID: ");
            int departmentId = Integer.parseInt(scanner.nextLine());

            System.out.print("Admit datetime (yyyy-MM-ddTHH:mm), or empty for now: ");
            String dtStr = scanner.nextLine();

            LocalDateTime admitDateTime = dtStr.isBlank()
                    ? LocalDateTime.now()
                    : LocalDateTime.parse(dtStr);

            System.out.print("Bed number: ");
            String bedNumber = scanner.nextLine();

            System.out.print("Reason: ");
            String reason = scanner.nextLine();

            Hospitalization h = new Hospitalization();
            h.setPatientId(patientId);
            h.setDepartmentId(departmentId);
            h.setAdmitDateTime(admitDateTime);
            h.setBedNumber(bedNumber);
            h.setReason(reason);

            hospitalizationDAO.insert(h, connection);

            System.out.println("Hospitalization inserted with ID = " + h.getHospitalizationId());
        } catch (SQLException e) {
            System.out.println("Database error while admitting: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void updateHospitalization() {
        try {
            System.out.print("Enter hospitalization ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Hospitalization existing = hospitalizationDAO.findById(id, connection);
            if (existing == null) {
                System.out.println("Hospitalization not found.");
                return;
            }

            while (true) {
                System.out.println("\nChoose field to update:");
                System.out.println("1. Patient ID");
                System.out.println("2. Department ID");
                System.out.println("3. Admit datetime");
                System.out.println("4. Discharge datetime");
                System.out.println("5. Bed number");
                System.out.println("6. Reason");
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
                        field = "patient_id";
                        System.out.print("New patient ID: ");
                        value = Integer.parseInt(scanner.nextLine());
                    }
                    case 2 -> {
                        field = "department_id";
                        System.out.print("New department ID: ");
                        value = Integer.parseInt(scanner.nextLine());
                    }
                    case 3 -> {
                        field = "admit_datetime";
                        System.out.print("New admit datetime (yyyy-MM-ddTHH:mm): ");
                        String s = scanner.nextLine();
                        LocalDateTime dt = LocalDateTime.parse(s);
                        value = Timestamp.valueOf(dt);
                    }
                    case 4 -> {
                        field = "discharge_datetime";
                        System.out.print("New discharge datetime (yyyy-MM-ddTHH:mm, empty for NULL): ");
                        String s = scanner.nextLine();
                        if (s.isBlank()) value = null;
                        else value = Timestamp.valueOf(LocalDateTime.parse(s));
                    }
                    case 5 -> {
                        field = "bed_number";
                        System.out.print("New bed number: ");
                        value = scanner.nextLine();
                    }
                    case 6 -> {
                        field = "reason";
                        System.out.print("New reason: ");
                        value = scanner.nextLine();
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        continue;
                    }
                }

                int rows = hospitalizationDAO.updateHospitalizationField(id, field, value, connection);
                if (rows == 0) System.out.println("Nothing updated.");
                else System.out.println("Updated successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating hospitalization: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void discharge() {
        try {
            System.out.print("Enter hospitalization ID to discharge: ");
            int id = Integer.parseInt(scanner.nextLine());

            int rows = hospitalizationDAO.discharge(id, connection);
            if (rows == 0) {
                System.out.println("Nothing updated (maybe already discharged or not found).");
            } else {
                System.out.println("Patient discharged.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while discharging: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void deleteHospitalization() {
        try {
            System.out.print("Enter hospitalization ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            int rows = hospitalizationDAO.deleteById(id, connection);
            if (rows == 0) {
                System.out.println("No hospitalization deleted (not found).");
            } else {
                System.out.println("Hospitalization deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting hospitalization: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }
}
