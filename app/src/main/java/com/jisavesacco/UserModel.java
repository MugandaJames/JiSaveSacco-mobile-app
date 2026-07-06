package com.jisavesacco;

public class UserModel {
    private String userId;
    private String fullName;
    private String email;
    private String nationalId; // 👈 ADDED
    private String phone;      // 👈 ADDED
    private String role;       // "member" or "admin"
    private String status;     // "Active" or "Pending"
    private long timestamp;    // 👈 ADDED for Date Joined calculation

    public UserModel() {}

    public UserModel(String userId, String fullName, String email, String nationalId, String phone, String role, String status, long timestamp) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.nationalId = nationalId;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNationalId() { return nationalId; } // 👈 ADDED
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getPhone() { return phone; }           // 👈 ADDED
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }     // 👈 ADDED
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}