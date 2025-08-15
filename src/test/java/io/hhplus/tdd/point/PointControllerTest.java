package io.hhplus.tdd.point;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointController.class)
public class PointControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PointService pointService;

  /**
   * 유효한 ID로 API 요청 시 정상 응답 확인
   * 의도한 UserPoint 가 리턴되는지 Id 확인
   *
   */
  @Test
  @DisplayName("/GET /point/{id} - 유효한 ID")
  void getPointValidId() throws Exception {
    // given
    Long testId = 1L;
    UserPoint userPoint = new UserPoint(
      testId,
      111L,
      System.currentTimeMillis()
    );

    // when
    given(pointService.getUserPoint(testId)).willReturn(userPoint);

    // then
    mockMvc
      .perform(get("/point/{id}", testId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(testId));
  }

  /**
   * ID가 문자열로 입력되었을 경우 BadRequest 처리 확인
   *
   */
  @Test
  @DisplayName("/GET /point/{id} - 문자열 ID")
  void getPointInvalidId() throws Exception {
    // given
    String testId = "STRING_ID";

    // when + then
    mockMvc
      .perform(get("/point/{id}", testId))
      .andExpect(status().isBadRequest());
  }

  /**
   * 유효한 ID로 정상적인 List가 리턴되는지 확인
   * 리턴된 List가 의도된 결과인지 확인하기 위해 크기 확인
   */
  @Test
  @DisplayName("/GET /point/{id}/histories - 포인트 충전/이용 내역 조회")
  void getPointHistories() throws Exception {
    // given
    Long testId = 1L;
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

    // when
    given(pointService.getPointHistory(testId)).willReturn(pointHistories);

    // then
    mockMvc
      .perform(get("/point/{id}/histories", testId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$", hasSize(2)));
  }
}
