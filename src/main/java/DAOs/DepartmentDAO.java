import Entities.Department;
import Entities.DepartmentSummary;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public Department insertDepartment(Department d, Connection connection) throws SQLException {
        String sql = "INSERT INTO department (name, capacity) VALUES (?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getName());
            if (d.getCapacity() != null) {
                ps.setInt(2, d.getCapacity());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    d.setDepartmentId(rs.getInt(1));
                }
            }
        }
        return d;
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
        String sql = "SELECT * FROM department WHERE LOWER(name) = LOWER(?)";
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

    public int updateDepartmentField(int departmentId, String field, Object value, Connection connection) throws SQLException {
        String sql = "UPDATE department SET " + field + " = ? WHERE department_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            ps.setInt(2, departmentId);
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

    public double averageNumberOfDoctorPerDepartment(Connection connection) throws SQLException {
        String sql = "Select Avg(doctor_count) from (Select department_id, Count(*) as doctor_count" +
                "from doctor " +
                "group by department_id)";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int numberOfDoctorsInDepartment(String departmentName, Connection connection) throws SQLException {
        String sql = "Select count(*) from doctor where department_id = (SELECT department_id FROM " +
                "department WHERE LOWER(name) = LOWER(?))";

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, departmentName);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public DepartmentSummary departmentSummary(String departmentName , Connection connection) throws SQLException {
        String sql = "Select * from department_full_view where department_id = (SELECT department_id FROM " +
                "department WHERE LOWER(name) = LOWER(?))";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, departmentName);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                return extractDepartmentSummary(rs);
            }
        }
        return null;
    }

    public int occupancy(String deparmentName, Connection connection) throws SQLException {
        String sql = "Select current_occupancy from department_full_view where LOWER(department_name) = LOWER(?);";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, deparmentName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
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

    private DepartmentSummary extractDepartmentSummary(ResultSet rs) throws SQLException {
        Department d = new Department();
        d.setDepartmentId(rs.getInt("department_id"));
        d.setName(rs.getString("department_name"));
        d.setCapacity(rs.getInt("capacity"));
        int occupancy = rs.getInt("current_occupancy");
        int doctorCount = rs.getInt("doctor_count");

        return (new DepartmentSummary(d, occupancy, doctorCount));
    }
}
