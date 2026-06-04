# 专家排班管理接口文档

## 1. 模块说明

该模块用于专家维护自己的一对一咨询排班，提供以下能力：

- 新增排班
- 修改排班
- 删除排班
- 获取我的排班列表

接口统一前缀：`/api/expertSchedule`

返回结构统一使用项目中的 `HttpResult<T>`。

---

## 2. 业务规则

### 2.1 通用规则

- 需要登录后访问
- 只有角色为 `2` 的专家用户可以管理排班
- 专家只能管理自己的排班

### 2.2 新增排班

- 排班日期不能为空
- 时间段不能为空
- 最大预约人数必须大于 `0`
- 同一专家在同一天、同一时间段不能重复创建排班
- 创建成功后系统自动设置：
  - `expertId = 当前登录专家ID`
  - `reservedCount = 0`
  - `status = 0`
  - `isDelete = 0`

### 2.3 修改排班

- 排班 ID 不能为空
- 只能修改自己的排班
- 最大预约人数不能小于当前已预约人数
- 修改后的日期 + 时间段不能与自己其他排班重复
- 状态值仅允许：
  - `0`：可预约
  - `1`：已满
  - `2`：已关闭
- 当 `reservedCount >= maxReserveCount` 时，系统会自动将状态置为 `1=已满`

### 2.4 删除排班

- 只能删除自己的排班
- 已有预约的排班不能删除
- 删除采用逻辑删除，设置 `isDelete = 1`

---

## 3. 状态字段说明

### 3.1 排班状态 `status`

- `0`：可预约
- `1`：已满
- `2`：已关闭

---

## 4. 接口列表

### 4.1 新增排班

- **请求方式**：`POST`
- **请求路径**：`/api/expertSchedule/create`
- **是否登录**：是（专家）
- **请求体类型**：`application/json`

#### 请求体参数

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| scheduleDate | LocalDate | 是 | 排班日期 |
| timeSlot | String | 是 | 时间段 |
| maxReserveCount | Integer | 是 | 最大预约人数 |

#### 请求示例

```json
{
  "scheduleDate": "2026-05-30",
  "timeSlot": "14:00-15:00",
  "maxReserveCount": 5
}
```

#### 成功返回示例

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "expertId": 2,
    "scheduleDate": "2026-05-30",
    "timeSlot": "14:00-15:00",
    "maxReserveCount": 5,
    "reservedCount": 0,
    "status": 0,
    "createTime": "2026-05-24T16:40:00",
    "updateTime": "2026-05-24T16:40:00",
    "isDelete": 0
  }
}
```

#### 失败场景

- 未登录
- 当前用户不是专家
- 排班信息不能为空
- 排班日期不能为空
- 时间段不能为空
- 最大预约人数必须大于0
- 该时间段排班已存在

---

### 4.2 修改排班

- **请求方式**：`PUT`
- **请求路径**：`/api/expertSchedule/update`
- **是否登录**：是（专家）
- **请求体类型**：`application/json`

#### 请求体参数

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| id | Long | 是 | 排班 ID |
| scheduleDate | LocalDate | 是 | 排班日期 |
| timeSlot | String | 是 | 时间段 |
| maxReserveCount | Integer | 是 | 最大预约人数 |
| status | Integer | 否 | 排班状态：0可预约 1已满 2已关闭 |

#### 请求示例

```json
{
  "id": 1,
  "scheduleDate": "2026-05-30",
  "timeSlot": "15:00-16:00",
  "maxReserveCount": 6,
  "status": 0
}
```

#### 成功返回示例

```json
{
  "code": 200,
  "msg": "success",
  "data": "修改成功"
}
```

#### 失败场景

- 未登录
- 当前用户不是专家
- 排班ID不能为空
- 排班不存在
- 无权修改他人的排班
- 最大预约人数不能小于已预约人数
- 该时间段排班已存在
- 排班状态不合法

---

### 4.3 删除排班

- **请求方式**：`POST`
- **请求路径**：`/api/expertSchedule/delete/{scheduleId}`
- **是否登录**：是（专家）

#### 路径参数

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| scheduleId | Long | 是 | 排班 ID |

#### 成功返回示例

```json
{
  "code": 200,
  "msg": "success",
  "data": "删除成功"
}
```

#### 失败场景

- 未登录
- 当前用户不是专家
- 排班不存在
- 无权删除他人的排班
- 该排班已有预约，无法删除

---

### 4.4 获取我的排班列表

- **请求方式**：`GET`
- **请求路径**：`/api/expertSchedule/my`
- **是否登录**：是（专家）

#### 查询参数

| 参数名 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| page | long | 是 | 页码 |
| size | long | 是 | 每页数量 |

#### 请求示例

```text
/api/expertSchedule/my?page=1&size=10
```

#### 返回说明

返回 `IPage<ExpertScheduleDTO>` 分页结构，`records` 中每项为当前专家的排班记录。

#### 返回示例

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "expertId": 2,
        "scheduleDate": "2026-05-30",
        "timeSlot": "14:00-15:00",
        "maxReserveCount": 5,
        "reservedCount": 0,
        "status": 0,
        "createTime": "2026-05-24T16:40:00",
        "updateTime": "2026-05-24T16:40:00",
        "isDelete": 0
      }
    ],
    "total": 1,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

---

## 5. DTO 字段说明

### 5.1 ExpertScheduleDTO

| 字段名 | 类型 | 说明 |
| --- | --- | --- |
| id | Long | 排班 ID |
| expertId | Long | 专家用户 ID |
| scheduleDate | LocalDate | 排班日期 |
| timeSlot | String | 预约时间段 |
| maxReserveCount | Integer | 最大可预约人数 |
| reservedCount | Integer | 已预约人数 |
| status | Integer | 排班状态 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| isDelete | Integer | 逻辑删除标记 |

---

## 6. 与咨询预约模块的关系

该模块维护的数据表为 `t_expert_schedule`，会被咨询预约模块直接使用：

- 用户预约时会校验排班是否存在
- 用户预约成功后会增加 `reservedCount`
- 用户取消或专家拒绝预约时会回滚 `reservedCount`
- 已有预约的排班不能删除，避免破坏预约链路

---

## 7. 后续建议

建议下一步继续补充：

- 专家关闭排班接口
- 专家公开排班列表接口的文档
- 咨询预约接口与排班接口合并总文档
