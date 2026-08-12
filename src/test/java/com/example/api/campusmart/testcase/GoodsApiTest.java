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
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商品发布与浏览链路测试
 */
@Epic("商品模块")
@Feature("商品发布与浏览")
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
    @Story("发布商品")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("发布商品成功")
    @Description("卖家使用合法参数发布商品，期望返回商品 ID")
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
    @Story("查询商品")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("查询商品详情成功")
    @Description("根据商品 ID 查询商品详情，期望返回正确的商品信息")
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
    @Story("查询商品")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("分页查询商品列表")
    @Description("分页查询商品列表，期望返回非空列表且包含共享商品")
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
    @Story("查询商品")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("根据标题搜索商品")
    @Description("根据商品标题搜索商品，命中共享商品；使用不存在的关键字搜索，期望无结果")
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
    @Story("查询商品")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("根据发布人查询商品")
    @Description("根据发布人 ID 查询商品，期望返回该发布人的商品列表")
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
    @Story("删除商品")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("删除商品成功")
    @Description("卖家删除自己发布的商品，删除后再次查询期望返回空")
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
    @Story("权限校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("未登录访问商品详情返回 TOKEN_INVALID")
    @Description("未携带 token 访问商品详情接口，期望返回 TOKEN_INVALID")
    void shouldRejectUnauthorizedAccess() {
        Result<GoodsDetail> result = GoodsApi.getGoodsByIdWithoutToken(sharedGoodsId);

        assertThat(result.getCode()).isEqualTo(ResultCode.TOKEN_INVALID.getCode());
    }

    @ParameterizedTest(name = "{3}")
    @CsvSource(delimiter = '|', value = {
            "empty_title | 100 | GOODS_TITLE_EMPTY | 商品标题为空时发布失败",
            "negative_price | -1 | GOODS_PRICE_ILLEGAL | 商品价格为负数时发布失败"
    })
    @Story("参数校验")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("商品参数校验失败")
    @Description("使用无效参数发布商品，期望返回对应错误码")
    void shouldRejectInvalidGoodsParams(String invalidScenarios, Long price, ResultCode expectedCode, String description) {
        String sellerToken = AccountContext.getSeller().getToken();
        Long sellerId = AccountContext.getSeller().getUserId();

        String title = "empty_title".equals(invalidScenarios) ? "" : RandomUtil.randomGoodsTitle();

        GoodsAddRequest invalidRequest = GoodsAddRequest.builder()
                .publishUserID(sellerId)
                .title(title)
                .appearance("九成新")
                .itemDescription("无效商品")
                .price(price)
                .build();

        Result<Long> result = GoodsApi.addGoods(sellerToken, invalidRequest);

        assertThat(result.getCode()).isEqualTo(expectedCode.getCode());
    }
}
