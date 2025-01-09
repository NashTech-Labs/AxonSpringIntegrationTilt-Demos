package org.nashtech.reception.preauth.repository;

import org.nashtech.reception.preauth.entity.PreAuthCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreAuthRepository extends JpaRepository<PreAuthCharge, String> {
}
