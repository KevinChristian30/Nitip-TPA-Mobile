package edu.bluejack22_2.nitip.Model;

public class Bill {

    private String id;
    private String debtorEmail;
    private String lenderEmail;
    private int amount;
    private String status;
    private String date;
    private String proof;

    public Bill(String debtorEmail, String lenderEmail, int amount, String status, String date, String proof) {
        this.debtorEmail = debtorEmail;
        this.lenderEmail = lenderEmail;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.proof = proof;
    }

    public Bill() {}

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDebtor_email() {
        return debtorEmail;
    }

    public void setDebtor_email(String debtorEmail) {
        this.debtorEmail = debtorEmail;
    }

    public String getLender_email() {
        return lenderEmail;
    }

    public void setLender_email(String lenderEmail) {
        this.lenderEmail = lenderEmail;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }
}
