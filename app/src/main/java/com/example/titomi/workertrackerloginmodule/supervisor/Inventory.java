package com.example.titomi.workertrackerloginmodule.supervisor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class Inventory extends Entity implements Serializable {
    protected static final long serialVersionUID = 1l;
    public static class InventoryRequests extends Entity implements Serializable{


        public int getSupervisorId() {
            return supervisorId;
        }

        public void setSupervisorId(int supervisorId) {
            this.supervisorId = supervisorId;
        }

        public int getDistributorId() {
            return distributorId;
        }

        public void setDistributorId(int distributorId) {
            this.distributorId = distributorId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public Date getDateAcknowledge() {
            return dateAcknowledge;
        }

        public void setDateAcknowledge(Date dateAcknowledge) {
            this.dateAcknowledge = dateAcknowledge;
        }

        public Date getResponseDate() {
            return responseDate;
        }

        public void setResponseDate(Date responseDate) {
            this.responseDate = responseDate;
        }

        public String getSupervisorMessage() {
            return supervisorMessage;
        }

        public void setSupervisorMessage(String supervisorMessage) {
            this.supervisorMessage = supervisorMessage;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public boolean isAcknowledged() {
            return acknowledged;
        }

        public void setAcknowledged(boolean acknowledged) {
            this.acknowledged = acknowledged;
        }

        public User getDistributor() {
            return distributor;
        }

        public void setDistributor(User distributor) {
            this.distributor = distributor;
        }

        public Date getDateRequested() {
            return dateRequested;
        }

        public void setDateRequested(Date dateRequested) {
            this.dateRequested = dateRequested;
        }

        public User getSupervisor() {
            return supervisor;
        }

        public void setSupervisor(User supervisor) {
            this.supervisor = supervisor;
        }

        private User supervisor;
        private User distributor;
        private int supervisorId;
        private int distributorId;
        private String message;
        private String comment;
        private Date dateAcknowledge;
        private Date responseDate;
        private Date dateRequested;

        private String supervisorMessage;
        private int quantity;
        private boolean acknowledged;

        protected final long serialVersionUID = 1l;
    }
}
