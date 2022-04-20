package com.fittr.server.batch;

import java.util.List;

import com.fittr.server.model.Vouchers;
import com.fittr.server.repository.VoucherRepo;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * This class implements itemwriter code used by voucher batch
 *
 * @author Rakesh Kumar
 */

@Component
public class DBWriter implements ItemWriter<Vouchers>{

	private VoucherRepo voucherRepo;
	
	@Autowired
	public DBWriter(VoucherRepo voucherRepo) {
		this.voucherRepo=voucherRepo;
	}
	
	@Override
	public void write(List<? extends Vouchers> vouchers) throws Exception {
		voucherRepo.saveAll(vouchers);
	}
	
	
}
