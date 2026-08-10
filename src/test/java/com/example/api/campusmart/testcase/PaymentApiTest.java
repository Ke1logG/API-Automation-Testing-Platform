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
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("创建支付单成功")
    void shouldCreatePaymentSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, sharedGoodsId);
        Long orderId = createResult.getData().getOrderID();

        Result<PaymentVo> result = PaymentApi.createPayment(buyerToken, orderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getPaymentID()).isNotNull();
        assertThat(result.getData().getOrderID()).isEqualTo(orderId);
        assertThat(result.getData().getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("根据订单 ID 查询支付单成功")
    void shouldGetPaymentByOrderIdSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<PaymentVo> result = PaymentApi.getPaymentByOrderId(buyerToken, sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getPaymentID()).isEqualTo(sharedPaymentId);
        assertThat(result.getData().getOrderID()).isEqualTo(sharedOrderId);
    }

    @Test
    @DisplayName("重复创建同一订单的支付单返回已有支付单")
    void shouldReturnExistingPaymentWhenCreateDuplicate() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<PaymentVo> result = PaymentApi.createPayment(buyerToken, sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData().getPaymentID()).isEqualTo(sharedPaymentId);
    }

    @Test
    @DisplayName("非买家创建支付单失败")
    void shouldRejectPaymentCreationByNonBuyer() {
        String sellerToken = AccountContext.getSeller().getToken();

        Response response = PaymentApi.createPaymentRaw(sellerToken, sharedOrderId);
        assertThat(response.getStatusCode()).isNotEqualTo(200);
    }

    @Test
    @DisplayName("非 CREATED 状态订单不能创建支付单")
    void shouldRejectPaymentCreationForNonCreatedOrder() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, sharedGoodsId);
        Long orderId = createResult.getData().getOrderID();
        OrderApi.cancelOrder(buyerToken, orderId);

        Response response = PaymentApi.createPaymentRaw(buyerToken, orderId);
        assertThat(response.getStatusCode()).isNotEqualTo(200);
    }

    @Test
    @DisplayName("取消订单后支付单变为 CLOSED")
    void shouldClosePaymentWhenOrderCancelled() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, sharedGoodsId);
        Long orderId = createResult.getData().getOrderID();

        Result<PaymentVo> paymentResult = PaymentApi.createPayment(buyerToken, orderId);
        Long paymentId = paymentResult.getData().getPaymentID();
        assertThat(paymentResult.getData().getStatus()).isEqualTo("PENDING");

        OrderApi.cancelOrder(buyerToken, orderId);

        Result<PaymentVo> queryResult = PaymentApi.getPaymentByOrderId(buyerToken, orderId);
        assertThat(queryResult.getData().getPaymentID()).isEqualTo(paymentId);
        assertThat(queryResult.getData().getStatus()).isEqualTo("CLOSED");
    }
}
