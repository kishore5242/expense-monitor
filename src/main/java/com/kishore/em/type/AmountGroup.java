package com.kishore.em.type;

public class AmountGroup {
    private String group;
    private Double amount;

    public AmountGroup(String group, Double amount) {
        this.group = group;
        this.amount = amount;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Double getAmount() {
        if (amount != null) {
            return Math.round(amount * 100.0) / 100.0;
        } else {
            return amount;
        }
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
