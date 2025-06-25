-- 创建车辆信息表
CREATE TABLE vehicle_info (
                              vid VARCHAR(16) NOT NULL COMMENT '车辆识别码，16位随机字符串，每辆车唯一',
                              frame_number INT COMMENT '车架编号',
                              battery_type VARCHAR(20) COMMENT '电池类型',
                              total_mileage INT COMMENT '总里程(km)',
                              battery_health INT COMMENT '电池健康状态(%)',
                              PRIMARY KEY (vid) -- 设 vid 为主键，保证唯一性
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;


-- 创建规则表
CREATE TABLE alarm_rules (
                             rid INT AUTO_INCREMENT COMMENT '序号，自增作为主键',
                             rule_number INT COMMENT '规则编号',
                             alarm_rule VARCHAR(20) COMMENT '预警规则描述',
                             battery_type VARCHAR(20) COMMENT '电池类型',
                             alarm_level INT COMMENT'警报等级',
                             PRIMARY KEY (rid)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 规则映射表
CREATE TABLE rules_mapping (
                               rule_number INT COMMENT '规则编号',
                               rule_name VARCHAR(50) COMMENT '名称',
                               PRIMARY KEY (rule_number)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- 车辆信号表
CREATE TABLE car_signal (
                            sid INT COMMENT '规则编号',
                            frame_number INT COMMENT '车架编号',
                            Mx FLOAT COMMENT '最大电压',
                            Mi FLOAT COMMENT '最小电压',
                            Ix FLOAT COMMENT '最大电流',
                            Ii FLOAT COMMENT '最小电流',
                            time DATETIME COMMENT '时间',
                            PRIMARY KEY (sid)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;



-- 插入第一条数据
INSERT INTO vehicle_info (vid, frame_number, battery_type, total_mileage, battery_health)
VALUES ('abcdef1234567890', 1, '三元电池', 100, 100);
-- 插入第二条数据
INSERT INTO vehicle_info (vid, frame_number, battery_type, total_mileage, battery_health)
VALUES ('ghijkl1234567891', 2, '铁锂电池', 600, 95);
-- 插入第三条数据
INSERT INTO vehicle_info (vid, frame_number, battery_type, total_mileage, battery_health)
VALUES ('mnopqr1234567892', 3, '三元电池', 300, 98);
-- 插入预警规则数据（示例数据，可根据实际情况修改）



-- 1. 三元电池 - 电压差报警（规则编号 1）
INSERT INTO alarm_rules (rule_number, alarm_rule, battery_type, alarm_level) VALUES
                                                                                 (1, '5<=(Mx - Mi)', '三元电池', 0),
                                                                                 (1, '3<=(Mx - Mi)<5', '三元电池', 1),
                                                                                 (1, '1<=(Mx - Mi)<3', '三元电池', 2),
                                                                                 (1, '0.6<=(Mx - Mi)<1', '三元电池', 3),
                                                                                 (1, '0.2<=(Mx - Mi)<0.6', '三元电池', 4),
                                                                                 (1, '(Mx - Mi)<0.2', '三元电池', -1);

-- 2. 铁锂电池 - 电压差报警（规则编号 1）
INSERT INTO alarm_rules (rule_number, alarm_rule, battery_type, alarm_level) VALUES
                                                                                 (1, '2<=(Mx - Mi)', '铁锂电池', 0),
                                                                                 (1, '1<=(Mx - Mi)<2', '铁锂电池', 1),
                                                                                 (1, '0.7<=(Mx - Mi)<1', '铁锂电池', 2),
                                                                                 (1, '0.4<=(Mx - Mi)<0.7', '铁锂电池', 3),
                                                                                 (1, '0.2<=(Mx - Mi)<0.4', '铁锂电池', 4),
                                                                                 (1, '(Mx - Mi)<0.2', '铁锂电池', -1);

-- 3. 三元电池 - 电流差报警（规则编号 2）
INSERT INTO alarm_rules (rule_number, alarm_rule, battery_type, alarm_level) VALUES
                                                                                 (2, '3<=(Ix - Ii)', '三元电池', 0),
                                                                                 (2, '1<=(Ix - Ii)<3', '三元电池', 1),
                                                                                 (2, '0.2<=(Ix - Ii)<1', '三元电池', 2),
                                                                                 (2, '(Ix - Ii)<0.2', '三元电池', -1);

-- 4. 铁锂电池 - 电流差报警（规则编号 2）
INSERT INTO alarm_rules (rule_number, alarm_rule, battery_type, alarm_level) VALUES
                                                                                 (2, '1<=(Ix - Ii)', '铁锂电池', 0),
                                                                                 (2, '0.5<=(Ix - Ii)<1', '铁锂电池', 1),
                                                                                 (2, '0.2<=(Ix - Ii)<0.5', '铁锂电池', 2),
                                                                                 (2, '(Ix - Ii)<0.2', '铁锂电池', -1);