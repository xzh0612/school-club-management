<template>
  <div class="student-layout">
    <!-- Top Header -->
    <header class="student-header">
      <div class="header-logo">🏫 社团管理系统</div>
      <div class="header-right">
        <span class="header-icon">🔍</span>
        <span class="header-icon badge">🔔</span>
        <div class="avatar avatar-lg" :style="{ background: user.color }">{{ user.avatar }}</div>
        <span>{{ user.name }}</span>
        <button class="btn btn-outline btn-sm" @click="handleLogout">退出</button>
      </div>
    </header>

    <!-- Banner -->
    <div class="banner">
      <h1>{{ currentTitle }}</h1>
    </div>

    <!-- Nav -->
    <div class="nav-wrapper">
      <div class="nav-bar">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          :class="{ active: route.path === item.path }"
        >
          {{ item.icon }} {{ item.label }}
        </router-link>
      </div>
    </div>

    <!-- Content -->
    <main class="content-wrapper">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const user = computed(() => JSON.parse(localStorage.getItem('user') || '{}'))

const currentTitle = computed(() => route.meta.title || '社团广场')

const navItems = [
  { path: '/student/square', icon: '🏠', label: '全部社团' },
  { path: '/student/my-clubs', icon: '📋', label: '我的社团' },
  { path: '/student/activities', icon: '🎉', label: '活动中心' },
  { path: '/student/announcements', icon: '📢', label: '公告' },
  { path: '/student/profile', icon: '👤', label: '个人中心' },
]

function handleLogout() {
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.student-layout { min-height: 100vh; background: #F0F2F5; }
.student-header {
  height: 56px; background: #fff;
  border-bottom: 1px solid #E5E7EB;
  display: flex; align-items: center; justify-content: space-between;
  padding: 0 28px; position: sticky; top: 0; z-index: 50;
}
.header-logo { font-size: 16px; font-weight: 700; display: flex; align-items: center; gap: 8px; }
.header-right { display: flex; align-items: center; gap: 16px; }
.header-icon { font-size: 18px; cursor: pointer; }
.badge { position: relative; }
.badge::after {
  content: ''; position: absolute; top: -2px; right: -4px;
  width: 8px; height: 8px; background: #EF4444; border-radius: 50%;
}
.banner {
  height: 120px;
  background: linear-gradient(135deg, #3B82F6, #8B5CF6);
  display: flex; align-items: center; justify-content: center; color: #fff;
}
.banner h1 { font-size: 24px; font-weight: 600; }
.nav-wrapper {
  max-width: 1100px; margin: -30px auto 24px;
  padding: 0 28px; position: relative;
}
.nav-bar {
  display: flex; gap: 8px;
  background: #fff; border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.nav-bar a {
  padding: 8px 20px; border-radius: 8px;
  font-size: 13px; color: #6B7280;
  font-weight: 500; transition: all 0.2s;
}
.nav-bar a:hover { background: #F3F4F6; color: #1F2937; }
.nav-bar a.active { background: #3B82F6; color: #fff; }
.content-wrapper {
  max-width: 1100px; margin: 0 auto 24px; padding: 0 28px;
}
</style>
