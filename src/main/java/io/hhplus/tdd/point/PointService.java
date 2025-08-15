package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final UserPointTable userPointTable;
  private final PointHistoryTable pointHistoryTable;

  public UserPoint getUserPoint(Long id) throws Exception {
    if (id < 0) {
      throw new Exception("유저 ID는 음수일 수 없습니다.");
    }
    return userPointTable.selectById(id);
  }

  public List<PointHistory> getPointHistory(long id) throws Exception {
    if (id < 0) {
      throw new Exception("유저 ID는 음수일 수 없습니다.");
    }
    return pointHistoryTable.selectAllByUserId(id);
  }
}
