import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordDAO {

    public MedicalRecord insertMedicalRecord(MedicalRecord mr, Connection connection) throws SQLException {
        String sql = "INSERT INTO medical_record " +
                "(patient_id, doctor_id, record_datetime, diagnosis, treatment, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING record_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mr.getPatientId());

            if (mr.getDoctorId() != null) {
                ps.setInt(2, mr.getDoctorId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            if (mr.getRecordDateTime() != null) {
                ps.setTimestamp(3, Timestamp.valueOf(mr.getRecordDateTime()));
            } else {
                // let DB default NOW() if null
                ps.setNull(3, Types.TIMESTAMP);
            }

            ps.setString(4, mr.getDiagnosis());
            ps.setString(5, mr.getTreatment());
            ps.setString(6, mr.getNotes());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    mr.setRecordId(id);
                    return mr;
                } else {
                    throw new SQLException("Failed to insert medical record — no ID returned");
                }
            }
        }
    }

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
        String sql = "SELECT * FROM medical_record";
        List<MedicalRecord> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractMedicalRecord(rs));
            }
        }
        return list;
    }

    public int updateMedicalRecord(MedicalRecord mr, Connection connection) throws SQLException {
        String sql = "UPDATE medical_record " +
                "SET patient_id = ?, doctor_id = ?, record_datetime = ?, " +
                "diagnosis = ?, treatment = ?, notes = ? " +
                "WHERE record_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mr.getPatientId());

            if (mr.getDoctorId() != null) {
                ps.setInt(2, mr.getDoctorId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setTimestamp(3, Timestamp.valueOf(mr.getRecordDateTime()));
            ps.setString(4, mr.getDiagnosis());
            ps.setString(5, mr.getTreatment());
            ps.setString(6, mr.getNotes());
            ps.setInt(7, mr.getRecordId());

            return ps.executeUpdate();
        }
    }

    public int deleteById(int id, Connection connection) throws SQLException {
        String sql = "DELETE FROM medical_record WHERE record_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    // ===== Helper mapper =====
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
