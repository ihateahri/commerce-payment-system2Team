package com.example.commercepaymentsystem2team.domain.payment.entity;

public enum PaymentStatus {
    IN_PROGRESS {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return target == PAID || target == FAILED || target == CANCELLED;
        }
    },

    PAID {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return target == CANCELLED;
        }
    },

    FAILED {
        @Override
        public boolean canTransitTo(PaymentStatus status) {
            return false;
        }
    },

    CANCELLED {
        @Override
        public boolean canTransitTo(PaymentStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(PaymentStatus target);
}