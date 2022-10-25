package com.nju.edu.erp.model.vo.workerManagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerVO {

    /**
     * 员工id
     */
    private Integer id;

    /**
     * 员工名称
     */
    private String name;

    /**
     * 性别
     * 0为女，1为男
     */
    private Integer sex;

    /**
     * 生日
     */
    private String birth;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 角色
     */
    private String role;
}
