<template>
  <div class="role-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>🔐 角色说明</h2>
      <p>查看系统内置角色、页面入口和当前用户分布</p>
    </div>

    <!-- 角色统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon bg-blue">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ roleStats.total }}</div>
          <div class="stat-label">角色总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-green">👤</div>
        <div class="stat-info">
          <div class="stat-value">{{ roleStats.active }}</div>
          <div class="stat-label">启用角色</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-yellow">🔒</div>
        <div class="stat-info">
          <div class="stat-value">{{ roleStats.disabled }}</div>
          <div class="stat-label">禁用角色</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon bg-purple">🔑</div>
        <div class="stat-info">
          <div class="stat-value">{{ roleStats.permissions }}</div>
          <div class="stat-label">页面入口数</div>
        </div>
      </div>
    </div>

    <!-- 角色说明 -->
    <div class="roles-section card">
      <div class="section-header">
        <h3>内置角色</h3>
      </div>

      <el-table :data="roleList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="角色名称" width="150"></el-table-column>
        <el-table-column prop="code" label="角色编码" width="150">
          <template #default="scope">
            <el-tag>{{ scope.row.code }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column prop="userCount" label="用户数" width="100">
          <template #default="scope">
            <span class="user-count">{{ scope.row.userCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag type="success">{{ scope.row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
      </el-table>
    </div>

    <!-- 入口说明 -->
    <div class="permissions-section card">
      <h3>页面入口说明</h3>
      <div class="permission-tree">
        <el-tree
          :data="permissionTree"
          node-key="id"
          :default-expanded-keys="expandedKeys"
          :props="defaultProps"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span class="node-label">{{ data.label }}</span>
              <span class="node-code">{{ data.code }}</span>
            </span>
          </template>
        </el-tree>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList } from '../../api/user'

// 数据状态
const loading = ref(false)
const roleList = ref([])

// 统计数据
const roleStats = reactive({
  total: 4,
  active: 4,
  disabled: 0,
  permissions: 0
})

// 权限树数据
const permissionTree = ref([
  {
    id: 1,
    label: '系统管理',
    code: 'SYSTEM_MANAGE',
    children: [
      { id: 11, label: '用户管理', code: 'USER_MANAGE' },
      { id: 12, label: '角色说明', code: 'ROLE_OVERVIEW' },
      { id: 13, label: '账号状态', code: 'ACCOUNT_STATUS' },
      { id: 14, label: '操作日志', code: 'OPERATION_LOG' }
    ]
  },
  {
    id: 2,
    label: '社团管理',
    code: 'CLUB_MANAGE',
    children: [
      { id: 21, label: '社团管理', code: 'CLUB_LIST' },
      { id: 22, label: '审批中心', code: 'APPROVAL_CENTER' },
      { id: 23, label: '招新管理', code: 'RECRUITMENT_MANAGE' }
    ]
  },
  {
    id: 3,
    label: '活动管理',
    code: 'ACTIVITY_MANAGE',
    children: [
      { id: 31, label: '活动管理', code: 'ACTIVITY_LIST' },
      { id: 32, label: '活动审批', code: 'ACTIVITY_APPROVAL' }
    ]
  },
  {
    id: 4,
    label: '内容管理',
    code: 'CONTENT_MANAGE',
    children: [
      { id: 41, label: '公告管理', code: 'ANNOUNCEMENT_MANAGE' },
      { id: 42, label: '数据统计', code: 'STATISTICS_VIEW' }
    ]
  }
])

const defaultProps = {
  children: 'children',
  label: 'label'
}

const expandedKeys = ref([1, 2, 3, 4])
const checkedKeys = ref([])

const countPermissionNodes = (nodes) => nodes.reduce((total, item) => {
  return total + 1 + (item.children ? countPermissionNodes(item.children) : 0)
}, 0)

// 获取角色列表
const getRoleList = async () => {
  loading.value = true
  try {
    const roles = [
      { id: 1, role: 'admin', name: '系统管理员', code: 'ADMIN', description: '维护平台账号、全校社团和全局配置' },
      { id: 2, role: 'teacher', name: '指导老师', code: 'TEACHER', description: '审核和监管所指导社团' },
      { id: 3, role: 'club_leader', name: '社团负责人', code: 'CLUB_LEADER', description: '管理本社团成员、活动、招新和公告' },
      { id: 4, role: 'student', name: '普通学生', code: 'STUDENT', description: '浏览社团、报名活动、提交入社申请' }
    ]
    const counts = await Promise.all(roles.map(item => getUserList(1, 1, item.role).then(res => res.data.total || 0).catch(() => 0)))
    roleList.value = roles.map((item, index) => ({
      ...item,
      userCount: counts[index],
      status: 1,
      createTime: '系统内置'
    }))
    roleStats.permissions = countPermissionNodes(permissionTree.value)
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  getRoleList()
})
</script>

<style scoped>
.role-management {
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
.bg-purple { background: #EDE9FE; color: #8B5CF6; }

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

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #1F2937;
}

.user-count {
  font-weight: 500;
  color: #3B82F6;
}

.permission-tree {
  padding: 20px;
  border: 1px solid #E5E7EB;
  border-radius: 8px;
  background: #F9FAFB;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.node-label {
  font-weight: 500;
}

.node-code {
  font-family: monospace;
  font-size: 12px;
  color: #6B7280;
  background: #E5E7EB;
  padding: 2px 6px;
  border-radius: 4px;
}
</style>
