public class Department {

    private int departmentId;
    private String name;
    private Integer capacity; // can be null in DB

    public Department() {
    }

    public Department(int departmentId, String name, Integer capacity) {
        this.departmentId = departmentId;
        this.name = name;
        this.capacity = capacity;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        String capStr = (capacity == null) ? "N/A" : capacity.toString();
        return String.format("%-4d %-20s %-4s", departmentId, name, capStr);
    }
}
