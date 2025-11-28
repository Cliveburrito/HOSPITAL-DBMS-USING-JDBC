package Menus;

import DAOs.MedicalRecordDAO;
import Entities.MedicalRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MedicalRecordMenu {

    private final MedicalRecordDAO medicalRecordDAO;
    private final Connection connection;
    private final Scanner scanner;

    public MedicalRecordMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.medicalRecordDAO = new MedicalRecordDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== MEDICAL RECORD MENU ===");
            System.out.println("1. List all medical records");
            System.out.println("2. List records by patient ID");
            System.out.println("3. Add new medical record");
            System.out.println("4. Update medical record (field by field)");
            System.out.println("5. Delete medical record");
            System.out.println("6. Find record by ID");
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
                case 2 -> listByPatient();
                case 3 -> addRecord();
                case 4 -> updateRecord();
                case 5 -> deleteRecord();
                case 6 -> findById();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listAll() {
        try {
            List<MedicalRecord> list = medicalRecordDAO.findAll(connection);
            if (list.isEmpty()) {
                System.out.println("No medical records found.");
                return;
            }
            for (MedicalRecord mr : list) {
                System.out.println(mr);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing medical records: " + e.getMessage());
        }
    }

    private void listByPatient() {
        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            List<MedicalRecord> list = medicalRecordDAO.findByPatientId(patientId, connection);
            if (list.isEmpty()) {
                System.out.println("No records for this patient.");
                return;
            }
            for (MedicalRecord mr : list) {
                System.out.println(mr);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing by patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void addRecord() {
        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            System.out.print("Doctor ID (empty for null): ");
            String docStr = scanner.nextLine();
            Integer doctorId = docStr.isBlank() ? null : Integer.parseInt(docStr);

            System.out.print("Diagnosis: ");
            String diagnosis = scanner.nextLine();

            System.out.print("Treatment: ");
            String treatment = scanner.nextLine();

            System.out.print("Notes (optional): ");
            String notes = scanner.nextLine();

            MedicalRecord mr = new MedicalRecord();
            mr.setPatientId(patientId);
            mr.setDoctorId(doctorId);
            mr.setDiagnosis(diagnosis);
            mr.setTreatment(treatment);
            mr.setNotes(notes);

            // record_datetime μάλλον default NOW() στη βάση
            medicalRecordDAO.insertMedicalRecord(mr, connection);

            System.out.println("Medical record inserted with ID = " + mr.getRecordId());
        } catch (SQLException e) {
            System.out.println("Database error while inserting medical record: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void updateRecord() {
        try {
            System.out.print("Enter record ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            MedicalRecord existing = medicalRecordDAO.findById(id, connection);
            if (existing == null) {
                System.out.println("Medical record not found.");
                return;
            }

            while (true) {
                System.out.println("\nChoose field to update:");
                System.out.println("1. Patient ID");
                System.out.println("2. Doctor ID");
                System.out.println("3. Record datetime");
                System.out.println("4. Diagnosis");
                System.out.println("5. Treatment");
                System.out.println("6. Notes");
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
                        field = "doctor_id";
                        System.out.print("New doctor ID (empty for null): ");
                        String s = scanner.nextLine();
                        value = s.isBlank() ? null : Integer.parseInt(s);
                    }
                    case 3 -> {
                        field = "record_datetime";
                        System.out.print("New datetime (yyyy-MM-ddTHH:mm): ");
                        String s = scanner.nextLine();
                        LocalDateTime dt = LocalDateTime.parse(s);
                        value = Timestamp.valueOf(dt);
                    }
                    case 4 -> {
                        field = "diagnosis";
                        System.out.print("New diagnosis: ");
                        value = scanner.nextLine();
                    }
                    case 5 -> {
                        field = "treatment";
                        System.out.print("New treatment: ");
                        value = scanner.nextLine();
                    }
                    case 6 -> {
                        field = "notes";
                        System.out.print("New notes: ");
                        value = scanner.nextLine();
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        continue;
                    }
                }

                int rows = medicalRecordDAO.updateMedicalRecordField(id, field, value, connection);
                if (rows == 0) {
                    System.out.println("Nothing updated.");
                } else {
                    System.out.println("Field updated successfully.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating record: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void deleteRecord() {
        try {
            System.out.print("Enter record ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            int rows = medicalRecordDAO.deleteById(id, connection);
            if (rows == 0) {
                System.out.println("No record deleted (not found).");
            } else {
                System.out.println("Medical record deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting record: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void findById() {
        try {
            System.out.print("Enter record ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            MedicalRecord mr = medicalRecordDAO.findById(id, connection);
            if (mr == null) {
                System.out.println("Medical record not found.");
            } else {
                System.out.println(mr);
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding record: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }
}
