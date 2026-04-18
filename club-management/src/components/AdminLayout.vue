<template>
  <div class="admin-layout">
    <!-- Sidebar -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-emoji">🏫</span>
        <span>社团管理系统</span>
      </div>
      <nav>
        <div class="nav-group">概览</div>
        <router-link to="/admin/dashboard" :class="{ active: isActive('/admin/dashboard') }">
          <span class="nav-icon">📊</span> 首页仪表盘
        </router-link>

        <div class="nav-group">系统管理</div>
        <router-link to="/admin/users" :class="{ active: isActive('/admin/users') }">
          <span class="nav-icon">👤</span> 用户管理
        </router-link>
        <router-link to="/admin/roles" :class="{ active: isActive('/admin/roles') }">
          <span class="nav-icon">🔐</span> 权限配置
        </router-link>
        <router-link to="/admin/logs" :class="{ active: isActive('/admin/logs') }">
          <span class="nav-icon">📝</span> 操作日志
        </router-link>

        <div class="nav-group">审批管理</div>
        <router-link to="/admin/approval" :class="{ active: isActive('/admin/approval') }">
          <span class="nav-icon">✅</span> 审批中心
        </router-link>
        <router-link to="/admin/recruitment" :class="{ active: isActive('/admin/recruitment') }">
          <span class="nav-icon">👥</span> 招新管理
        </router-link>

        <div class="nav-group">社团管理</div>
        <router-link to="/admin/clubs" :class="{ active: isActive('/admin/clubs') }">
          <span class="nav-icon">🏛️</span> 社团管理
        </router-link>
        <router-link to="/admin/activities" :class="{ active: isActive('/admin/activities') }">
          <span class="nav-icon">🎉</span> 活动管理
        </router-link>

        <div class="nav-group">其他</div>
        <router-link to="/admin/announcements" :class="{ active: isActive('/admin/announcements') }">
          <span class="nav-icon">📢</span> 公告资讯
        </router-link>
        <router-link to="/admin/statistics" :class="{ active: isActive('/admin/statistics') }">
          <span class="nav-icon">⚙️</span> 数据统计
        </router-link>
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
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))

const currentTitle = computed(() => route.meta.title || '')

function isActive(path) {
  return route.path === path
}

function handleLogout() {
  localStorage.removeItem('user')
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
