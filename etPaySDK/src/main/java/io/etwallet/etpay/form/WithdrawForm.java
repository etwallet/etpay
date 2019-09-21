package io.etwallet.etpay.form;

import java.io.Serializable;
import java.math.BigDecimal;

public class WithdrawForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long uid;
	private String coinName;
	private String address;
	private String serial_number;
	private BigDecimal amount;
	private String memo;
	private String code;
	private String password;
	private BigDecimal fee;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getCoinName() {
		return coinName;
	}

	public void setCoinName(String coinName) {
		this.coinName = coinName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getMemo() {
		if (memo == null)
			return "";
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

}
