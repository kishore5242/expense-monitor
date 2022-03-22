package com.kishore.em.type;

import java.time.LocalDate;

public class Record {

    private String bank;

    private LocalDate valueDate;

    private Double balance;

    private Double credit;

    private Double debit;

    private String remark;

    public Record(String bank, LocalDate valueDate, Double balance) {
        this.bank = bank;
        this.valueDate = valueDate;
        this.balance = balance;
    }

    public Record(String bank, LocalDate valueDate, Double balance, Double credit, Double debit) {
        this.bank = bank;
        this.valueDate = valueDate;
        this.balance = balance;
        this.credit = credit;
        this.debit = debit;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getDebit() {
        return debit;
    }

    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
