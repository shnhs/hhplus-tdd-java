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
    idValidationCheck(id);
    return userPointTable.selectById(id);
  }

  public List<PointHistory> getPointHistory(long id) throws Exception {
    idValidationCheck(id);
    return pointHistoryTable.selectAllByUserId(id);
  }

  public UserPoint chargePoint(long id, long chargeAmount) throws Exception {
    idValidationCheck(id);
    amountValidationCheck(chargeAmount);

    long prevPointAmount = userPointTable.selectById(id).point();

    UserPoint chargedPoint = userPointTable.insertOrUpdate(
      id,
      prevPointAmount + chargeAmount
    );

    pointHistoryTable.insert(
      id,
      chargeAmount,
      TransactionType.CHARGE,
      System.currentTimeMillis()
    );

    return chargedPoint;
  }

  public UserPoint usePoint(long id, long useAmount) throws Exception {
    idValidationCheck(id);
    amountValidationCheck(useAmount);

    long prevPoint = userPointTable.selectById(id).point();

    if (prevPoint < useAmount) {
      throw new Exception("포인트가 부족합니다.");
    }

    UserPoint insertOrUpdate = userPointTable.insertOrUpdate(
      id,
      prevPoint - useAmount
    );

    pointHistoryTable.insert(
      id,
      useAmount,
      TransactionType.USE,
      System.currentTimeMillis()
    );

    return insertOrUpdate;
  }

  private void idValidationCheck(Long id) throws Exception {
    if (id < 0) {
      throw new Exception("유저 ID는 음수일 수 없습니다.");
    }
  }

  private void amountValidationCheck(Long amount) throws Exception {
    if (amount < 0) {
      throw new Exception("포인트 충전/사용 값은 음수일 수 없습니다.");
    }
  }
}
