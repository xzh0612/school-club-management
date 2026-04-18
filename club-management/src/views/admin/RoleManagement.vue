<template>
  <div class="role-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>🔐 权限配置</h2>
      <p>管理系统角色和权限分配</p>
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
          <div class="stat-label">权限项数</div>
        </div>
      </div>
    </div>

    <!-- 角色管理 -->
    <div class="roles-section card">
      <div class="section-header">
        <h3>角色列表</h3>
        <el-button type="primary" @click="showRoleDialog()">新建角色</el-button>
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
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="toggleRoleStatus(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" @click="editRole(scope.row)">编辑</el-button>
            <el-button size="small" @click="configurePermissions(scope.row)">权限</el-button>
            <el-button size="small" type="danger" @click="deleteRole(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 权限树 -->
    <div class="permissions-section card">
      <h3>权限体系</h3>
      <div class="permission-tree">
        <el-tree
          :data="permissionTree"
          show-checkbox
          node-key="id"
          :default-expanded-keys="expandedKeys"
          :default-checked-keys="checkedKeys"
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

    <!-- 角色对话框 -->
    <el-dialog v-model="roleDialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="currentRole" label-width="100px">
        <el-form-item label="角色名称">
          <el-input v-model="currentRole.name" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="currentRole.code" placeholder="请输入角色编码"></el-input>
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input 
            v-model="currentRole.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入角色描述"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">保存</el-button>
      </template>
    </el-dialog>

    <!-- 权限配置对话框 -->
    <el-dialog v-model="permissionDialogVisible" :title="`${currentRole.name} - 权限配置`" width="600px">
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :default-expanded-keys="expandedKeys"
        :default-checked-keys="rolePermissions[currentRole.id] || []"
        :props="defaultProps"
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRolePermissions">保存权限</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 数据状态
const loading = ref(false)
const roleDialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const roleList = ref([])
const currentRole = ref({})
const rolePermissions = ref({})

// 统计数据
const roleStats = reactive({
  total: 6,
  active: 5,
  disabled: 1,
  permissions: 24
})

// 权限树数据
const permissionTree = ref([
  {
    id: 1,
    label: '系统管理',
    code: 'SYSTEM_MANAGE',
    children: [
      { id: 11, label: '用户管理', code: 'USER_MANAGE' },
      { id: 12, label: '角色管理', code: 'ROLE_MANAGE' },
      { id: 13, label: '权限配置', code: 'PERMISSION_CONFIG' },
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

// 计算属性
const dialogTitle = computed(() => {
  return currentRole.value.id ? '编辑角色' : '新建角色'
})

// 获取角色列表
const getRoleList = async () => {
  loading.value = true
  try {
    // 模拟数据
    roleList.value = [
      { id: 1, name: '系统管理员', code: 'ADMIN', description: '拥有系统最高权限', userCount: 1, status: 1, createTime: '2026-01-01 00:00:00' },
      { id: 2, name: '社团管理员', code: 'CLUB_ADMIN', description: '负责社团日常管理', userCount: 3, status: 1, createTime: '2026-01-02 00:00:00' },
      { id: 3, name: '指导老师', code: 'TEACHER', description: '社团指导老师', userCount: 5, status: 1, createTime: '2026-01-03 00:00:00' },
      { id: 4, name: '社团负责人', code: 'CLUB_LEADER', description: '社团负责人', userCount: 12, status: 1, createTime: '2026-01-04 00:00:00' },
      { id: 5, name: '普通学生', code: 'STUDENT', description: '普通学生用户', userCount: 156, status: 1, createTime: '2026-01-05 00:00:00' },
      { id: 6, name: '已禁用角色', code: 'DISABLED_ROLE', description: '测试禁用角色', userCount: 0, status: 0, createTime: '2026-01-06 00:00:00' }
    ]
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

// 显示角色对话框
const showRoleDialog = (role = {}) => {
  currentRole.value = { ...role }
  roleDialogVisible.value = true
}

// 编辑角色
const editRole = (role) => {
  showRoleDialog(role)
}

// 保存角色
const saveRole = () => {
  ElMessage.success('角色保存成功')
  roleDialogVisible.value = false
  getRoleList()
}

// 切换角色状态
const toggleRoleStatus = (role) => {
  ElMessage.success(`角色${role.status === 1 ? '启用' : '禁用'}成功`)
}

// 删除角色
const deleteRole = async (role) => {
  try {
    await ElMessageBox.confirm(`确定要删除角色 "${role.name}" 吗？`, '提示', {
      type: 'warning'
    })
    ElMessage.success('角色删除成功')
    getRoleList()
  } catch (error) {
    // 用户取消删除
  }
}

// 配置权限
const configurePermissions = (role) => {
  currentRole.value = role
  permissionDialogVisible.value = true
}

// 保存角色权限
const saveRolePermissions = () => {
  const checkedNodes = permissionTreeRef.value.getCheckedNodes()
  const halfCheckedNodes = permissionTreeRef.value.getHalfCheckedNodes()
  rolePermissions.value[currentRole.value.id] = [
    ...checkedNodes.map(node => node.id),
    ...halfCheckedNodes.map(node => node.id)
  ]
  ElMessage.success('权限配置保存成功')
  permissionDialogVisible.value = false
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