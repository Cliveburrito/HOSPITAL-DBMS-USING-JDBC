package Entities;

import java.time.LocalDateTime;

public class Hospitalization {

    private int hospitalizationId;
    private int patientId;
    private int departmentId;
    private LocalDateTime admitDateTime;
    private LocalDateTime dischargeDateTime; // can be null
    private String bedNumber;
    private String reason;

    public Hospitalization() {
    }

    // Full constructor (including ID)
    public Hospitalization(int hospitalizationId,
                           int patientId,
                           int departmentId,
                           LocalDateTime admitDateTime,
                           LocalDateTime dischargeDateTime,
                           String bedNumber,
                           String reason) {
        this.hospitalizationId = hospitalizationId;
        this.patientId = patientId;
        this.departmentId = departmentId;
        this.admitDateTime = admitDateTime;
        this.dischargeDateTime = dischargeDateTime;
        this.bedNumber = bedNumber;
        this.reason = reason;
    }

    // Constructor without ID (for inserts where DB generates the ID)
    public Hospitalization(int patientId,
                           int departmentId,
                           LocalDateTime admitDateTime,
                           LocalDateTime dischargeDateTime,
                           String bedNumber,
                           String reason) {
        this.patientId = patientId;
        this.departmentId = departmentId;
        this.admitDateTime = admitDateTime;
        this.dischargeDateTime = dischargeDateTime;
        this.bedNumber = bedNumber;
        this.reason = reason;
    }

    public int getHospitalizationId() {
        return hospitalizationId;
    }

    public void setHospitalizationId(int hospitalizationId) {
        this.hospitalizationId = hospitalizationId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public LocalDateTime getAdmitDateTime() {
        return admitDateTime;
    }

    public void setAdmitDateTime(LocalDateTime admitDateTime) {
        this.admitDateTime = admitDateTime;
    }

    public LocalDateTime getDischargeDateTime() {
        return dischargeDateTime;
    }

    public void setDischargeDateTime(LocalDateTime dischargeDateTime) {
        this.dischargeDateTime = dischargeDateTime;
    }

    public String getBedNumber() {
        return bedNumber;
    }

    public void setBedNumber(String bedNumber) {
        this.bedNumber = bedNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        // adjust formatting as you like
        return String.format(
                "%-4d %-4d %-4d %-20s %-20s %-8s %-30s",
                hospitalizationId,
                patientId,
                departmentId,
                admitDateTime,
                (dischargeDateTime != null ? dischargeDateTime : "N/A"),
                (bedNumber != null ? bedNumber : "-"),
                (reason != null ? reason : "-")
        );
    }
}
