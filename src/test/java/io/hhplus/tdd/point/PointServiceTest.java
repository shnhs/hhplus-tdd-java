package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PointServiceTest {

  private PointService pointService;
  private UserPointTable userPointTable;

  @BeforeEach
  void setUp() {
    userPointTable = mock(UserPointTable.class);
    pointService = new PointService(userPointTable);
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
}
