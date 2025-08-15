package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PointServiceTest {

  private PointService pointService;

  private UserPointTable userPointTable;
  private PointHistoryTable pointHistoryTable;

  @BeforeEach
  void setUp() {
    userPointTable = mock(UserPointTable.class);
    pointHistoryTable = mock(PointHistoryTable.class);
    pointService = new PointService(userPointTable, pointHistoryTable);
  }

  /**
   * 포인트 조회 기능은 UserPointTable을 통해 유저 ID로 조회.
   * UserPointTable의 selectById가 호출되었는지 확인.
   * 호출된 결과가 원하는 UserPoint인지 검증하기 위해 ID를 확인.
   */
  @Test
  @DisplayName("getUserPoint - 유효한 ID")
  void getUserPoint_validId() throws Exception {
    // given
    Long testId = 1L;
    UserPoint userPoint = new UserPoint(
      testId,
      111L,
      System.currentTimeMillis()
    );

    // when
    given(userPointTable.selectById(testId)).willReturn(userPoint);
    UserPoint result = pointService.getUserPoint(testId);

    // then
    verify(userPointTable).selectById(testId);
    assertEquals(result.id(), testId);
  }

  /**
   * 음수 ID는 부적절한 입력으로 간주.
   * 음수 ID로 조회가 요청되었을 경우 오류 처리
   */
  @Test
  @DisplayName("getUserPoint - 음수 ID")
  void getUserId_invalidId() {
    //given
    Long invalidId = -1L;

    // when + then
    assertThatThrownBy(() -> pointService.getUserPoint(invalidId))
      .isInstanceOf(Exception.class)
      .hasMessageContaining("음수");
  }

  /**
   * 포인트 내역 조회 기능은 PointHistoryTable을 통해 유저 ID로 조회.
   * PointHistoryTable의 selectAllByUserId가 호출되었는지 확인.
   * 호출된 결과가 의도된 List인지 검증하기 위해 리스트의 사이즈 확인.
   *
   */
  @Test
  @DisplayName("getPointHistories - 정상적인 ID")
  void getPointHistory_validId() throws Exception {
    // given
    Long validId = 1L;
    List<PointHistory> pointHistories = List.of(
      new PointHistory(
        0,
        1L,
        10000L,
        TransactionType.CHARGE,
        System.currentTimeMillis()
      ),
      new PointHistory(
        1,
        1L,
        5000,
        TransactionType.USE,
        System.currentTimeMillis()
      )
    );

    //when
    given(pointHistoryTable.selectAllByUserId(validId))
      .willReturn(pointHistories);
    List<PointHistory> result = pointService.getPointHistory(validId);

    // then
    verify(pointHistoryTable).selectAllByUserId(validId);
    assertEquals(result.size(), 2);
  }

  /**
   * UserPointTable 로직상 기존 포인트에 충전할 포인트을 더하여 insertOrUpdate를 호출.
   * 충전 결과 포인트로 메서드가 호출되는지 확인.
   * 호출후 리턴되는 값이 기존 포인트 + 충전할 포인트 인지 확인.
   * 충전으로 포인트 내역을 쌓았는지 확인.
   */
  @Test
  @DisplayName("chargePoint - 정상적인 입력값")
  void patchChargePoint_validInput() throws Exception {
    // given
    Long validId = 1L;
    Long prevAmount = 1000L;
    Long chargeAmount = 2000L;

    UserPoint previousPoint = new UserPoint(
      validId,
      prevAmount,
      System.currentTimeMillis()
    );

    UserPoint chargedPoint = new UserPoint(
      validId,
      prevAmount + chargeAmount,
      System.currentTimeMillis()
    );

    // when
    given(userPointTable.selectById(validId)).willReturn(previousPoint);
    given(userPointTable.insertOrUpdate(validId, prevAmount + chargeAmount))
      .willReturn(chargedPoint);

    UserPoint result = pointService.chargePoint(validId, chargeAmount);

    // then
    verify(userPointTable).selectById(validId);
    verify(userPointTable).insertOrUpdate(validId, prevAmount + chargeAmount);

    assertEquals(result.point(), prevAmount + chargeAmount);
  }

  @Test
  @DisplayName("chargePoint - 음수 포인트 충전 시도")
  void patchChargePoint_invalidAmount() {
    Long validId = 1L;
    Long invalidAmount = -1000L;

    // when + then
    assertThatThrownBy(() -> pointService.chargePoint(validId, invalidAmount))
      .isInstanceOf(Exception.class)
      .hasMessageContaining("음수");
  }
}
