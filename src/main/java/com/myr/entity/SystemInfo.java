package com.myr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemInfo {


    /**
     * 服务单元的名称，通常以 .service 结尾
     */
    public String unit;

    /**
     * 单元定义文件是否已被 systemd 成功加载。
     */
    public String load;

    /**
     * 服务的高层级激活状态，通常反映服务是否正在运行。
     */
    public String active;

    /**
     * 服务的低层级子状态，提供了更细致的状态描述。
     */
    public String sub;

    /**
     * 对服务的简短描述，帮助你识别这个服务是做什么的。
     */
    public String description;

}
