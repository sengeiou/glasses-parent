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
@Table(name = "`device_long_short_num_map`")
public interface DeviceLongShortNumMap extends IBaseDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    @FieldDef(label="id")
    @EditMode(editor = FieldEditor.Number, required = true)
    Long getId();

    void setId(Long id);

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

    @Column(name = "`long_num`")
    @FieldDef(label="长编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getLongNum();

    void setLongNum(String longNum);

    @Column(name = "`short_num`")
    @FieldDef(label="短编号")
    @EditMode(editor = FieldEditor.Number, required = false)
    Integer getShortNum();

    void setShortNum(Integer shortNum);

    @Column(name = "`create_record_id`")
    @FieldDef(label="创建记录id")
    @EditMode(editor = FieldEditor.Number, required = false)
    Long getCreateRecordId();

    void setCreateRecordId(Long createRecordId);

    @Column(name = "`batch`")
    @FieldDef(label="批次编号", maxLength = 255)
    @EditMode(editor = FieldEditor.Text, required = false)
    String getBatch();

    void setBatch(String batch);
}