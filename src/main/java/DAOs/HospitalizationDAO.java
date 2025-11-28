package DAOs;

import Entities.CurrentHospitalizationSummary;
import Entities.Department;
import Entities.Patient;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HospitalizationDAO {
    public Hospitalization insert(Hospitalization h, Connection connection) throws SQLException {
        String sql = """
        INSERT INTO hospitalization
            (patient_id, department_id, admit_datetime, bed_number, reason)
        VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, h.getPatientId());
            ps.setInt(2, h.getDepartmentId());
            ps.setTimestamp(3, Timestamp.valueOf(h.getAdmitDateTime()));
            ps.setString(4, h.getBedNumber());
            ps.setString(5, h.getReason());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Insert failed: no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    h.setHospitalizationId(keys.getInt(1));
                }
            }
        }
        return h;
    }

    // ---------- FIND BY ID ----------
    public Hospitalization findById(int id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM hospitalization WHERE hospitalization_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractHospitalization(rs);
                }
                return null;
            }
        }
    }

    public CurrentHospitalizationSummary findCurrentlyHospitalizedByPatientAmka(String amka, Connection connection) throws SQLException {
        String sql = "SELECT * FROM current_hospitalization_view WHERE patient_amka = ?;";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, amka);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractCurrentHospitalizationSummary(rs);
                }
                return null;
            }
        }
    }

    public List<Hospitalization> findAll(Connection connection) throws SQLException {
        List<Hospitalization> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitalization ORDER BY admit_datetime DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractHospitalization(rs));
            }
        }
        return list;
    }

    public List<Hospitalization> findByPatientId(int patientId, Connection connection) throws SQLException {
        List<Hospitalization> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitalization WHERE patient_id = ? ORDER BY admit_datetime DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractHospitalization(rs));
                }
            }
        }
        return list;
    }

    public List<Hospitalization> findCurrentHospitalizations(Connection connection) throws SQLException {
        List<Hospitalization> list = new ArrayList<>();
        String sql = "SELECT * FROM hospitalization WHERE discharge_datetime IS NULL ORDER BY admit_datetime DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractHospitalization(rs));
            }
        }

        return list;
    }

    public List<CurrentHospitalizationSummary> findAllCurrentSummaries(Connection connection) throws SQLException {
        List<CurrentHospitalizationSummary> list = new ArrayList<>();
        String sql = "SELECT * FROM current_hospitalization_view ORDER BY admit_datetime DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractCurrentHospitalizationSummary(rs));
            }
        }

        return list;
    }

    public int updateHospitalizationField(int hospitalizationId, String field, Object value, Connection connection) throws SQLException {
        String sql = "UPDATE hospitalization SET " + field + " = ? WHERE hospitalization_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            ps.setInt(2, hospitalizationId);
            return ps.executeUpdate();
        }
    }

    // ---------- DELETE ----------
    public int deleteById(int id, Connection connection) throws SQLException {
        String sql = "DELETE FROM hospitalization WHERE hospitalization_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate(); // 0 = nothing deleted, 1 = deleted
        }
    }

    public int dischargePatient(int hospitalizationId, Connection connection) throws SQLException {
        String sql = "UPDATE hospitalization SET discharge_datetime = NOW() WHERE hospitalization_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, hospitalizationId);
            return ps.executeUpdate();
        }
    }


    // ---------- Helper mapper ----------
    private Hospitalization extractHospitalization(ResultSet rs) throws SQLException {
        Hospitalization h = new Hospitalization();
        h.setHospitalizationId(rs.getInt("hospitalization_id"));
        h.setPatientId(rs.getInt("patient_id"));
        h.setDepartmentId(rs.getInt("department_id"));

        Timestamp admitTs = rs.getTimestamp("admit_datetime");
        if (admitTs != null) {
            h.setAdmitDateTime(admitTs.toLocalDateTime());
        }

        Timestamp dischargeTs = rs.getTimestamp("discharge_datetime");
        if (dischargeTs != null) {
            h.setDischargeDateTime(dischargeTs.toLocalDateTime());
        }

        h.setBedNumber(rs.getString("bed_number"));
        h.setReason(rs.getString("reason"));

        return h;
    }

    private CurrentHospitalizationSummary extractCurrentHospitalizationSummary(ResultSet rs) throws SQLException {

        Patient patient = new Patient();
        patient.setPatientId(rs.getInt("patient_id"));
        patient.setFirstName(rs.getString("patient_first_name"));
        patient.setLastName(rs.getString("patient_last_name"));
        patient.setDateOfBirth(rs.getDate("patient_date_of_birth").toLocalDate());
        patient.setGender(rs.getString("patient_gender"));
        patient.setPhone(rs.getString("patient_phone"));
        patient.setAmka(rs.getString("patient_amka"));

        Department department = new Department();
        department.setDepartmentId(rs.getInt("department_id"));
        department.setName(rs.getString("department_name"));

        Hospitalization hospitalization = new Hospitalization();
        hospitalization.setHospitalizationId(rs.getInt("hospitalization_id"));
        hospitalization.setPatientId(patient.getPatientId());
        hospitalization.setDepartmentId(rs.getInt("department_id"));
        hospitalization.setAdmitDateTime(rs.getTimestamp("admit_datetime").toLocalDateTime());
        hospitalization.setBedNumber(rs.getString("bed_number"));
        hospitalization.setReason(rs.getString("reason"));
        hospitalization.setDischargeDateTime(null); // by definition in this view

        return new CurrentHospitalizationSummary(
                patient,
                department,
                hospitalization
        );
    }

}
