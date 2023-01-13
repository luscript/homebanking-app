package com.mindhub.homebanking.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long passwordid;
    private String passwordResetToken;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @OneToOne(targetEntity = Client.class, fetch = FetchType.EAGER)
    private Client client;

    public PasswordResetToken() {}

    public PasswordResetToken(Client client) {
        this.client = client;
        this.createdDate = new Date();
        this.passwordResetToken = UUID.randomUUID().toString();
    }

    public long getPasswordid() {
        return passwordid;
    }

    public void setPasswordid(long passwordid) {
        this.passwordid = passwordid;
    }

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
