package com.kishore.em.type;

import java.time.LocalDate;

public class Balance {
    private LocalDate date;
    private Double balance;

    public Balance(LocalDate date, Double balance) {
        this.date = date;
        this.balance = balance;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
