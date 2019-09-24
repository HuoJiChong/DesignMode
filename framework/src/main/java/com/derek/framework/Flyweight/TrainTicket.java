package com.derek.framework.Flyweight;

import java.util.Random;

public class TrainTicket implements Ticket {
    private String from;
    private String to;
    
    public int price;

    public TrainTicket(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void showTicketInfo(String bunk) {
        price = new Random().nextInt(300);
        System.out.println(" 购买 从" + from + "到" +to + " 的" + bunk + "火车票，价格：" + price);
    }
}
