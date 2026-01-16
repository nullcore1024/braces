# Braces2 - 牙齿矫正计划管理应用

Braces2是一款基于Android平台的牙齿矫正计划管理应用，帮助用户跟踪和管理牙齿矫正过程中的各项指标和进度。

## 功能特点

### 1. 矫正计划管理
- 创建和管理个人矫正计划
- 设置矫正目标和时间周期
- 跟踪矫正进度

### 2. 每日记录跟踪
- 记录每日佩戴时间
- 跟踪矫正器调整情况
- 记录口腔健康状况

### 3. 可视化日历
- 直观的日历视图展示
- 标记重要的矫正里程碑
- 查看历史记录和趋势

### 4. 计划控制
- 灵活调整矫正计划
- 设置提醒和目标
- 生成统计报告

## 技术栈

### 开发语言
- **Kotlin** - 主要开发语言

### UI框架
- **Jetpack Compose** - 现代化的Android UI工具包

### 数据库
- **Room** - Android官方推荐的本地数据库解决方案

### 架构模式
- **MVVM (Model-View-ViewModel)** - 清晰的分层架构
- **Repository Pattern** - 数据访问抽象层

## 项目结构

```
braces2/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/braces2/
│   │   │   │   ├── components/       # 自定义Compose组件
│   │   │   │   │   ├── CalendarView.kt       # 日历视图组件
│   │   │   │   │   ├── CountSelector.kt      # 计数选择器组件
│   │   │   │   │   └── PlanControl.kt        # 计划控制组件
│   │   │   │   ├── data/             # 数据层
│   │   │   │   │   ├── AppDatabase.kt        # Room数据库定义
│   │   │   │   │   ├── AppRepository.kt      # 数据仓库
│   │   │   │   │   ├── CorrectionPlan.kt     # 矫正计划实体
│   │   │   │   │   ├── CorrectionPlanDao.kt  # 矫正计划DAO
│   │   │   │   │   ├── DailyRecord.kt        # 每日记录实体
│   │   │   │   │   ├── DailyRecordDao.kt     # 每日记录DAO
│   │   │   │   │   └── DateConverter.kt      # 日期转换器
│   │   │   │   ├── ui/               # UI层
│   │   │   │   │   └── theme/        # 主题定义
│   │   │   │   ├── BracesApplication.kt      # 应用入口
│   │   │   │   └── MainActivity.kt           # 主活动
│   │   │   └── res/                  # 资源文件
│   │   ├── androidTest/              # 仪器测试
│   │   └── test/                     # 单元测试
│   ├── build.gradle                  # 模块构建配置
│   └── proguard-rules.pro            # ProGuard规则
├── build.gradle                      # 项目构建配置
├── gradle.properties                 # Gradle属性
├── gradlew                           # Gradle包装器（Linux/Mac）
├── gradlew.bat                       # Gradle包装器（Windows）
└── settings.gradle                   # 项目设置
```

## 核心功能实现原理

### 1. 数据持久化

使用Room数据库实现数据持久化，主要包含两个实体：
- `CorrectionPlan` - 存储矫正计划的基本信息
- `DailyRecord` - 存储每日佩戴和调整记录

```kotlin
@Entity(tableName = "correction_plans")
data class CorrectionPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val targetHoursPerDay: Int
)

@Entity(tableName = "daily_records")
data class DailyRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val planId: Int,
    val date: LocalDate,
    val hoursWorn: Int,
    val adjustmentMade: Boolean,
    val notes: String
)
```

### 2. 自定义Compose组件

应用包含多个自定义Jetpack Compose组件：

#### CalendarView
- 实现了一个交互式日历视图
- 支持日期选择和标记
- 显示矫正记录状态

#### CountSelector
- 用于选择每日佩戴时间
- 提供增减按钮和直接输入功能
- 实时更新UI反馈

#### PlanControl
- 计划管理主界面
- 整合日历和计数功能
- 提供计划调整选项

### 3. 应用架构

采用MVVM架构模式：
- **Model** - Room实体和数据访问对象
- **View** - Jetpack Compose UI组件
- **ViewModel** - 业务逻辑和UI状态管理（通过MainActivity中的状态管理实现）

使用Repository模式封装数据访问：
```kotlin
class AppRepository(private val db: AppDatabase) {
    // 矫正计划相关操作
    suspend fun insertCorrectionPlan(plan: CorrectionPlan) = db.correctionPlanDao().insert(plan)
    suspend fun getAllCorrectionPlans() = db.correctionPlanDao().getAll()
    
    // 每日记录相关操作
    suspend fun insertDailyRecord(record: DailyRecord) = db.dailyRecordDao().insert(record)
    suspend fun getDailyRecordsByPlanId(planId: Int) = db.dailyRecordDao().getByPlanId(planId)
    suspend fun getDailyRecordByDate(planId: Int, date: LocalDate) = db.dailyRecordDao().getByDate(planId, date)
}
```

## 应用工作流程

1. **创建矫正计划** - 用户设置矫正目标、开始和结束日期
2. **每日记录** - 用户每天记录佩戴时间和调整情况
3. **进度跟踪** - 通过日历视图查看历史记录和进度
4. **计划调整** - 根据实际情况调整矫正计划
5. **统计分析** - 生成佩戴时间统计和趋势分析

## 构建和运行

### 环境要求
- Android Studio Flamingo或更高版本
- Kotlin 1.8.0或更高版本
- Android SDK 33或更高版本
- JDK 17或更高版本

### 构建步骤

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 同步Gradle依赖
4. 连接Android设备或启动模拟器
5. 点击运行按钮构建并安装应用

### 命令行构建

```bash
# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 运行单元测试
./gradlew test

# 运行仪器测试
./gradlew connectedAndroidTest
```

## 未来规划

1. 添加数据可视化图表，更直观展示矫正进度
2. 集成推送通知，提醒用户每日记录
3. 添加数据导出功能，支持CSV格式导出
4. 实现云同步功能，支持多设备数据同步
5. 添加口腔健康知识科普板块

## 贡献

欢迎提交Issue和Pull Request来帮助改进项目。

## 许可证

本项目采用MIT许可证，详见LICENSE文件。
