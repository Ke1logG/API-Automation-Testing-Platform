package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.GoodsApi;
import com.example.api.campusmart.api.OrderApi;
import com.example.api.campusmart.api.PaymentApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.goods.GoodsAddRequest;
import com.example.api.campusmart.dto.trade.OrderVo;
import com.example.api.campusmart.dto.trade.PaymentVo;
import com.example.api.campusmart.util.RandomUtil;
import io.qameta.allure.Allure;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("交易链路")
@Feature("支付单管理")
@DisplayName("支付单接口测试")
public class PaymentApiTest extends BaseTest {

    private static Long sharedGoodsId;
    private static Long sharedOrderId;
    private static Long sharedPaymentId;

    @BeforeAll
    static void prepareSharedData() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        GoodsAddRequest goodsRequest = RandomUtil.randomGoodsAddRequest(sellerId);
        Result<Long> goodsResult = GoodsApi.addGoods(sellerToken, goodsRequest);
        assertThat(goodsResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        sharedGoodsId = goodsResult.getData();

        String buyerToken = AccountContext.getBuyer().getToken();
        Result<OrderVo> orderResult = OrderApi.createOrder(buyerToken, sharedGoodsId);
        assertThat(orderResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        sharedOrderId = orderResult.getData().getOrderID();

        Result<PaymentVo> paymentResult = PaymentApi.createPayment(buyerToken, sharedOrderId);
        assertThat(paymentResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        sharedPaymentId = paymentResult.getData().getPaymentID();
    }

    @Test
    @Story("创建支付单")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("创建支付单成功")
    @Description("买家为 CREATED 状态订单创建支付单，期望支付单状态为 PENDING")
    void shouldCreatePaymentSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        Long goodsId = publishIndependentGoods(sellerToken, sellerId);
        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, goodsId);
        Long orderId = createResult.getData().getOrderID();

        Result<PaymentVo> result = PaymentApi.createPayment(buyerToken, orderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getPaymentID()).isNotNull();
        assertThat(result.getData().getOrderID()).isEqualTo(orderId);
        assertThat(result.getData().getStatus()).isEqualTo("PENDING");
    }

    @Test
    @Story("查询支付单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("根据订单 ID 查询支付单成功")
    @Description("根据订单 ID 查询支付单，期望返回正确支付单信息")
    void shouldGetPaymentByOrderIdSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<PaymentVo> result = PaymentApi.getPaymentByOrderId(buyerToken, sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getPaymentID()).isEqualTo(sharedPaymentId);
        assertThat(result.getData().getOrderID()).isEqualTo(sharedOrderId);
    }

    @Test
    @Story("创建支付单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("重复创建同一订单支付单返回已有支付单")
    @Description("重复为同一订单创建支付单，期望返回已有支付单")
    void shouldReturnExistingPaymentWhenCreateDuplicate() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<PaymentVo> result = PaymentApi.createPayment(buyerToken, sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData().getPaymentID()).isEqualTo(sharedPaymentId);
    }

    @Test
    @Story("权限校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("非买家创建支付单失败")
    @Description("非买家为订单创建支付单，期望返回 ORDER_NO_PERMISSION")
    void shouldRejectPaymentCreationByNonBuyer() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<PaymentVo> result = PaymentApi.createPayment(sellerToken, sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.ORDER_NO_PERMISSION.getCode());
    }

    @Test
    @Story("状态机校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("非 CREATED 状态订单不能创建支付单")
    @Description("为已取消订单创建支付单，期望返回 ORDER_STATUS_ERROR")
    void shouldRejectPaymentCreationForNonCreatedOrder() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        Long goodsId = publishIndependentGoods(sellerToken, sellerId);
        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, goodsId);
        Long orderId = createResult.getData().getOrderID();
        OrderApi.cancelOrder(buyerToken, orderId);

        Result<PaymentVo> result = PaymentApi.createPayment(buyerToken, orderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.ORDER_STATUS_ERROR.getCode());
    }

    @Test
    @Story("取消支付单")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("取消订单后支付单变为 CLOSED")
    @Description("买家取消订单后，关联支付单状态应变为 CLOSED")
    void shouldClosePaymentWhenOrderCancelled() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        Long goodsId = publishIndependentGoods(sellerToken, sellerId);

        Long orderId = Allure.step("创建独立订单", () -> {
            Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, goodsId);
            assertThat(createResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
            return createResult.getData().getOrderID();
        });

        Long paymentId = Allure.step("创建支付单", () -> {
            Result<PaymentVo> paymentResult = PaymentApi.createPayment(buyerToken, orderId);
            assertThat(paymentResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
            assertThat(paymentResult.getData().getStatus()).isEqualTo("PENDING");
            return paymentResult.getData().getPaymentID();
        });

        Allure.step("取消订单", () -> {
            Result<Boolean> cancelResult = OrderApi.cancelOrder(buyerToken, orderId);
            assertThat(cancelResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        });

        Allure.step("校验支付单状态变为 CLOSED", () -> {
            Result<PaymentVo> queryResult = PaymentApi.getPaymentByOrderId(buyerToken, orderId);
            assertThat(queryResult.getData().getPaymentID()).isEqualTo(paymentId);
            assertThat(queryResult.getData().getStatus()).isEqualTo("CLOSED");
        });
    }

    @Step("发布独立商品")
    private static Long publishIndependentGoods(String sellerToken, Long sellerId) {
        GoodsAddRequest request = RandomUtil.randomGoodsAddRequest(sellerId);
        Result<Long> result = GoodsApi.addGoods(sellerToken, request);
        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        return result.getData();
    }
}
