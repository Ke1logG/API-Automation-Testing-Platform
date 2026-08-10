package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.GoodsApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.goods.GoodsAddRequest;
import com.example.api.campusmart.dto.goods.GoodsDetail;
import com.example.api.campusmart.dto.goods.GoodsVo;
import com.example.api.campusmart.dto.goods.PageResult;
import com.example.api.campusmart.util.RandomUtil;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商品发布与浏览链路测试
 */
@DisplayName("商品接口测试")
public class GoodsApiTest extends BaseTest {

    private static Long sharedGoodsId;
    private static String sharedGoodsTitle;
    private static Long goodsToDeleteId;

    @BeforeAll
    static void prepareSharedGoods() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        GoodsAddRequest requestA = RandomUtil.randomGoodsAddRequest(sellerId);
        sharedGoodsTitle = requestA.getTitle();
        Result<Long> resultA = GoodsApi.addGoods(sellerToken, requestA);
        assertThat(resultA.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        sharedGoodsId = resultA.getData();

        GoodsAddRequest requestB = RandomUtil.randomGoodsAddRequest(sellerId);
        Result<Long> resultB = GoodsApi.addGoods(sellerToken, requestB);
        assertThat(resultB.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        goodsToDeleteId = resultB.getData();
    }

    @Test
    @DisplayName("发布商品成功")
    void shouldPublishGoodsSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        GoodsAddRequest request = RandomUtil.randomGoodsAddRequest(sellerId);
        Result<Long> result = GoodsApi.addGoods(sellerToken, request);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData()).isGreaterThan(0);
    }

    @Test
    @DisplayName("查询商品详情成功")
    void shouldGetGoodsDetailSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        Result<GoodsDetail> result = GoodsApi.getGoodsById(sellerToken, sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getGoodID()).isEqualTo(sharedGoodsId);
        assertThat(result.getData().getPublishUserID()).isEqualTo(sellerId);
        assertThat(result.getData().getTitle()).isEqualTo(sharedGoodsTitle);
    }

    @Test
    @DisplayName("分页查询商品列表")
    void shouldPageGoodsSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<PageResult<GoodsVo>> result = GoodsApi.pageGoods(sellerToken, 1, 10);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getRecords()).isNotEmpty();
        assertThat(result.getData().getCurrent()).isEqualTo(1);
        assertThat(result.getData().getSize()).isEqualTo(10);

        boolean containsSharedGoods = result.getData().getRecords().stream()
                .anyMatch(g -> g.getGoodID().equals(sharedGoodsId));
        assertThat(containsSharedGoods).isTrue();
    }

    @Test
    @DisplayName("按标题搜索商品")
    void shouldSearchGoodsByTitleSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<PageResult<GoodsVo>> hitResult = GoodsApi.searchGoodsByTitle(sellerToken, 1, 10, sharedGoodsTitle);
        assertThat(hitResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        boolean containsSharedGoods = hitResult.getData().getRecords().stream()
                .anyMatch(g -> g.getGoodID().equals(sharedGoodsId));
        assertThat(containsSharedGoods).isTrue();

        String noHitKeyword = "不存在的商品关键字_abc123";
        Result<PageResult<GoodsVo>> noHitResult = GoodsApi.searchGoodsByTitle(sellerToken, 1, 10, noHitKeyword);
        boolean containsSharedGoodsInNoHit = noHitResult.getData().getRecords().stream()
                .anyMatch(g -> g.getGoodID().equals(sharedGoodsId));
        assertThat(containsSharedGoodsInNoHit).isFalse();
    }

    @Test
    @DisplayName("按发布人查询商品")
    void shouldGetGoodsByPosterSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();
        Long buyerId = AccountContext.getBuyer().getUserId();

        Result<PageResult<GoodsVo>> sellerResult = GoodsApi.getGoodsByPoster(sellerToken, 1, 10, sellerId);
        assertThat(sellerResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        boolean containsSharedGoods = sellerResult.getData().getRecords().stream()
                .anyMatch(g -> g.getGoodID().equals(sharedGoodsId));
        assertThat(containsSharedGoods).isTrue();

        Result<PageResult<GoodsVo>> buyerResult = GoodsApi.getGoodsByPoster(sellerToken, 1, 10, buyerId);
        boolean containsSharedGoodsInBuyerResult = buyerResult.getData().getRecords().stream()
                .anyMatch(g -> g.getGoodID().equals(sharedGoodsId));
        assertThat(containsSharedGoodsInBuyerResult).isFalse();
    }

    @Test
    @DisplayName("删除商品成功")
    void shouldDeleteGoodsSuccessfully() {
        String sellerToken = AccountContext.getSeller().getToken();

        Result<Boolean> deleteResult = GoodsApi.deleteGoodsById(sellerToken, goodsToDeleteId);
        assertThat(deleteResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(deleteResult.getData()).isTrue();

        // 删除后再次查询，验证缓存与数据库最终一致
        Result<GoodsDetail> queryResult = GoodsApi.getGoodsById(sellerToken, goodsToDeleteId);
        assertThat(queryResult.getData()).isNull();
    }

    @Test
    @DisplayName("未登录访问商品接口应返回 401")
    void shouldRejectUnauthorizedAccess() {
        Response response = GoodsApi.getGoodsByIdWithoutToken(sharedGoodsId);
        assertThat(response.getStatusCode()).isEqualTo(401);
    }

    @Test
    @DisplayName("商品标题为空时发布失败")
    void shouldRejectInvalidGoodsTitle() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        GoodsAddRequest invalidRequest = GoodsAddRequest.builder()
                .publishUserID(sellerId)
                .title("")
                .appearance("九成新")
                .itemDescription("无效商品")
                .price(100L)
                .build();

        Response response = GoodsApi.addGoodsRaw(sellerToken, invalidRequest);
        assertThat(response.getStatusCode()).isNotEqualTo(200);
    }

    @Test
    @DisplayName("商品价格为负数时发布失败")
    void shouldRejectInvalidGoodsPrice() {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        GoodsAddRequest invalidRequest = GoodsAddRequest.builder()
                .publishUserID(sellerId)
                .title(RandomUtil.randomGoodsTitle())
                .appearance("九成新")
                .itemDescription("无效商品")
                .price(-1L)
                .build();

        Response response = GoodsApi.addGoodsRaw(sellerToken, invalidRequest);
        assertThat(response.getStatusCode()).isNotEqualTo(200);
    }
}
