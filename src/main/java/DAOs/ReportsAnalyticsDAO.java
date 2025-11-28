package DAOs;

import Entities.Doctor;

import java.sql.*;
import java.util.HashMap;

public class ReportsAnalyticsDAO {

    /**
     * Average length of stay  per department in days.
     * EPOCH converts the date difference to seconds and the division with 86400 makes the seconds into days
     * Returns HashMap<String, Integer>.
     */
    public HashMap<String, Integer> averageLengthOfStayPerDepartment(Connection connection) throws SQLException {
        String sql = """
                SELECT d.name AS department_name, AVG(EXTRACT(EPOCH FROM (h.discharge_datetime - h.admit_datetime)) / 86400.0)
                           AS avg_stay_days
                FROM hospitalization h
                JOIN department d ON d.department_id = h.department_id
                WHERE h.discharge_datetime IS NOT NULL
                GROUP BY d.name
                """;

        HashMap<String, Integer> result = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String deptName = rs.getString("department_name");
                double avgDays = rs.getDouble("avg_stay_days");
                int roundedDays = (int) Math.round(avgDays);
                result.put(deptName, roundedDays);
            }
        }

        return result;
    }

    /**
     * Doctors and number of distinct patients they have examined.
     * Returns HashMap<Entities.Doctor, Integer>.
     */
    public HashMap<Doctor, Integer> doctorPatientCounts(Connection connection) throws SQLException {
        String sql = """
                SELECT d.doctor_id, d.first_name, d.last_name, d.specialty, d.phone, d.department_id,
                       COUNT(DISTINCT mr.patient_id) AS patient_count
                FROM doctor d
                JOIN medical_record mr ON mr.doctor_id = d.doctor_id
                GROUP BY d.doctor_id, d.first_name, d.last_name,
                         d.specialty, d.phone, d.department_id
                ORDER BY patient_count DESC
                """;

        HashMap<Doctor, Integer> result = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Doctor doctor = new Doctor();
                doctor.setDoctorId(rs.getInt("doctor_id"));
                doctor.setFirstName(rs.getString("first_name"));
                doctor.setLastName(rs.getString("last_name"));
                doctor.setSpecialty(rs.getString("specialty"));
                doctor.setPhone(rs.getString("phone"));

                int deptId = rs.getInt("department_id");
                if (rs.wasNull()) {
                    doctor.setDepartmentId(null);
                } else {
                    doctor.setDepartmentId(deptId);
                }

                int patientCount = rs.getInt("patient_count");
                result.put(doctor, patientCount);
            }
        }
        return result;
    }

    /**
     * Number of covid cases
     * LIKE returns any diagnosis that contains the word covid
     */
    public int countCovidCases(Connection connection) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM medical_record
                WHERE LOWER(diagnosis) LIKE '%covid% OR %COVID19%'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Bed occupancy rate per department, based on current hospitalizations
     * Returns HashMap<String, Double> with rate between 0.0 and 1.0.
     */
    public HashMap<String, Double> bedOccupancyRatePerDepartment(Connection connection) throws SQLException {
        String sql = """
                SELECT d.name AS department_name, d.capacity,
                       COUNT(h.hospitalization_id) AS current_patients
                FROM department d
                LEFT JOIN hospitalization h
                  ON h.department_id = d.department_id
                 AND h.discharge_datetime IS NULL
                GROUP BY d.name, d.capacity
                """;

        HashMap<String, Double> result = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String deptName = rs.getString("department_name");
                int capacity = rs.getInt("capacity");
                boolean capacityWasNull = rs.wasNull();
                int currentPatients = rs.getInt("current_patients");

                double rate;
                if (capacityWasNull || capacity <= 0) {
                    rate = 0.0;
                } else {
                    rate = (double) currentPatients / capacity;
                }

                result.put(deptName, rate);
            }
        }
        return result;
    }

    /**
     * Average age of patients who have been admitted
     */
    public double averageAgeOfAdmittedPatients(Connection connection) throws SQLException {
        String sql = """
                SELECT AVG(EXTRACT(YEAR FROM age(NOW(), p.date_of_birth))) AS avg_age
                FROM patient p
                WHERE EXISTS (
                    SELECT 1
                    FROM hospitalization h
                    WHERE h.patient_id = p.patient_id
                )
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("avg_age");
            }
        }
        return 0;
    }

    /**
     * Admissions per year for each department.
     * For a given year, returns HashMap<DepartmentName, Integer>.
     */
    public HashMap<String, Integer> admissionsPerDepartmentPerYear(int year, Connection connection) throws SQLException {
        String sql = """
                SELECT d.name AS department_name,
                       COUNT(*) AS admissions
                FROM hospitalization h
                JOIN department d ON d.department_id = h.department_id
                WHERE EXTRACT(YEAR FROM h.admit_datetime) = ?
                GROUP BY d.name
                ORDER BY d.name
                """;

        HashMap<String, Integer> result = new HashMap<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, year);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String deptName = rs.getString("department_name");
                    int admissions = rs.getInt("admissions");
                    result.put(deptName, admissions);
                }
            }
        }
        return result;
    }
}
