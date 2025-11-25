import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDAO {

    /** INSERT doctor and return generated Doctor object */
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

            if (doctor.getDepartmentId() != null)
                ps.setInt(5, doctor.getDepartmentId());
            else
                ps.setNull(5, Types.INTEGER);

            ResultSet rs = ps.executeQuery();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    doctor.setDoctorId(keys.getInt(1));
                } else {
                    throw new SQLException("Inserting doctor failed, no ID obtained.");
                }
            }
        }
        return doctor;
    }

    /** FIND by ID */
    public Optional<Doctor> findById(int doctorId, Connection connection) throws SQLException {
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Doctor d = extractDoctor(rs);
                return Optional.of(d);
            }
            return Optional.empty();
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

    /** UPDATE doctor (entire object update) */
    public int updateDoctor(Doctor doctor, Connection connection) throws SQLException {
        String sql = """
                UPDATE doctor SET
                    first_name = ?,
                    last_name = ?,
                    specialty = ?,
                    phone = ?,
                    department_id = ?
                WHERE doctor_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, doctor.getFirstName());
            ps.setString(2, doctor.getLastName());
            ps.setString(3, doctor.getSpecialty());
            ps.setString(4, doctor.getPhone());

            if (doctor.getDepartmentId() != null)
                ps.setInt(5, doctor.getDepartmentId());
            else
                ps.setNull(5, Types.INTEGER);

            ps.setInt(6, doctor.getDoctorId());

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

    /** Extract Doctor from ResultSet (private helper) */
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
