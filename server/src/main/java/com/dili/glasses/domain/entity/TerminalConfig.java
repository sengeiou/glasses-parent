package com.dili.glasses.domain.entity;

import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2019-10-24 14:25:52.
 */
@Table(name = "`terminal_config`")
public interface TerminalConfig extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`terminal_id`")
    @FieldDef(label="设备id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getTerminalId();

    void setTerminalId(Integer terminalId);

    @Column(name = "`size`")
    @FieldDef(label="字节大小")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getSize();

    void setSize(Integer size);

    @Column(name = "`type`")
    @FieldDef(label="标识码")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getType();

    void setType(Byte type);

    @Column(name = "`config_desc`")
    @FieldDef(label="标识描述", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getConfigDesc();

    void setConfigDesc(String configDesc);

    @Column(name = "`content`")
    @FieldDef(label="内容")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getContent();

    void setContent(Integer content);

    @Column(name = "`config_type`")
    @FieldDef(label="0：系统配置 1：用户配置")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getConfigType();

    void setConfigType(Byte configType);

    @Column(name = "`create_time`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreateTime();

    void setCreateTime(Date createTime);

    @Column(name = "`modify_time`")
    @FieldDef(label="更新时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModifyTime();

    void setModifyTime(Date modifyTime);

    @Column(name = "`delete_flag`")
    @FieldDef(label="删除标识")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getDeleteFlag();

    void setDeleteFlag(Byte deleteFlag);

    @Column(name = "`system_config_id`")
    @FieldDef(label="对应的系统配置的id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getSystemConfigId();

    void setSystemConfigId(Long systemConfigId);

    @Column(name = "`custom_config_id`")
    @FieldDef(label="自定义配置id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getCustomConfigId();

    void setCustomConfigId(Integer customConfigId);
}