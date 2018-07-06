package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by NeonTetras on 02-Mar-18.
 */

public class Remittance extends Entity implements Serializable{


    public long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Date getRemittanceDate() {
        return remittanceDate;
    }

    public void setRemittanceDate(Date remittanceDate) {
        this.remittanceDate = remittanceDate;
    }

    public int getAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(int acknowledged) {
        this.acknowledged = acknowledged;
    }

    public Date getDateAcknowledge() {
        return dateAcknowledge;
    }

    public void setDateAcknowledge(Date dateAcknowledge) {
        this.dateAcknowledge = dateAcknowledge;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public boolean isUploadOk() {
        return uploadOk;
    }

    public void setUploadOk(boolean uploadOk) {
        this.uploadOk = uploadOk;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public User getWorker() {
        return worker;
    }

    public void setWorker(User worker) {
        this.worker = worker;
    }

    private long supervisorId;
    private long workerId;
    public User supervisor;
    public User worker;
    public long amount;
    public Date remittanceDate;
    public int acknowledged;
    public Date dateAcknowledge;
    public String proof;
    public boolean uploadOk;
    protected static final long serialVersionUID = 1l;
}
