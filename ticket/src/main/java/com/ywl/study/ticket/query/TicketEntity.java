package com.ywl.study.ticket.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name="tb_ticket")
@ToString
public class TicketEntity {
    private static final Logger LOG= LoggerFactory.getLogger(TicketEntity.class);
    @Id
    private String ticketId;
    private String name;

    /*锁票人员*/
    private String lockUser;
    /*锁票后，支付完成，则该票的拥有者*/
    private String owner;

}
