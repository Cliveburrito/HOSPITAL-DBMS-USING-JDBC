import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    public Appointment insert(Appointment a, Connection conn) throws SQLException {
        String sql = """
            INSERT INTO appointment (patient_id, doctor_id, appointment_datetime, reason)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, a.getPatientID());
            ps.setInt(2, a.getDoctorID());
            ps.setTimestamp(3, Timestamp.valueOf(a.getAppointmentDate()));
            ps.setString(4, a.getReason());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    a.setAppointmentID(keys.getInt(1));
                }
            }
        }
        return a;
    }


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

    public int countAppointmentsByDoctor(int doctorId, Connection conn) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

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
    public List<Appointment> listAppointmentsInDateInterval(Date from, Date to, Connection connection) throws SQLException{
        String sql = "Select * from appointment where appointment_datetime Between ? and ?;";
        List<Appointment> list = new ArrayList<>();

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, from);
            ps.setDate(2, to);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Appointment ap = extractAppointment(rs);
                list.add(ap);
            }

        }
        return list;
    }

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

    public int delete(int appointmentId, Connection conn) throws SQLException {
        String sql = "DELETE FROM appointment WHERE appointment_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate();
        }
    }

    public List<AppointmentSummary> listByDoctor(int doctorId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM appointment_full_view WHERE doctor_id = ? ORDER BY appointment_datetime DESC";
        List<AppointmentSummary> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAppointmentSummary(rs));
                }
            }
        }

        return list;
    }

    public List<AppointmentSummary> listByPatient(int patientID, Connection connection) throws SQLException {
        String sql = "SELECT * FROM appointment_full_view WHERE patient_id = ? ORDER BY appointment_datetime DESC";
        List<AppointmentSummary> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractAppointmentSummary(rs));
                }
            }
        }
        return list;
    }

    public AppointmentSummary appointmentSummary(int appointmentID, Connection connection) throws SQLException {
        String sql = "Select * from appointment_full_view where appointment_id = ?;";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, appointmentID);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return extractAppointmentSummary(rs);
            }
        }
        return null;
    }

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

    private AppointmentSummary extractAppointmentSummary(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        Doctor d = new Doctor();
        Department dpt = new Department();
        Appointment a = new Appointment();

        a.setAppointmentID(rs.getInt("appointment_id"));
        Timestamp ts = rs.getTimestamp("appointment_datetime");
        a.setAppointmentDate(ts.toLocalDateTime());
        a.setReason(rs.getString("appointment_reason"));
        dpt.setDepartmentId(rs.getInt("department_id"));
        dpt.setName(rs.getString("department_name"));
        d.setDoctorId(rs.getInt("doctor_id"));
        d.setFirstName(rs.getString("doctor_first_name"));
        d.setLastName(rs.getString("doctor_last_name"));
        p.setPatientId(rs.getInt("patient_id"));
        p.setFirstName(rs.getString("patient_first_name"));
        p.setLastName(rs.getString("patient_last_name"));
        p.setDateOfBirth(rs.getDate("patient_birth_date").toLocalDate());
        p.setGender(rs.getString("patient_gender"));
        p.setPhone(rs.getString("patient_phone"));
        p.setAmka(rs.getString("patient_amka"));

        AppointmentSummary aps = new AppointmentSummary(a, d , p, dpt);

        return aps;
    }
}
