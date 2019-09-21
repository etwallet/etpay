/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.etwallet.etpay.pojo;

/**
 *
 * @author abill
 */
public class CoinType {

    private String name;
    private String contract;
    private int decimal;

    public CoinType(String name, String contract, int decimal) {
        this.name = name;
        this.contract = contract;
        this.decimal = decimal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }

}
