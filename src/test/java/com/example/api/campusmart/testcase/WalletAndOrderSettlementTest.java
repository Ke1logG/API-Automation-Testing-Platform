package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.api.GoodsApi;
import com.example.api.campusmart.api.OrderApi;
import com.example.api.campusmart.api.PaymentApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.context.TestAccount;
import com.example.api.campusmart.db.entity.UserWallet;
import com.example.api.campusmart.db.entity.WalletFlow;
import com.example.api.campusmart.db.service.PaymentDbService;
import com.example.api.campusmart.db.service.WalletDbService;
import com.example.api.campusmart.db.service.WalletFlowDbService;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.goods.GoodsAddRequest;
import com.example.api.campusmart.dto.trade.OrderVo;
import com.example.api.campusmart.dto.trade.PaymentVo;
import com.example.api.campusmart.util.JwtUtil;
import com.example.api.campusmart.util.RandomUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

    @Autowired
    private WalletDbService walletDbService;

    @Autowired
    private WalletFlowDbService walletFlowDbService;

    private TestAccount thirdAccount;

    @BeforeAll
    void prepareThirdAccount() {
        thirdAccount = registerThirdAccount();
    }

    @Test
    @Story("确认收货成功")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("模拟支付成功后买家确认收货成功")
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
    void shouldIncreaseSellerWalletBalanceAfterConfirm() {
        OrderData orderData = createIndependentPaidOrder();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        UserWallet walletBefore = walletDbService.getByUserId(sellerId);
        BigDecimal balanceBefore = walletBefore == null ? BigDecimal.ZERO : walletBefore.getBalance();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);
        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        UserWallet walletAfter = walletDbService.getByUserId(sellerId);
        assertThat(walletAfter).isNotNull();
        assertThat(walletAfter.getBalance()).isEqualByComparingTo(balanceBefore.add(orderData.amount));
    }

    @Test
    @Story("钱包流水记录")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("确认收货后生成卖家收入流水")
    void shouldCreateSellerIncomeFlowAfterConfirm() {
        OrderData orderData = createIndependentPaidOrder();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);
        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());

        List<WalletFlow> flows = walletFlowDbService.listByUserId(sellerId);
        boolean existsIncomeFlow = flows.stream().anyMatch(flow ->
                "INCOME".equals(flow.getFlowType())
                        && orderData.orderId.equals(flow.getRelatedID())
                        && orderData.amount.compareTo(flow.getAmount()) == 0);
        assertThat(existsIncomeFlow).isTrue();
    }

    @Test
    @Story("状态机校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("未支付订单不能确认收货")
    void shouldRejectConfirmForUnpaidOrder() {
        OrderData orderData = createIndependentOrderWithPayment();
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(buyerToken, orderData.orderId);

        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.ORDER_CONFIRM_NOT_ALLOWED.getCode());

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(buyerToken, orderData.orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("CREATED");
    }

    @Test
    @Story("权限校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("非订单参与者不能确认收货")
    void shouldRejectConfirmByNonParticipant() {
        OrderData orderData = createIndependentPaidOrder();

        Result<Boolean> confirmResult = OrderApi.confirmOrder(
                thirdAccount.getToken(), orderData.orderId);

        assertThat(confirmResult.getCode()).isEqualTo(ResultCode.ORDER_NO_PERMISSION.getCode());

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(
                AccountContext.getBuyer().getToken(), orderData.orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("PAID");
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
