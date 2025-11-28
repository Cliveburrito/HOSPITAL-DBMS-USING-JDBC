package Entities;

public record DepartmentSummary(
        Department department,
        int occupancy,
        int doctorCount
) {
    @Override
    public String toString() {
        return """
                Department Summary:
                ---------------------
                Department: (ID: %d) %s
                Capacity: %d / Occupancy: %d
                Department Doctors: %d
                """.formatted(
                department.getDepartmentId(),
                department.getName(),
                department.getCapacity(),
                occupancy,
                doctorCount
        );
    }
}