package com.nju.edu.erp.model.po;

import com.nju.edu.erp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkerPO {

    /**
     * 员工id
     */
    private Integer id;

    /**
     * 员工名
     */
    private String name;

    /**
     * 性别
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

