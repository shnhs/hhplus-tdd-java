package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final UserPointTable userPointTable;

  public UserPoint getUserPoint(Long id) throws Exception {
    if (id < 0) {
      throw new Exception("유저 ID는 음수일 수 없습니다.");
    }
    return userPointTable.selectById(id);
  }
}
