import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public Department insertDepartment(Department d, Connection connection) throws SQLException {
        String sql = "INSERT INTO department (name, capacity) VALUES (?, ?) RETURNING department_id";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            if (d.getCapacity() != null) {
                ps.setInt(2, d.getCapacity());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    d.setDepartmentId(id);
                    return d;
                } else {
                    throw new SQLException("Failed to insert department — no ID returned");
                }
            }
        }
    }

    public Department findById(int id, Connection connection) throws SQLException {
        String sql = "SELECT * FROM department WHERE department_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractDepartment(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public Department findByName(String name, Connection connection) throws SQLException {
        String sql = "SELECT * FROM department WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractDepartment(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public List<Department> findAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM department ORDER BY department_id";
        List<Department> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractDepartment(rs));
            }
        }
        return list;
    }

    public int updateDepartment(Department d, Connection connection) throws SQLException {
        String sql = "UPDATE department SET name = ?, capacity = ? WHERE department_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            if (d.getCapacity() != null) {
                ps.setInt(2, d.getCapacity());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, d.getDepartmentId());

            return ps.executeUpdate();
        }
    }

    public int deleteById(int id, Connection connection) throws SQLException {
        String sql = "DELETE FROM department WHERE department_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    // Example aggregate / reporting: departments with their capacity (simple but usable in menu)
    public int countDepartments(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM department";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    // ===== Helper mapper =====
    private Department extractDepartment(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setDepartmentId(rs.getInt("department_id"));
        d.setName(rs.getString("name"));
        int capacity = rs.getInt("capacity");
        if (rs.wasNull()) {
            d.setCapacity(null);
        } else {
            d.setCapacity(capacity);
        }
        return d;
    }
}
