package Menus;

import DAOs.DepartmentDAO;
import Entities.Department;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class DepartmentMenu {

    private final DepartmentDAO departmentDAO;
    private final Connection connection;
    private final Scanner scanner;

    public DepartmentMenu(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
        this.departmentDAO = new DepartmentDAO();
    }

    public void show() {
        while (true) {
            System.out.println("\n=== DEPARTMENT MENU ===");
            System.out.println("1. List all departments");
            System.out.println("2. Add new department");
            System.out.println("3. Update department");
            System.out.println("4. Delete department");
            System.out.println("5. Find department by ID");
            System.out.println("6. Find department by name");
            System.out.println("7. Count departments");
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
                case 1 -> listAllDepartments();
                case 2 -> addDepartment();
                case 3 -> updateDepartment();
                case 4 -> deleteDepartment();
                case 5 -> findDepartmentById();
                case 6 -> findDepartmentByName();
                case 7 -> countDepartments();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ============== LIST ==============
    private void listAllDepartments() {
        try {
            List<Department> departments = departmentDAO.findAll(connection);
            if (departments.isEmpty()) {
                System.out.println("No departments found.");
                return;
            }
            for (Department d : departments) {
                System.out.print(d);
            }
        } catch (SQLException e) {
            System.out.println("Database error while listing departments: " + e.getMessage());
        }
    }

    // ============== INSERT ==============
    private void addDepartment() {
        try {
            System.out.print("Department name: ");
            String name = scanner.nextLine();

            System.out.print("Capacity (empty for null): ");
            String capInput = scanner.nextLine();

            Integer capacity = capInput.isBlank() ? null : Integer.parseInt(capInput);

            Department d = new Department();
            d.setName(name);
            d.setCapacity(capacity);

            departmentDAO.insertDepartment(d, connection);

            System.out.println("Department inserted with ID = " + d.getDepartmentId());
        } catch (SQLException e) {
            System.out.println("Database error while inserting department: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    // ============== UPDATE (dynamic) ==============
    private void updateDepartment() {
        try {
            System.out.print("Enter department ID to update: ");
            int id = Integer.parseInt(scanner.nextLine());

            Department existing = departmentDAO.findById(id, connection);
            if (existing == null) {
                System.out.println("Department not found.");
                return;
            }

            System.out.println("Updating department: " + existing.getName());

            while (true) {
                System.out.println("\nChoose field to update:");
                System.out.println("1. Name");
                System.out.println("2. Capacity");
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
                        field = "name";
                        System.out.print("New name: ");
                        value = scanner.nextLine();
                    }
                    case 2 -> {
                        field = "capacity";
                        System.out.print("New capacity (empty for null): ");
                        String capInput = scanner.nextLine();
                        value = capInput.isBlank() ? null : Integer.parseInt(capInput);
                    }
                    default -> {
                        System.out.println("Invalid choice.");
                        continue;
                    }
                }

                int rows = departmentDAO.updateDepartmentField(id, field, value, connection);
                if (rows == 0) {
                    System.out.println("Nothing updated.");
                } else {
                    System.out.println("Field updated successfully.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error while updating department: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid data: " + e.getMessage());
        }
    }

    // ============== DELETE ==============
    private void deleteDepartment() {
        try {
            System.out.print("Enter department ID to delete: ");
            int id = Integer.parseInt(scanner.nextLine());

            int rows = departmentDAO.deleteById(id, connection);
            if (rows == 0) {
                System.out.println("No department deleted (not found or FK constraint).");
            } else {
                System.out.println("Department deleted.");
            }
        } catch (SQLException e) {
            System.out.println("Database error while deleting department: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    // ============== FIND BY ID ==============
    private void findDepartmentById() {
        try {
            System.out.print("Enter department ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Department d = departmentDAO.findById(id, connection);
            if (d == null) {
                System.out.println("Department not found.");
            } else {
                System.out.print(d);
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding department: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    // ============== FIND BY NAME ==============
    private void findDepartmentByName() {
        try {
            System.out.print("Enter department name: ");
            String name = scanner.nextLine();

            Department d = departmentDAO.findByName(name, connection);
            if (d == null) {
                System.out.println("Department not found.");
            } else {
                System.out.print(d);
            }
        } catch (SQLException e) {
            System.out.println("Database error while finding department: " + e.getMessage());
        }
    }

    // ============== COUNT ==============
    private void countDepartments() {
        try {
            int count = departmentDAO.countDepartments(connection);
            System.out.println("Total departments: " + count);
        } catch (SQLException e) {
            System.out.println("Database error while counting departments: " + e.getMessage());
        }
    }
}
