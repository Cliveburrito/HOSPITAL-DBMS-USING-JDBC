package DAOs;

import Entities.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    // ---------- INSERT ----------
    public MedicalRecord insertMedicalRecord(MedicalRecord mr, Connection connection) throws SQLException {
        String sql = """
            INSERT INTO medical_record
                (patient_id, doctor_id, diagnosis, treatment, notes)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mr.getPatientId());

            if (mr.getDoctorId() != null) {
                ps.setInt(2, mr.getDoctorId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, mr.getDiagnosis());
            ps.setString(4, mr.getTreatment());
            ps.setString(5, mr.getNotes());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    mr.setRecordId(id);
                }
            }
        }
        return mr;
    }


    // ---------- FIND BY ID ----------
    public MedicalRecord findById(int id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM medical_record WHERE record_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMedicalRecord(rs);
                } else {
                    return null;
                }
            }
        }
    }

    // ---------- FIND BY PATIENT ----------
    public List<MedicalRecord> findByPatientId(int patientId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM medical_record WHERE patient_id = ? ORDER BY record_datetime DESC";
        List<MedicalRecord> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractMedicalRecord(rs));
                }
            }
        }
        return list;
    }

    public List<MedicalRecord> findAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM medical_record ORDER BY record_datetime DESC";
        List<MedicalRecord> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractMedicalRecord(rs));
            }
        }
        return list;
    }

    public int updateMedicalRecordField(int recordId, String field, Object value, Connection connection) throws SQLException {
        String sql = "UPDATE medical_record SET " + field + " = ? WHERE record_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            ps.setInt(2, recordId);
            return ps.executeUpdate();
        }
    }

    // ---------- DELETE ----------
    public int deleteById(int id, Connection connection) throws SQLException {
        String sql = "DELETE FROM medical_record WHERE record_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    // ---------- Helper mapper ----------
    private MedicalRecord extractMedicalRecord(ResultSet rs) throws SQLException {
        MedicalRecord mr = new MedicalRecord();
        mr.setRecordId(rs.getInt("record_id"));
        mr.setPatientId(rs.getInt("patient_id"));

        int docId = rs.getInt("doctor_id");
        if (rs.wasNull()) {
            mr.setDoctorId(null);
        } else {
            mr.setDoctorId(docId);
        }

        Timestamp ts = rs.getTimestamp("record_datetime");
        if (ts != null) {
            mr.setRecordDateTime(ts.toLocalDateTime());
        } else {
            mr.setRecordDateTime(null);
        }

        mr.setDiagnosis(rs.getString("diagnosis"));
        mr.setTreatment(rs.getString("treatment"));
        mr.setNotes(rs.getString("notes"));
        return mr;
    }
}
