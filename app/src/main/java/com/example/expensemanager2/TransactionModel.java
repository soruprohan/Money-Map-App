package com.example.expensemanager2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TransactionModel {
    private String id,note,amount,type,date;
    public final long timestamp;


    public TransactionModel(String id, String note, String amount, String type,String date, long timestamp) {
        this.id = id;
        this.note = note;
        this.amount = amount;
        this.type = type;
        this.date=date;

        this.timestamp = timestamp;

    }

    private long getTimestamp(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM, yyyy hh:mm");
        try{
            LocalDateTime dateTime = LocalDateTime.from(formatter.parse(date));

            return dateTime.getSecond();
        }
        catch (Exception e){
            e.printStackTrace();
            return 0L;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
