package com.derek.framework.Flyweight;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TicketFactory {
//    public static Ticket getTicket(String from,String to){
//        return new TrainTicket(from,to);
//    }

    private static Map<String ,Ticket> sTicketMap = new ConcurrentHashMap<>();

    public static Ticket getTicket(String from,String to) {
        Ticket ticket;
        String key = from + "-" + to;
        if (sTicketMap.containsKey(key)){
            System.out.println("适用缓存");
            ticket = sTicketMap.get(key);
        }else{
            System.out.println("=====================");
            ticket = new TrainTicket(from,to);
            sTicketMap.put(key,ticket);
        }

        return ticket;
    }
}
