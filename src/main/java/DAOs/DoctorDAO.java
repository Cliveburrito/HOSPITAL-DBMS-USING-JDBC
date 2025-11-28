package DAOs;


import Entities.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /** INSERT doctor and return generated Entities.Doctor object */
    public Doctor insertDoctor(Doctor doctor, Connection connection) throws SQLException {
        String sql = """
                INSERT INTO doctor(first_name, last_name, specialty, phone, department_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, doctor.getFirstName());
            ps.setString(2, doctor.getLastName());
            ps.setString(3, doctor.getSpecialty());
            ps.setString(4, doctor.getPhone());
            ps.setInt(5, doctor.getDepartmentId());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    doctor.setDoctorId(keys.getInt(1));
                }
            }
        }
        return doctor;
    }

    /** FIND by ID */
    public Doctor findById(int doctorId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Doctor d = extractDoctor(rs);
                return d;
            }
            return null;
        }
    }
    public int numberOfDoctors(Connection connection) throws SQLException {
        String sql = "Select Count(*) from doctor;";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /** LIST all doctors */
    public List<Doctor> findAll(Connection connection) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctor ORDER BY doctor_id";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractDoctor(rs));
            }
        }
        return list;
    }

    /**
     * Updates a single column of doctor by ID.
     */
    public int updateDoctorField(int doctorId, String field, Object value, Connection connection) throws SQLException {
        String sql = "UPDATE doctor SET " + field + " = ? WHERE doctor_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            ps.setInt(2, doctorId);
            return ps.executeUpdate();
        }
    }



    /** DELETE doctor by ID, return rows affected */
    public int deleteById(int doctorId, Connection connection) throws SQLException {
        String sql = "DELETE FROM doctor WHERE doctor_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            return ps.executeUpdate();
        }
    }
    public List<Doctor> findDoctorsByDepartmentName(String departmentName, Connection connection) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "Select * from doctor where department_id = (Select department_id from " +
                "department where LOWER(name) = LOWER(?));";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, departmentName);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Doctor d = extractDoctor(rs);
                list.add(d);
            }
        }
        return list;
    }

    public List<Doctor> findDoctorsByDepartmentId(int departmentID, Connection connection) throws SQLException {
        List<Doctor> list = new ArrayList<>();
        String sql = "Select * from doctor where deparment_id = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departmentID);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Doctor d = extractDoctor(rs);
                list.add(d);
            }
        }
        return list;
    }

    /** Extract Entities.Doctor from ResultSet (private helper) */
    private Doctor extractDoctor(ResultSet rs) throws SQLException {
        Doctor d = new Doctor();
        d.setDoctorId(rs.getInt("doctor_id"));
        d.setFirstName(rs.getString("first_name"));
        d.setLastName(rs.getString("last_name"));
        d.setSpecialty(rs.getString("specialty"));
        d.setPhone(rs.getString("phone"));

        int dept = rs.getInt("department_id");
        d.setDepartmentId(rs.wasNull() ? null : dept);

        return d;
    }
}
