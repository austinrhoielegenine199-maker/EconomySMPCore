package com.nyretha.pay.model;

public class PayTransaction {
    private final String id;
    private final String sender;
    private final String receiver;
    private final double amount;
    private final String date;

    public PayTransaction(String id, String sender, String receiver, double amount, String date) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.date = date;
    }

    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}
