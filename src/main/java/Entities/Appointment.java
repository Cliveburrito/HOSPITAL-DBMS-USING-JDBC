import java.time.LocalDate;
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentID;
    private int patientID;
    private int doctorID;
    private LocalDateTime appointmentDateTime;
    private String reason;

    public Appointment(int patientID, int appointmentID, int doctorID, LocalDateTime appointmentDateTime, String reason) {
        this.patientID = patientID;
        this.appointmentID = appointmentID;
        this.doctorID = doctorID;
        this.appointmentDateTime = appointmentDateTime;
        this.reason = reason;
    }

    public Appointment() {
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        if(patientID <= 0) {
            throw new IllegalArgumentException("ID must be >0");
        }
        this.patientID = patientID;
    }

    public int getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(int doctorID) {
        if(doctorID <= 0) {
            throw new IllegalArgumentException("ID must be >0");
        }
        this.doctorID = patientID;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDateTime;
    }

    public void setAppointmentDate(LocalDateTime appointmentDateTime) {
        if (appointmentDateTime == null) {
            throw new IllegalArgumentException("Choose appointment date!");
        }
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointment date cannot be in the past.");
        }
        this.appointmentDateTime = appointmentDateTime;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        String s = String.format("%-4d %-4d %-4d %-12s %-25s%n",
                appointmentID, patientID, doctorID, appointmentDateTime, reason);
        return s;
    }

}
