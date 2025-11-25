import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    // ----------------------------------------------------
    // INSERT
    // ----------------------------------------------------
    public Appointment insert(Appointment a, Connection conn) throws SQLException {
        String sql = """
                INSERT INTO appointment (patient_id, doctor_id, appointment_datetime, reason)
                VALUES (?, ?, ?, ?)
                RETURNING appointment_id;
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientID());
            ps.setInt(2, a.getDoctorID());
            ps.setTimestamp(3, Timestamp.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getReason());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                a.setAppointmentID(rs.getInt(1));
                return a; // return updated object with ID
            } else {
                throw new SQLException("Failed to insert appointment — no generated ID returned");
            }
        }
    }

    // ----------------------------------------------------
    // FIND BY ID
    // ----------------------------------------------------
    public Appointment findById(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM appointment WHERE appointment_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractAppointment(rs);
            }
            return null;
        }
    }

    // ----------------------------------------------------
    // FIND ALL
    // ----------------------------------------------------
    public List<Appointment> findAll(Connection conn) throws SQLException {
        List<Appointment> list = new ArrayList<>();

        String sql = "SELECT * FROM appointment ORDER BY appointment_datetime";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractAppointment(rs));
            }

            return list;
        }
    }

    // ----------------------------------------------------
    // FIND APPOINTMENTS BY DOCTOR
    // ----------------------------------------------------
    public List<Appointment> findByDoctor(int doctorId, Connection conn) throws SQLException {
        List<Appointment> list = new ArrayList<>();

        String sql = """
                SELECT * FROM appointment
                WHERE doctor_id = ?
                ORDER BY appointment_datetime
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(extractAppointment(rs));
            }

            return list;
        }
    }

    // ----------------------------------------------------
    // FIND APPOINTMENTS BY PATIENT
    // ----------------------------------------------------
    public List<Appointment> findByPatient(int patientId, Connection conn) throws SQLException {
        List<Appointment> list = new ArrayList<>();

        String sql = """
                SELECT * FROM appointment
                WHERE patient_id = ?
                ORDER BY appointment_datetime
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(extractAppointment(rs));
            }

            return list;
        }
    }

    // ----------------------------------------------------
    // UPDATE
    // ----------------------------------------------------
    public int update(Appointment a, Connection conn) throws SQLException {
        String sql = """
                UPDATE appointment
                SET patient_id = ?, doctor_id = ?, appointment_datetime = ?, reason = ?
                WHERE appointment_id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, a.getPatientID());
            ps.setInt(2, a.getDoctorID());
            ps.setTimestamp(3, Timestamp.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getReason());
            ps.setInt(5, a.getAppointmentID());

            return ps.executeUpdate();
        }
    }

    // ----------------------------------------------------
    // DELETE
    // ----------------------------------------------------
    public int delete(int appointmentId, Connection conn) throws SQLException {
        String sql = "DELETE FROM appointment WHERE appointment_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate();
        }
    }

    // ----------------------------------------------------
    // Private helper method to map a ResultSet row to object
    // ----------------------------------------------------
    private Appointment extractAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setAppointmentID(rs.getInt("appointment_id"));
        a.setPatientID(rs.getInt("patient_id"));
        a.setDoctorID(rs.getInt("doctor_id"));

        Timestamp ts = rs.getTimestamp("appointment_datetime");
        a.setAppointmentDate(ts.toLocalDateTime());

        a.setReason(rs.getString("reason"));
        return a;
    }
}
