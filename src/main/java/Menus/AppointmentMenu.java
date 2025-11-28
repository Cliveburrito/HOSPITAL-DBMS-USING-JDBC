package Menus;

import DAOs.AppointmentDAO;
import Entities.Appointment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class AppointmentMenu {

    private final AppointmentDAO appointmentDAO;
    private final Connection connection;
    private final Scanner scanner;

    public AppointmentMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.appointmentDAO = new AppointmentDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== APPOINTMENT MENU ===");
            System.out.println("1. List all appointments");
            System.out.println("2. Add new appointment");
            System.out.println("3. Update appointment");
            System.out.println("4. Delete appointment");
            System.out.println("5. List appointments by doctor");
            System.out.println("6. List appointments by patient");
            System.out.println("7. List appointments in date interval (by date only)");
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
                case 2 -> addAppointment();
                case 3 -> updateAppointment();
                case 4 -> deleteAppointment();
                case 5 -> listByDoctor();
                case 6 -> listByPatient();
                case 7 -> listInDateInterval();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void listAll() {
        try {
            List<Appointment> list = appointmentDAO.findAll(connection);
            if (list.isEmpty()) {
                System.out.println("No appointments found.");
                return;
            }
            for (Appointment a : list) {
                System.out.println(a);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing appointments: " + e.getMessage());
        }
    }

    private void addAppointment() {
        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            System.out.print("Doctor ID: ");
            int doctorId = Integer.parseInt(scanner.nextLine());

            System.out.print("Appointment datetime (yyyy-MM-ddTHH:mm, empty for now): ");
            String dtStr = scanner.nextLine();
            LocalDateTime dt = dtStr.isBlank() ? LocalDateTime.now() : LocalDateTime.parse(dtStr);

            System.out.print("Reason: ");
            String reason = scanner.nextLine();

            Appointment a = new Appointment();
            a.setPatientID(patientId);
            a.setDoctorID(doctorId);
            a.setAppointmentDate(dt);
            a.setReason(reason);

            appointmentDAO.insert(a, connection);

            System.out.println("Appointment inserted with ID = " + a.getAppointmentID());
        } catch (SQLException e) {
            System.out.println("Database error while inserting appointment: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void updateAppointment() {
        try {
            System.out.print("Enter appointment ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Appointment existing = appointmentDAO.findById(id, connection);
            if (existing == null) {
                System.out.println("Appointment not found.");
                return;
            }

            System.out.println("Leave field empty to keep current value.");

            System.out.print("New patient ID (" + existing.getPatientID() + "): ");
            String pStr = scanner.nextLine();
            int patientId = pStr.isBlank() ? existing.getPatientID() : Integer.parseInt(pStr);

            System.out.print("New doctor ID (" + existing.getDoctorID() + "): ");
            String dStr = scanner.nextLine();
            int doctorId = dStr.isBlank() ? existing.getDoctorID() : Integer.parseInt(dStr);

            System.out.print("New datetime (" + existing.getAppointmentDate() + ", yyyy-MM-ddTHH:mm): ");
            String dtStr = scanner.nextLine();
            LocalDateTime dt = dtStr.isBlank()
                    ? existing.getAppointmentDate()
                    : LocalDateTime.parse(dtStr);

            System.out.print("New reason (" + existing.getReason() + "): ");
            String reason = scanner.nextLine();
            String finalReason = reason.isBlank() ? existing.getReason() : reason;

            existing.setPatientID(patientId);
            existing.setDoctorID(doctorId);
            existing.setAppointmentDate(dt);
            existing.setReason(finalReason);

            int rows = appointmentDAO.update(existing, connection);
            if (rows == 0) {
                System.out.println("Nothing updated.");
            } else {
                System.out.println("Appointment updated.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating appointment: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    private void deleteAppointment() {
        try {
            System.out.print("Enter appointment ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            int rows = appointmentDAO.delete(id, connection);
            if (rows == 0) {
                System.out.println("No appointment deleted (not found).");
            } else {
                System.out.println("Appointment deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting appointment: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void listByDoctor() {
        try {
            System.out.print("Doctor ID: ");
            int doctorId = Integer.parseInt(scanner.nextLine());

            List<Appointment> list = appointmentDAO.findByDoctor(doctorId, connection);
            if (list.isEmpty()) {
                System.out.println("No appointments for this doctor.");
                return;
            }
            for (Appointment a : list) {
                System.out.println(a);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing by doctor: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void listByPatient() {
        try {
            System.out.print("Patient ID: ");
            int patientId = Integer.parseInt(scanner.nextLine());

            List<Appointment> list = appointmentDAO.findByPatient(patientId, connection);
            if (list.isEmpty()) {
                System.out.println("No appointments for this patient.");
                return;
            }
            for (Appointment a : list) {
                System.out.println(a);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing by patient: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void listInDateInterval() {
        try {
            System.out.print("From date (yyyy-MM-dd): ");
            String fromStr = scanner.nextLine();
            System.out.print("To date (yyyy-MM-dd): ");
            String toStr = scanner.nextLine();

            Date from = Date.valueOf(LocalDate.parse(fromStr));
            Date to = Date.valueOf(LocalDate.parse(toStr));

            List<Appointment> list = appointmentDAO.listAppointmentsInDateInterval(from, to, connection);
            if (list.isEmpty()) {
                System.out.println("No appointments in this interval.");
                return;
            }
            for (Appointment a : list) {
                System.out.println(a);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing interval: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format.");
        }
    }
}
