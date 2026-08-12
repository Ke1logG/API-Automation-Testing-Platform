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
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Epic("交易链路")
@Feature("订单管理")
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
    @Story("创建订单")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("买家创建订单成功")
    @Description("买家对商品创建订单，期望订单状态为 CREATED")
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
    @Story("创建订单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("买家购买自己发布的商品失败")
    @Description("卖家尝试购买自己发布的商品，期望返回 GOODS_SELF_PURCHASE")
    void shouldRejectSelfPurchase() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<OrderVo> result = OrderApi.createOrder(sellerToken, sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.GOODS_SELF_PURCHASE.getCode());
    }

    @Test
    @Story("创建订单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("重复创建同一商品订单返回已有订单")
    @Description("买家重复创建同一商品的订单，期望返回已有订单")
    void shouldReturnExistingOrderWhenCreateDuplicate() {
        String buyerToken = AccountContext.getBuyer().getToken();

        Result<OrderVo> result = OrderApi.createOrder(buyerToken, sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData().getOrderID()).isEqualTo(sharedOrderId);
    }

    @Test
    @Story("查询订单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("查询订单详情成功")
    @Description("订单参与者查询订单详情，期望返回正确订单信息")
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
    @Story("权限校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("非订单参与者查看订单详情失败")
    @Description("非订单参与者查询订单详情，期望返回 ORDER_NO_PERMISSION")
    void shouldRejectOrderDetailForNonParticipant() {
        Result<OrderVo> result = OrderApi.getOrderDetail(thirdAccount.getToken(), sharedOrderId);

        assertThat(result.getCode()).isEqualTo(ResultCode.ORDER_NO_PERMISSION.getCode());
    }

    @Test
    @Story("查询订单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("查询买家订单列表包含订单")
    @Description("买家查询自己的订单列表，期望包含共享订单")
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
    @Story("查询订单")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("查询卖家订单列表包含订单")
    @Description("卖家查询自己的订单列表，期望包含共享订单")
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
    @Story("取消订单")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("取消 CREATED 订单成功")
    @Description("买家取消 CREATED 状态订单，期望订单状态变为 CANCELLED")
    void shouldCancelOrderSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        Long goodsId = publishIndependentGoods(sellerToken, sellerId);
        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, goodsId);
        Long orderId = createResult.getData().getOrderID();

        assertThat(createResult.getData().getStatus()).isEqualTo("CREATED");

        Result<Boolean> cancelResult = OrderApi.cancelOrder(buyerToken, orderId);
        assertThat(cancelResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(cancelResult.getData()).isTrue();

        Result<OrderVo> detailResult = OrderApi.getOrderDetail(buyerToken, orderId);
        assertThat(detailResult.getData().getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    @Story("状态机校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("取消已取消的订单失败")
    @Description("买家重复取消已取消的订单，期望返回 ORDER_CANCEL_NOT_ALLOWED")
    void shouldRejectCancelNonCreatedOrder() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();
        String buyerToken = AccountContext.getBuyer().getToken();

        Long goodsId = publishIndependentGoods(sellerToken, sellerId);
        Result<OrderVo> createResult = OrderApi.createOrder(buyerToken, goodsId);
        Long orderId = createResult.getData().getOrderID();
        OrderApi.cancelOrder(buyerToken, orderId);

        Result<Boolean> cancelAgainResult = OrderApi.cancelOrder(buyerToken, orderId);
        assertThat(cancelAgainResult.getCode()).isEqualTo(ResultCode.ORDER_CANCEL_NOT_ALLOWED.getCode());
    }

    private static Long publishIndependentGoods(String sellerToken, Long sellerId) {
        GoodsAddRequest request = RandomUtil.randomGoodsAddRequest(sellerId);
        Result<Long> result = GoodsApi.addGoods(sellerToken, request);
        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        return result.getData();
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
