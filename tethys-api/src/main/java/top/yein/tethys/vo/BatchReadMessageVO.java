package top.yein.tethys.vo;

import java.util.List;
import lombok.Data;

/**
 * 批量更新消息已读状态 VO.
 *
 * @author KK (kzou227@qq.com)
 */
@Data
public class BatchReadMessageVO {

  /** 消息 IDs. */
  private List<String> messageIds;
}
