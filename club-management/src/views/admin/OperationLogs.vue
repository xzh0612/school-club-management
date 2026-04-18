<template>
  <div class="operation-logs">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>📝 操作日志</h2>
      <p>记录系统关键操作和用户行为</p>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="filters.module" placeholder="请选择模块" clearable>
            <el-option label="用户管理" value="user"></el-option>
            <el-option label="社团管理" value="club"></el-option>
            <el-option label="活动管理" value="activity"></el-option>
            <el-option label="公告管理" value="announcement"></el-option>
            <el-option label="审批管理" value="approval"></el-option>
            <el-option label="系统设置" value="system"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filters.operator" placeholder="操作人员" clearable>
            <el-option label="系统管理员" value="admin"></el-option>
            <el-option label="奚梓恒" value="xiziheng"></el-option>
            <el-option label="刘硕" value="liushuo"></el-option>
            <el-option label="马子健" value="mazijian"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="filters.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="searchLogs">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon bg-blue">📊</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">总操作数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-green">✅</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.success }}</div>
          <div class="stat-label">成功操作</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-yellow">⚠️</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.warning }}</div>
          <div class="stat-label">警告操作</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-red">❌</div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.error }}</div>
          <div class="stat-label">失败操作</div>
        </div>
      </div>
    </div>

    <!-- 日志表格 -->
    <div class="logs-table card">
      <el-table :data="logList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="operator" label="操作人员" width="120">
          <template #default="scope">
            <el-tag :type="getOperatorTagType(scope.row.operator)">
              {{ scope.row.operator }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="操作模块" width="120">
          <template #default="scope">
            <el-tag :type="getModuleTagType(scope.row.module)">
              {{ getModuleName(scope.row.module) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作类型" width="120">
          <template #default="scope">
            <el-tag :type="getActionTagType(scope.row.action)">
              {{ getActionName(scope.row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述"></el-table-column>
        <el-table-column prop="ip" label="IP地址" width="140"></el-table-column>
        <el-table-column prop="createTime" label="操作时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">
              {{ getStatusName(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="操作详情" width="600px">
      <div v-if="selectedLog" class="log-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="ID">{{ selectedLog.id }}</el-descriptions-item>
          <el-descriptions-item label="操作人员">{{ selectedLog.operator }}</el-descriptions-item>
          <el-descriptions-item label="操作模块">{{ getModuleName(selectedLog.module) }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ getActionName(selectedLog.action) }}</el-descriptions-item>
          <el-descriptions-item label="操作描述">{{ selectedLog.description }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ selectedLog.ip }}</el-descriptions-item>
          <el-descriptions-item label="用户代理">{{ selectedLog.userAgent }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ selectedLog.createTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(selectedLog.status)">
              {{ getStatusName(selectedLog.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 数据状态
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const logList = ref([])
const detailDialogVisible = ref(false)
const selectedLog = ref(null)

// 筛选条件
const filters = reactive({
  module: '',
  operator: '',
  dateRange: []
})

// 统计数据
const stats = reactive({
  total: 1247,
  success: 1189,
  warning: 32,
  error: 26
})

// 获取日志列表
const getLogList = async () => {
  loading.value = true
  try {
    // 模拟数据
    const mockData = [
      {
        id: 1001,
        operator: '系统管理员',
        module: 'user',
        action: 'create',
        description: '创建新用户：张三',
        ip: '192.168.1.100',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        createTime: '2026-03-21 14:30:25',
        status: 'success'
      },
      {
        id: 1002,
        operator: '奚梓恒',
        module: 'club',
        action: 'update',
        description: '更新计算机协会信息',
        ip: '192.168.1.101',
        userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)',
        createTime: '2026-03-21 14:25:18',
        status: 'success'
      },
      {
        id: 1003,
        operator: '刘硕',
        module: 'activity',
        action: 'delete',
        description: '删除篮球社活动：新生杯赛程公布',
        ip: '192.168.1.102',
        userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)',
        createTime: '2026-03-21 14:20:45',
        status: 'warning'
      },
      {
        id: 1004,
        operator: '马子健',
        module: 'announcement',
        action: 'create',
        description: '发布公告：关于2026年春季社团年审工作的通知',
        ip: '192.168.1.103',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        createTime: '2026-03-21 14:15:32',
        status: 'success'
      },
      {
        id: 1005,
        operator: '系统管理员',
        module: 'system',
        action: 'login',
        description: '系统管理员登录系统',
        ip: '192.168.1.100',
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        createTime: '2026-03-21 14:10:15',
        status: 'success'
      }
    ]

    logList.value = mockData
    total.value = mockData.length
  } catch (error) {
    ElMessage.error('获取日志列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索日志
const searchLogs = () => {
  ElMessage.success('搜索功能已触发')
  getLogList()
}

// 重置筛选条件
const resetFilters = () => {
  filters.module = ''
  filters.operator = ''
  filters.dateRange = []
  ElMessage.info('筛选条件已重置')
  getLogList()
}

// 查看详情
const viewDetail = (row) => {
  selectedLog.value = row
  detailDialogVisible.value = true
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  getLogList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  getLogList()
}

// 工具函数
const getOperatorTagType = (operator) => {
  const types = {
    '系统管理员': 'primary',
    '奚梓恒': 'success',
    '刘硕': 'warning',
    '马子健': 'danger'
  }
  return types[operator] || 'info'
}

const getModuleTagType = (module) => {
  const types = {
    'user': 'primary',
    'club': 'success',
    'activity': 'warning',
    'announcement': 'danger',
    'approval': 'info',
    'system': ''
  }
  return types[module] || 'info'
}

const getModuleName = (module) => {
  const names = {
    'user': '用户管理',
    'club': '社团管理',
    'activity': '活动管理',
    'announcement': '公告管理',
    'approval': '审批管理',
    'system': '系统设置'
  }
  return names[module] || module
}

const getActionTagType = (action) => {
  const types = {
    'create': 'success',
    'update': 'warning',
    'delete': 'danger',
    'login': 'primary',
    'logout': 'info',
    'view': 'info'
  }
  return types[action] || 'info'
}

const getActionName = (action) => {
  const names = {
    'create': '新增',
    'update': '修改',
    'delete': '删除',
    'login': '登录',
    'logout': '登出',
    'view': '查看'
  }
  return names[action] || action
}

const getStatusTagType = (status) => {
  const types = {
    'success': 'success',
    'warning': 'warning',
    'error': 'danger'
  }
  return types[status] || 'info'
}

const getStatusName = (status) => {
  const names = {
    'success': '成功',
    'warning': '警告',
    'error': '失败'
  }
  return names[status] || status
}

// 初始化
onMounted(() => {
  getLogList()
})
</script>

<style scoped>
.operation-logs {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #1F2937;
}

.page-header p {
  margin: 0;
  color: #6B7280;
  font-size: 14px;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.filter-section {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;
}

.bg-blue { background: #DBEAFE; color: #3B82F6; }
.bg-green { background: #D1FAE5; color: #10B981; }
.bg-yellow { background: #FEF3C7; color: #F59E0B; }
.bg-red { background: #FEE2E2; color: #EF4444; }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6B7280;
}

.logs-table {
  padding: 0;
}

.pagination {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
}

.log-detail {
  padding: 20px 0;
}
</style>