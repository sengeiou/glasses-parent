package com.dili.glasses.domain.dto;

import com.dili.glasses.consts.BusinessMessageTypeConstants;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * @author Ren HongWei
 * @date 2019-09-20 16:41
 * @description 更新配置
 **/
@Getter
@Setter
public class UpdateSystemConfigRequestDto extends BaseBusinessServerRequestDto {

    public UpdateSystemConfigRequestDto() {
        super(BusinessMessageTypeConstants.UPDATE_CONFIG);
    }

    /**
     * 终端id集合
     * 如果指定该参数，则只向这些终端发送更新配置消息，否则，向所有设备发送消息
     */
    private Set<Integer> terminalIdSet;
}
