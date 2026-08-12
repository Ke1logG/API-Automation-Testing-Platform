package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.api.GoodsApi;
import com.example.api.campusmart.api.OrderApi;
import com.example.api.campusmart.api.PaymentApi;
import com.example.api.campusmart.api.WalletApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.context.TestAccount;
import com.example.api.campusmart.db.entity.UserWallet;
import com.example.api.campusmart.db.entity.WalletFlow;
import com.example.api.campusmart.db.service.PaymentDbService;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.goods.GoodsAddRequest;
import com.example.api.campusmart.dto.trade.OrderVo;
import com.example.api.campusmart.dto.trade.PaymentVo;
import com.example.api.campusmart.util.JwtUtil;
import com.example.api.campusmart.util.RandomUtil;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 确认收货与钱包结算链路测试
 */
@Epic("交易链路")
@Feature("确认收货与钱包结算")
@SpringBootTest(classes = com.example.api.campusmart.db.MyBatisPlusConfig.class)
@ActiveProfiles("local")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("确认收货与钱包结算测试")
public class WalletAndOrderSettlementTest extends BaseTest {

    @Autowired
    private PaymentDbService paymentDbService;

    private TestAccount thirdAccount;

    @BeforeAll
    void prepareThirdAccount() {
        thirdAccount = registerThirdAccount();
    }

    @Test
    @Story("确认收货成功")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("模拟支付成功后买家确认收货成功")
    @Description("模拟支付成功后买家确认收货，期望订单状态变为 SETTLED")
    void shouldConfirmReceiptAfterSimulatedPayment() {
        OrderData orderData = createIndependentPaidOrder();

        String buyerToken = AccountContext.getBuyer().getToken();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);

        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(confirmResult.getData()).isTrue();

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(buyerToken, orderData.orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("SETTLED");
    }

