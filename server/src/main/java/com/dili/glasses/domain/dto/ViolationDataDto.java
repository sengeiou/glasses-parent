package com.dili.glasses.domain.dto;

import com.dili.glasses.consts.SocketMessageType;
import com.dili.glasses.domain.ViolationDataReportDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Ren HongWei
 * @date 2019-09-20 11:13
 * @description 违规数据dto
 **/
@Getter
@Setter
public class ViolationDataDto extends BaseMessageFromSocket {

    public ViolationDataDto() {
        super(SocketMessageType.VIOLATION_DATA);
    }

    /**
     * 违规数据
     */
    private List<ViolationDataReportDto> violationDataReportDtoList;
}
