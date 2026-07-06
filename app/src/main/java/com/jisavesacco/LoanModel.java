package com.jisavesacco;

public class LoanModel {
    private String loanId;
    private String userId;
    private String loanAmount;
    private String loanType;
    private String repaymentPeriod;
    private String purpose;
    private String monthlyIncome;
    private String status; // "Pending", "Approved", "Rejected"
    private long timestamp;

    public LoanModel() {}

    public LoanModel(String loanId, String userId, String loanAmount, String loanType,
                     String repaymentPeriod, String purpose, String monthlyIncome,
                     String status, long timestamp) {
        this.loanId = loanId;
        this.userId = userId;
        this.loanAmount = loanAmount;
        this.loanType = loanType;
        this.repaymentPeriod = repaymentPeriod;
        this.purpose = purpose;
        this.monthlyIncome = monthlyIncome;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getLoanId() { return loanId; }
    public void setLoanId(String loanId) { this.loanId = loanId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getLoanAmount() { return loanAmount; }
    public void setLoanAmount(String loanAmount) { this.loanAmount = loanAmount; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public String getRepaymentPeriod() { return repaymentPeriod; }
    public void setRepaymentPeriod(String repaymentPeriod) { this.repaymentPeriod = repaymentPeriod; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(String monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}