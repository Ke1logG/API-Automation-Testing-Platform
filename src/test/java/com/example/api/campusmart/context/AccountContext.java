package com.example.api.campusmart.context;

public class AccountContext {

    private static final ThreadLocal<TestAccount> SELLER = new ThreadLocal<>();
    private static final ThreadLocal<TestAccount> BUYER = new ThreadLocal<>();

    public static void setSeller(TestAccount account) {
        SELLER.set(account);
    }

    public static TestAccount getSeller() {
        return SELLER.get();
    }

    public static void removeSeller() {
        SELLER.remove();
    }

    public static void setBuyer(TestAccount account) {
        BUYER.set(account);
    }

    public static TestAccount getBuyer() {
        return BUYER.get();
    }

    public static void removeBuyer() {
        BUYER.remove();
    }

    public static void clear() {
        removeSeller();
        removeBuyer();
    }
}
