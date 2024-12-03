package org.apache.fineract.portfolio.service.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceMasterDetailsRepository extends JpaRepository<ServiceDetails, Long>, JpaSpecificationExecutor<ServiceDetails> {


}
