import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HospitalizationDAO {

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

    // ---------- INSERT ----------
    // Returns the same object with the generated ID set
    public Hospitalization insert(Hospitalization h, Connection connection) throws SQLException {
        String sql = """
                INSERT INTO hospitalization
                    (patient_id, department_id, admit_datetime, discharge_datetime, bed_number, reason)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING hospitalization_id
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, h.getPatientId());
            ps.setInt(2, h.getDepartmentId());

            // admit_datetime is NOT NULL in schema
            LocalDateTime admit = h.getAdmitDateTime();
            if (admit == null) {
                throw new SQLException("admitDateTime cannot be null");
            }
            ps.setTimestamp(3, Timestamp.valueOf(admit));

            // discharge_datetime can be null
            if (h.getDischargeDateTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(h.getDischargeDateTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.setString(5, h.getBedNumber());
            ps.setString(6, h.getReason());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    h.setHospitalizationId(id);
                    return h;
                } else {
                    throw new SQLException("Failed to insert hospitalization — no ID returned");
                }
            }
        }
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
                return null; // caller decides what to do if null
            }
        }
    }

    // ---------- FIND ALL ----------
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

    // ---------- FIND BY PATIENT ----------
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

    // ---------- CURRENTLY ADMITTED (discharge_datetime IS NULL) ----------
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

    // ---------- UPDATE ----------
    // Full update by ID (you could also do a partial/dynamic one similar to your PatientDAO if you like)
    public int update(Hospitalization h, Connection connection) throws SQLException {
        String sql = """
                UPDATE hospitalization
                SET patient_id = ?,
                    department_id = ?,
                    admit_datetime = ?,
                    discharge_datetime = ?,
                    bed_number = ?,
                    reason = ?
                WHERE hospitalization_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, h.getPatientId());
            ps.setInt(2, h.getDepartmentId());

            if (h.getAdmitDateTime() == null) {
                throw new SQLException("admitDateTime cannot be null in update");
            }
            ps.setTimestamp(3, Timestamp.valueOf(h.getAdmitDateTime()));

            if (h.getDischargeDateTime() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(h.getDischargeDateTime()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            ps.setString(5, h.getBedNumber());
            ps.setString(6, h.getReason());
            ps.setInt(7, h.getHospitalizationId());

            return ps.executeUpdate(); // usually 0 or 1
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
}
