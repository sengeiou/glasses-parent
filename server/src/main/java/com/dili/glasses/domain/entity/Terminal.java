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
@Table(name = "`terminal`")
public interface Terminal extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

    @Column(name = "`terminal_id`")
    @FieldDef(label="终端号")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getTerminalId();

    void setTerminalId(Integer terminalId);

    @Column(name = "`hardware_version`")
    @FieldDef(label="硬件版本")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getHardwareVersion();

    void setHardwareVersion(Integer hardwareVersion);

    @Column(name = "`software_version`")
    @FieldDef(label="软件版本")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getSoftwareVersion();

    void setSoftwareVersion(Integer softwareVersion);

    @Column(name = "`last_login_time`")
    @FieldDef(label="上次登录时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getLastLoginTime();

    void setLastLoginTime(Date lastLoginTime);

    @Column(name = "`online`")
    @FieldDef(label="0:不在线 1：在线")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getOnline();

    void setOnline(Byte online);

    @Column(name = "`last_offline_time`")
    @FieldDef(label="最近一次下线时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getLastOfflineTime();

    void setLastOfflineTime(Date lastOfflineTime);

    @Column(name = "`active`")
    @FieldDef(label="0：未激活 1：已激活")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getActive();

    void setActive(Byte active);

    @Column(name = "`active_time`")
    @FieldDef(label="激活时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getActiveTime();

    void setActiveTime(Date activeTime);

    @Column(name = "`create_time`")
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getCreateTime();

    void setCreateTime(Date createTime);

    @Column(name = "`delete_flag`")
    @FieldDef(label="删除标识，0：未删除，1：已删除")
    @EditMode(editor = FieldEditor.Text, required = false)
    Byte getDeleteFlag();

    void setDeleteFlag(Byte deleteFlag);

    @Column(name = "`modify_time`")
    @FieldDef(label="更新时间")
    @EditMode(editor = FieldEditor.Datetime, required = false)
    Date getModifyTime();

    void setModifyTime(Date modifyTime);

    @Column(name = "`long_num`")
    @FieldDef(label="设备长编号，系统内使用", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getLongNum();

    void setLongNum(String longNum);

    @Column(name = "`batch`")
    @FieldDef(label="批次编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getBatch();

    void setBatch(String batch);
}