package com.ywl.study.user.query;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="tb_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerEntity {
    @Id
    private String id;

    @Column(name = "user_name")
    private String username;
    private String password;

    private Double deposit;
}
