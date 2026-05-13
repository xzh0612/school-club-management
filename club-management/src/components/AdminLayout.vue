<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-emoji">🏫</span>
        <span>社团管理系统</span>
      </div>
      <nav>
        <template v-for="group in visibleNavGroups" :key="group.name">
          <div class="nav-group">{{ group.name }}</div>
          <router-link
            v-for="item in group.items"
            :key="item.path"
            :to="item.path"
            :class="{ active: isActive(item.path) }"
          >
            <span class="nav-icon">{{ item.icon }}</span> {{ item.label }}
          </router-link>
        </template>
      </nav>
    </aside>

    <!-- Main -->
    <div class="main-area">
      <!-- Topbar -->
      <header class="topbar">
        <div class="breadcrumb">{{ currentTitle }}</div>
        <div class="topbar-right">
          <span class="topbar-icon">🔍</span>
          <span class="topbar-icon badge">🔔</span>
          <div class="avatar avatar-lg" :style="{ background: user.color }">
            {{ user.avatar }}
          </div>
          <span class="user-name">{{ user.name }}</span>
          <button class="btn btn-outline btn-sm" @click="handleLogout">退出</button>
        </div>
      </header>

      <!-- Content -->
      <main class="content-area">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const user = computed(() => JSON.parse(sessionStorage.getItem('user') || '{}'))
const currentRole = computed(() => user.value.role || '')

const currentTitle = computed(() => route.meta.title || '')

const navGroups = [
  {
    name: '概览',
    items: [
      { path: '/admin/dashboard', icon: '📊', label: '首页仪表盘', roles: ['admin', 'club_leader', 'teacher'] }
    ]
  },
  {
    name: '系统管理',
    items: [
      { path: '/admin/users', icon: '👤', label: '用户管理', roles: ['admin'] },
      { path: '/admin/roles', icon: '🔐', label: '角色说明', roles: ['admin'] },
      { path: '/admin/logs', icon: '📝', label: '操作日志', roles: ['admin'] }
    ]
  },
  {
    name: '审批管理',
    items: [
      { path: '/admin/approval', icon: '✅', label: '审批中心', roles: ['admin', 'teacher'] },
      { path: '/admin/recruitment', icon: '👥', label: '招新管理', roles: ['club_leader'] }
    ]
  },
  {
    name: '社团管理',
    items: [
      { path: '/admin/clubs', icon: '🏛️', label: '社团管理', roles: ['admin', 'club_leader', 'teacher'] },
      { path: '/admin/activities', icon: '🎉', label: '活动管理', roles: ['club_leader'] }
    ]
  },
  {
    name: '其他',
    items: [
      { path: '/admin/announcements', icon: '📢', label: '公告资讯', roles: ['admin', 'club_leader'] },
      { path: '/admin/statistics', icon: '⚙️', label: '数据统计', roles: ['admin', 'teacher'] }
    ]
  }
]

const visibleNavGroups = computed(() => {
  return navGroups
    .map(group => ({
      ...group,
      items: group.items.filter(item => item.roles.includes(currentRole.value))
    }))
    .filter(group => group.items.length > 0)
})

function isActive(path) {
  return route.path === path
}

function handleLogout() {
  sessionStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}
.sidebar {
  position: fixed;
  left: 0; top: 0;
  width: 220px;
  height: 100vh;
  background: linear-gradient(180deg, #1E3A5F 0%, #1E293B 100%);
  color: #fff;
  z-index: 100;
  overflow-y: auto;
}
.sidebar-logo {
  padding: 24px 20px;
  font-size: 16px;
  font-weight: 700;
  border-bottom: 1px solid rgba(255,255,255,0.1);
  display: flex;
  align-items: center;
  gap: 10px;
}
.logo-emoji { font-size: 24px; }
.sidebar nav { padding: 12px 0; }
.nav-group {
  padding: 8px 20px 4px;
  font-size: 11px;
  color: rgba(255,255,255,0.4);
  letter-spacing: 1px;
}
.sidebar a {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  color: rgba(255,255,255,0.7);
  font-size: 13px;
  transition: all 0.2s;
  cursor: pointer;
}
.sidebar a:hover { background: rgba(255,255,255,0.08); color: #fff; }
.sidebar a.active {
  background: rgba(59,130,246,0.3);
  color: #fff;
  border-right: 3px solid #3B82F6;
}
.nav-icon { width: 20px; text-align: center; font-size: 15px; }

.main-area {
  margin-left: 220px;
  flex: 1;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.topbar {
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #E5E7EB;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  position: sticky;
  top: 0;
  z-index: 50;
}
.breadcrumb { font-size: 14px; color: #6B7280; }
.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.topbar-icon { font-size: 18px; cursor: pointer; }
.badge { position: relative; }
.badge::after {
  content: '';
  position: absolute;
  top: -2px; right: -4px;
  width: 8px; height: 8px;
  background: #EF4444;
  border-radius: 50%;
}
.user-name { font-size: 13px; }
.content-area {
  padding: 24px 28px;
  flex: 1;
}
</style>
