package io.hhplus.tdd.point;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
