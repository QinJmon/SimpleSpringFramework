package demo.pattern.proxy.impl;

import demo.pattern.proxy.ToCPayment;

public class AlipayToC implements ToCPayment {
    ToCPayment toCPayment;
    public AlipayToC( ToCPayment toCPayment){
        this.toCPayment=toCPayment;
    }

    @Override
    public void pay() {
        beforePay();
        toCPayment.pay();
        afterPay();
    }

    private void beforePay() {
        System.out.println("从银行取款");
    }
    private void afterPay() {
        System.out.println("给商家付款转账");
    }
}
