import Entities.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    public List<Patient> findALL(Connection connection) throws SQLException {
        List<Patient> list = new ArrayList<>();

        String sql = "SELECT * FROM patient";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()){
            while(resultSet.next()) {
                Patient p = extractPatient(resultSet);
                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching patients" + e.getMessage());
            throw e;
        }
        return list;
    }

    public int numberOfPatients(Connection connection) throws SQLException {
        String sql = "Select Count(*) from patient;";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public Patient insertPatient(Patient p, Connection connection) throws SQLException {
        String sql = """
                INSERT INTO patient (first_name, last_name, date_of_birth, gender, phone, amka)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getFirstName());
            ps.setString(2, p.getLastName());
            ps.setDate(3, Date.valueOf(p.getDateOfBirth()));
            ps.setString(4, p.getGender());
            ps.setString(5, p.getPhone());
            ps.setString(6, p.getAmka());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setPatientId(keys.getInt(1));
                }
            }
        }

        return p;
    }

    public int updatePatientField(int patientId, String field, Object value, Connection connection) throws SQLException {
        String sql = "UPDATE patient SET " + field + " = ? WHERE patient_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            ps.setInt(2, patientId);
            return ps.executeUpdate();
        }
    }


    public int deletePatientById(int patientId, Connection connection) throws SQLException {
        String sql = "DELETE FROM patient WHERE patient_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            return ps.executeUpdate();
        }
    }

    public Patient findById(int patientId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM patient WHERE patient_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPatient(rs);
                }
            }
        }

        return null;
    }

    public Patient findByAMKA(String amka, Connection connection) throws SQLException {
        String sql = "SELECT * FROM patient WHERE amka = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, amka);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPatient(rs);
                }
            }
        }
        return null;
    }

    private Patient extractPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        p.setGender(rs.getString("gender"));
        p.setPhone(rs.getString("phone"));
        p.setAmka(rs.getString("amka"));
        return p;
    }
}