    @Test
    @Story("钱包余额结算")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("确认收货后卖家钱包余额增加")
    @Description("买家确认收货后，卖家钱包余额应增加订单金额")
    void shouldIncreaseSellerWalletBalanceAfterConfirm() {
        OrderData orderData = createIndependentPaidOrder();
        String sellerToken = AccountContext.getSeller().getToken();
        String buyerToken = AccountContext.getBuyer().getToken();

        BigDecimal balanceBefore = WalletApi.getWallet(sellerToken).getData().getBalance();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);
        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        BigDecimal balanceAfter = WalletApi.getWallet(sellerToken).getData().getBalance();
        assertThat(balanceAfter).isEqualByComparingTo(balanceBefore.add(orderData.amount));
    }

    @Test
    @Story("钱包流水记录")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("确认收货后生成卖家收入流水")
    @Description("买家确认收货后，应生成卖家 INCOME 流水记录")
    void shouldCreateSellerIncomeFlowAfterConfirm() {
        OrderData orderData = createIndependentPaidOrder();
        String sellerToken = AccountContext.getSeller().getToken();
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);
        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        List<WalletFlow> flows = WalletApi.listFlows(sellerToken).getData();
        boolean existsIncomeFlow = flows.stream().anyMatch(flow ->
                "INCOME".equals(flow.getFlowType())
                        && orderData.orderId.equals(flow.getRelatedID())
                        && orderData.amount.compareTo(flow.getAmount()) == 0);
        assertThat(existsIncomeFlow).isTrue();
    }

    @Test
    @Story("确认收货异常")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("未支付订单不能确认收货")
    @Description("对未支付订单执行确认收货，期望返回 ORDER_CONFIRM_NOT_ALLOWED")
    void shouldRejectConfirmForUnpaidOrder() {
        OrderData orderData = createIndependentOrderWithPayment();
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);

        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.ORDER_CONFIRM_NOT_ALLOWED.getCode());

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(buyerToken, orderData.orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("CREATED");
    }

    @Test
    @Story("确认收货异常")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("非订单参与者不能确认收货")
    @Description("非订单参与者执行确认收货，期望返回 ORDER_NO_PERMISSION")
    void shouldRejectConfirmByNonParticipant() {
        OrderData orderData = createIndependentPaidOrder();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(
                thirdAccount.getToken(), orderData.orderId);

        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.ORDER_NO_PERMISSION.getCode());

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(
                AccountContext.getBuyer().getToken(), orderData.orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("PAID");
    }

    @Test
    @Story("提现成功")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("卖家提现成功")
    @Description("卖家提现全部余额，期望余额扣减并生成 WITHDRAW 流水")
    void shouldWithdrawSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();

        BigDecimal balanceBefore = WalletApi.getWallet(sellerToken).getData().getBalance();
        BigDecimal amount = prepareSellerBalance();

        Result<Boolean> result = WalletApi.withdraw(sellerToken, amount, "test@alipay.com");
        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isTrue();

        BigDecimal balanceAfter = WalletApi.getWallet(sellerToken).getData().getBalance();
        assertThat(balanceAfter).isEqualByComparingTo(balanceBefore);

        List<WalletFlow> flows = WalletApi.listFlows(sellerToken).getData();
        boolean existsWithdrawFlow = flows.stream().anyMatch(flow ->
                "WITHDRAW".equals(flow.getFlowType())
                        && amount.negate().compareTo(flow.getAmount()) == 0
                        && balanceBefore.compareTo(flow.getBalance()) == 0);
        assertThat(existsWithdrawFlow).isTrue();
    }

    @Test
    @Story("提现异常")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("余额不足时提现失败")
    @Description("余额不足时执行提现，期望返回 WALLET_BALANCE_INSUFFICIENT")
    void shouldRejectWithdrawWhenBalanceInsufficient() {
        // 后端是钱包懒加载，所以先触发钱包创建，避免 withdraw 事务回滚后钱包不存在
        WalletApi.getWallet(thirdAccount.getToken());

        Result<Boolean> result = WalletApi.withdraw(
                thirdAccount.getToken(), new BigDecimal("100"), "test@alipay.com");

        assertThat(result.getCode()).isEqualTo(ResultCode.WALLET_BALANCE_INSUFFICIENT.getCode());

        BigDecimal balance = WalletApi.getWallet(thirdAccount.getToken()).getData().getBalance();
        assertThat(balance).isEqualByComparingTo(BigDecimal.ZERO);
    }

    /* 
    @Test
    @Story("提现异常")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("提现金额非法时失败")
    void shouldRejectWithdrawWithInvalidAmount() {
        Result<Boolean> zeroResult = WalletApi.withdraw(
                thirdAccount.getToken(), BigDecimal.ZERO, "test@alipay.com");
        assertThat(zeroResult.getCode()).isEqualTo(ResultCode.WITHDRAW_AMOUNT_INVALID.getCode());

        Result<Boolean> negativeResult = WalletApi.withdraw(
                thirdAccount.getToken(), new BigDecimal("-1"), "test@alipay.com");
        assertThat(negativeResult.getCode()).isEqualTo(ResultCode.WITHDRAW_AMOUNT_INVALID.getCode());
    }*/

    @ParameterizedTest(name = "{3}")
    @CsvSource(delimiter = '|',value = {
            "zeroCase | 0 | WITHDRAW_AMOUNT_INVALID | 提现失败-提现金额为零",
            "negativeCase | -1 | WITHDRAW_AMOUNT_INVALID | 提现失败-提现金额为负"
    })
    @Story("提现异常")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("提现金额非法时失败")
    @Description("使用 0 或负数金额提现，期望返回 WITHDRAW_AMOUNT_INVALID")
    void shouldRejectWithdrawWithInvalidAmount(String caseName , BigDecimal amount , ResultCode expectedCode, String description) {
        Result<Boolean> result = WalletApi.withdraw(thirdAccount.getToken(), amount, "test@alipay.com");
        assertThat(result.getCode()).isEqualTo(expectedCode.getCode());
    }







    //辅助方法
    
    private BigDecimal prepareSellerBalance() {
        OrderData orderData = createIndependentPaidOrder();
        Result<Boolean> confirmResult = OrderApi.confirmOrder(
                AccountContext.getBuyer().getToken(), orderData.orderId);
        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        return orderData.amount;
    }

    private OrderData createIndependentPaidOrder() {
        OrderData orderData = createIndependentOrderWithPayment();
        boolean simulated = paymentDbService.simulatePaid(orderData.orderId);
        assertThat(simulated).isTrue();
        return orderData;
    }

    private OrderData createIndependentOrderWithPayment() {
        TestAccount seller = AccountContext.getSeller();
        TestAccount buyer = AccountContext.getBuyer();

        Long goodsId = publishIndependentGoods(seller.getToken(), seller.getUserId());

        Result<OrderVo> orderResult = OrderApi.createOrder(buyer.getToken(), goodsId);
        assertThat(orderResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        Long orderId = orderResult.getData().getOrderID();

        Result<PaymentVo> paymentResult = PaymentApi.createPayment(buyer.getToken(), orderId);
        assertThat(paymentResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        return new OrderData(orderId, orderResult.getData().getAmount());
    }

    private static Long publishIndependentGoods(String sellerToken, Long sellerId) {
        GoodsAddRequest request = RandomUtil.randomGoodsAddRequest(sellerId);
        Result<Long> result = GoodsApi.addGoods(sellerToken, request);
        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        return result.getData();
    }

    private static TestAccount registerThirdAccount() {
        RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();
        Result<Void> registerResult = AuthApi.register(registerRequest);
        assertThat(registerResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        LoginRequest loginRequest = LoginRequest.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();
        Result<String> loginResult = AuthApi.login(loginRequest);
        assertThat(loginResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        Long userId = JwtUtil.parseUserId(loginResult.getData());
        return TestAccount.builder()
                .userId(userId)
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .token(loginResult.getData())
                .build();
    }

    private static class OrderData {
        private final Long orderId;
        private final BigDecimal amount;

        OrderData(Long orderId, BigDecimal amount) {
            this.orderId = orderId;
            this.amount = amount;
        }
    }
}
