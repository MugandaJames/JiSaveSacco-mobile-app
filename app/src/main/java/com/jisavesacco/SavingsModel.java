package com.jisavesacco;

public class SavingsModel {
    private String depositId;
    private String userId;
    private double amount;
    private String date;
    private long timestamp;

    public SavingsModel() {}

    public SavingsModel(String depositId, String userId, double amount, String date, long timestamp) {
        this.depositId = depositId;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.timestamp = timestamp;
    }

    public String getDepositId() { return depositId; }
    public void setDepositId(String depositId) { this.depositId = depositId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}