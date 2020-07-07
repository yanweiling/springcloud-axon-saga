package com.ywl.study.ticket.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketEntityRepository extends JpaRepository<TicketEntity,String> {
}
