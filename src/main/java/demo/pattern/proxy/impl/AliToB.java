package demo.pattern.proxy.impl;

import demo.pattern.proxy.ToBPayment;

public class AliToB implements ToBPayment{
    ToBPayment toBPayment;
    public AliToB(ToBPayment toBPayment){
        this.toBPayment=toBPayment;
    }

    public void pay() {
        beforePay();
        toBPayment.pay();
        afterPay();
    }

    private void beforePay() {
        System.out.println("从银行取款");
    }
    private void afterPay() {
        System.out.println("给商家付款转账");
    }
}
