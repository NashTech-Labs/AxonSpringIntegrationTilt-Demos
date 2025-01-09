--liquibase formatted sql
--changeset ad13021:5
--preconditions onFail:WARN
CREATE TABLE IF NOT EXISTS pre_auth_charge (
    reservation_id VARCHAR(50) PRIMARY KEY NOT NULL,
    check_in_date VARCHAR(20),
    check_out_date VARCHAR(20),
    room_number VARCHAR(20),
    room_type VARCHAR(50),
    hotel_id VARCHAR(50) NOT NULL,
    amount_hold NUMERIC(10, 2),
    final_bill_amount NUMERIC(10, 2),
    additional_charges NUMERIC(10, 2),
    payment_state VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
