package com.fittr.server.repository;

import com.fittr.server.model.Vouchers;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This repository class for vouchers
 *
 * @author Rakesh Kumar
 */
public interface VoucherRepo extends JpaRepository<Vouchers, Integer>{


}
