package Menus

import java.sql.Connection;
import java.util.Scanner;

public class MainMenu {

    private final PatientMenu patientMenu;
    private final DoctorMenu doctorMenu;
    private final DepartmentMenu departmentMenu;
    private final HospitalizationMenu hospitalizationMenu;
    private final AppointmentMenu appointmentMenu;
    private final MedicalRecordMenu medicalRecordMenu;
    private final ReportsMenu reportsMenu;
    private final Connection connection;
    private final Scanner scanner;

    public MainMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.patientMenu = new PatientMenu(connection, scanner);
        this.doctorMenu = new DoctorMenu(connection, scanner);
        this.departmentMenu = new DepartmentMenu(connection, scanner);
        this.hospitalizationMenu = new HospitalizationMenu(connection, scanner);
        this.appointmentMenu = new AppointmentMenu(connection, scanner);
        this.medicalRecordMenu = new MedicalRecordMenu(connection, scanner);
        this.reportsMenu = new ReportsMenu(connection, scanner);
    }

    public void start() {
        while (true) {
            System.out.println("=== MAIN MENU ===");
            System.out.println("1. Patients");
            System.out.println("2. Doctors");
            System.out.println("3. Departments");
            System.out.println("4. Hospitalizations");
            System.out.println("5. Appointments");
            System.out.println("6. Medical Records");
            System.out.println("7. Reports & Analytics");
            System.out.println("0. Exit");
            System.out.print("Choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> patientMenu.show();
                case 2 -> doctorMenu.show();
                case 3 -> departmentMenu.show();
                case 4 -> hospitalizationMenu.show();
                case 5 -> appointmentMenu.show();
                case 6 -> medicalRecordMenu.show();
                case 7 -> reportsMenu.show();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
