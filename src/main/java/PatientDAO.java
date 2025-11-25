import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PatientDAO {
    //List all patients
    //insertPatient(Patient p)
    //updatePatient(Patient p)
    //deletePatient(int id)
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

    /**
     * Inserts a new patient and returns the same Patient object
     * with the generated ID set.
     */
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

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Inserting patient failed, no rows affected.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setPatientId(keys.getInt(1));
                } else {
                    throw new SQLException("Inserting patient failed, no ID obtained.");
                }
            }
        }

        return p;
    }

    public int updatePatient(int patientID, Map<String, Object> fields, Connection connection) throws SQLException {
        if (fields == null || fields.isEmpty()) {
            return 0; // nothing to update
        }

        StringBuilder sql = new StringBuilder("UPDATE patient SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            sql.append(entry.getKey()).append(" = ?, ");
            params.add(entry.getValue());
        }

        sql.setLength(sql.length() - 2); // remove last comma
        sql.append(" WHERE patient_id = ?");

        params.add(patientID);

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        // bind parameters
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }

        return ps.executeUpdate();
    }

    public int deletePatientById(int patientId, Connection connection) {
        String sql = "DELETE FROM patient WHERE patient_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            return ps.executeUpdate(); // 0 = nothing deleted, 1 = deleted
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting patient with id " + patientId, e);
        }
    }

    public Patient findByID(int patientID, Connection connection) throws SQLException {
        String sql = "Select * from patient where patient_id = ?";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, patientID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractPatient(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding patient with id " + patientID, e);
        }
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
