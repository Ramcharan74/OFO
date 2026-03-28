package com.ram.foodapp.model.foodorder;

import com.ram.foodapp.enums.PaymentMode;

import java.util.Objects;

public class Payment {
    private final boolean isPaid;
    private final PaymentMode paymentMode;

    public Payment(boolean isPaid, String paymentMode) {
        if (!isPaid && paymentMode != null) {
            throw new IllegalStateException("Unpaid order should not have payment mode");
        }
        this.isPaid = isPaid;
        this.paymentMode = paymentMode != null ? PaymentMode.valueOf(paymentMode) : null;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return isPaid == payment.isPaid && paymentMode == payment.paymentMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPaid, paymentMode);
    }
}
