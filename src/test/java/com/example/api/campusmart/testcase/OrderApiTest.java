package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.api.GoodsApi;
import com.example.api.campusmart.api.OrderApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.context.TestAccount;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.goods.GoodsAddRequest;
import com.example.api.campusmart.dto.trade.OrderVo;
import com.example.api.campusmart.util.JwtUtil;
import com.example.api.campusmart.util.RandomUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("订单接口测试")
public class OrderApiTest extends BaseTest {

    private static Long sharedGoodsId;
    private static Long sharedOrderId;
    private static TestAccount thirdAccount;

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

        thirdAccount = registerThirdAccount();
    }

    @Test
    @DisplayName("买家创建订单成功")
    void shouldCreateOrderSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> result = OrderApi.createOrder(buyerToken, sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getOrderID()).isNotNull();
        assertThat(result.getData().getStatus()).isEqualTo("CREATED");
        assertThat(result.getData().getBuyerID()).isEqualTo(AccountContext.getBuyer().getUserId());
        assertThat(result.getData().getSellerID()).isEqualTo(AccountContext.getSeller().getUserId());
    }

    @Test
    @DisplayName("买家不能购买自己发布的商品")
    void shouldRejectSelfPurchase() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<OrderVo> result = OrderApi.createOrder(sellerToken, sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.GOODS_SELF_PURCHASE.getCode());
    }

    @Test
    @DisplayName("重复创建同一商品的订单返回已有订单")
    void shouldReturnExistingOrderWhenCreateDuplicate() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> result = OrderApi.createOrder(buyerToken, sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData().getOrderID()).isEqualTo(sharedOrderId);
    }

    @Test
    @DisplayName("查询订单详情成功")
    void shouldGetOrderDetailSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> result = OrderApi.getOrderDetail(buyerToken, sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getOrderID()).isEqualTo(sharedOrderId);
        assertThat(result.getData().getGoodID()).isEqualTo(sharedGoodsId);
        assertThat(result.getData().getStatus()).isEqualTo("CREATED");
    }

    @Test
    @DisplayName("非订单参与者无法查看订单详情")
    void shouldRejectOrderDetailForNonParticipant() {
        Result<OrderVo> result = OrderApi.getOrderDetail(thirdAccount.getToken(), sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.ORDER_NO_PERMISSION.getCode());
    }

    @Test
    @DisplayName("买家订单列表包含订单")
    void shouldListBuyerOrdersSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<List<OrderVo>> result = OrderApi.listBuyerOrders(buyerToken);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotEmpty();

        boolean containsSharedOrder = result.getData().stream()
                .anyMatch(o -> o.getOrderID().equals(sharedOrderId));
        assertThat(containsSharedOrder).isTrue();
    }

    @Test
    @DisplayName("卖家订单列表包含订单")
    void shouldListSellerOrdersSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<List<OrderVo>> result = OrderApi.listSellerOrders(sellerToken);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotEmpty();

        boolean containsSharedOrder = result.getData().stream()
                .anyMatch(o -> o.getOrderID().equals(sharedOrderId));
        assertThat(containsSharedOrder).isTrue();
    }

    @Test
    @DisplayName("取消 CREATED 订单成功")
    void shouldCancelOrderSuccessfully() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, sharedGoodsId);
        Long orderId = createResult.getData().getOrderID();

        Result<Boolean> cancelResult = OrderApi.cancelOrder(buyerToken, orderId);
        assertThat(cancelResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(cancelResult.getData()).isTrue();

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(buyerToken, orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("取消已取消的订单失败")
    void shouldRejectCancelNonCreatedOrder() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, sharedGoodsId);
        Long orderId = createResult.getData().getOrderID();
        OrderApi.cancelOrder(buyerToken, orderId);

        Result<Boolean> cancelAgainResult = OrderApi.cancelOrder(buyerToken, orderId);
        assertThat(cancelAgainResult.getCode()).isEqualTo(ResultCode.ORDER_CANCEL_NOT_ALLOWED.getCode());
    }

    private static TestAccount registerThirdAccount() {
        RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();
        AuthApi.register(registerRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();
        Result<String> loginResult = AuthApi.login(loginRequest);

        Long userId = JwtUtil.parseUserId(loginResult.getData());
        return TestAccount.builder()
                .userId(userId)
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .token(loginResult.getData())
                .build();
    }
}
