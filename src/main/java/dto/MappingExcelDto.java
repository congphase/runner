package dto;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelRow;
import lombok.Data;

@Data
public class MappingExcelDto {
  @ExcelCell(0)
  private String oldKey;

  @ExcelCell(1)
  private String newKey;

  @ExcelRow private int rowIndex;
}
