package Menus;

import DAOs.ReportsAnalyticsDAO;
import Entities.Doctor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ReportsMenu {

    private final ReportsAnalyticsDAO reportsDAO;
    private final Connection connection;
    private final Scanner scanner;

    public ReportsMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.reportsDAO = new ReportsAnalyticsDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== REPORTS & ANALYTICS MENU ===");
            System.out.println("1. Average length of stay per department");
            System.out.println("2. Doctors and number of patients they have examined");
            System.out.println("3. Number of COVID cases in medical records");
            System.out.println("4. Bed occupancy rate per department");
            System.out.println("5. Average age of admitted patients");
            System.out.println("6. Admissions per department per year");
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
                case 1 -> avgLengthOfStay();
                case 2 -> doctorPatientCounts();
                case 3 -> covidCases();
                case 4 -> bedOccupancy();
                case 5 -> avgAgeAdmitted();
                case 6 -> admissionsPerYear();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void avgLengthOfStay() {
        try {
            HashMap<String, Integer> map = reportsDAO.averageLengthOfStayPerDepartment(connection);
            if (map.isEmpty()) {
                System.out.println("No data.");
                return;
            }
            System.out.println("\nAverage length of stay (days) per department:");
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                System.out.println("- " + e.getKey() + ": " + e.getValue() + " days");
            }
        } catch (SQLException e) {
            System.out.println("Database error while fetching report: " + e.getMessage());
        }
    }

    private void doctorPatientCounts() {
        try {
            HashMap<Doctor, Integer> map = reportsDAO.doctorPatientCounts(connection);
            if (map.isEmpty()) {
                System.out.println("No data.");
                return;
            }
            System.out.println("\nDoctors and number of distinct patients:");
            for (Map.Entry<Doctor, Integer> e : map.entrySet()) {
                Doctor d = e.getKey();
                int count = e.getValue();
                System.out.printf("- %s %s (ID %d, %s): %d patients%n",
                        d.getFirstName(), d.getLastName(),
                        d.getDoctorId(),
                        d.getSpecialty(),
                        count);
            }
        } catch (SQLException e) {
            System.out.println("Database error while fetching doctor stats: " + e.getMessage());
        }
    }

    private void covidCases() {
        try {
            int count = reportsDAO.countCovidCases(connection);
            System.out.println("\nNumber of COVID-related medical records: " + count);
        } catch (SQLException e) {
            System.out.println("Database error while counting COVID cases: " + e.getMessage());
        }
    }

    private void bedOccupancy() {
        try {
            HashMap<String, Double> map = reportsDAO.bedOccupancyRatePerDepartment(connection);
            if (map.isEmpty()) {
                System.out.println("No data.");
                return;
            }
            System.out.println("\nBed occupancy rate per department:");
            for (Map.Entry<String, Double> e : map.entrySet()) {
                String deptName = e.getKey();
                double rate = e.getValue(); // 0.0–1.0
                System.out.printf("- %s: %.2f%%%n", deptName, rate * 100);
            }
        } catch (SQLException e) {
            System.out.println("Database error while fetching occupancy: " + e.getMessage());
        }
    }

    private void avgAgeAdmitted() {
        try {
            double avgAge = reportsDAO.averageAgeOfAdmittedPatients(connection);
            System.out.printf("%nAverage age of admitted patients: %.1f years%n", avgAge);
        } catch (SQLException e) {
            System.out.println("Database error while fetching average age: " + e.getMessage());
        }
    }

    private void admissionsPerYear() {
        try {
            System.out.print("Enter year (e.g. 2025): ");
            int year = Integer.parseInt(scanner.nextLine());

            HashMap<String, Integer> map = reportsDAO.admissionsPerDepartmentPerYear(year, connection);
            if (map.isEmpty()) {
                System.out.println("No admissions found for that year.");
                return;
            }
            System.out.println("\nAdmissions per department for year " + year + ":");
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                System.out.printf("- %s: %d admissions%n", e.getKey(), e.getValue());
            }
        } catch (SQLException e) {
            System.out.println("Database error while fetching admissions: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year format.");
        }
    }
}
