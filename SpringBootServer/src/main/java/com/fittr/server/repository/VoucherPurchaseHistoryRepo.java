package com.fittr.server.repository;

import com.fittr.server.model.VoucherPurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository class for voucher purchase history
 *
 * @author Rakesh Kumar
 */
public interface VoucherPurchaseHistoryRepo extends JpaRepository<VoucherPurchaseHistory, Integer>{


    List<VoucherPurchaseHistory> findAllByUserId(int userId);
}
