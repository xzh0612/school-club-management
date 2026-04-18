<template>
  <div>
    <div class="card"><div class="card-header"><h3>已加入的社团（{{clubs.length}}个）</h3></div>
      <div class="club-row" v-for="c in clubs" :key="c.id">
        <div class="club-cover" :style="{background:c.gradient}">{{c.icon}}</div>
        <div class="club-detail">
          <div style="display:flex;justify-content:space-between;align-items:center"><h4>{{c.name}}</h4><span class="tag tag-orange">⭐{{c.stars}}星</span></div>
          <p>{{c.desc}}</p>
          <div class="club-tags"><span :class="['tag',c.roleClass]">{{c.role}}</span><span class="meta-text">加入时间：{{c.joinDate}}</span><span class="meta-sep">|</span><span class="meta-text">{{c.members}}位社员</span></div>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header"><h3>最近活动</h3></div>
      <table class="data-table">
        <thead><tr><th>活动名称</th><th>社团</th><th>时间</th><th>地点</th><th>状态</th><th>操作</th></tr></thead>
        <tbody><tr v-for="a in activities" :key="a.id"><td style="font-weight:500">{{a.name}}</td><td>{{a.club}}</td><td>{{a.time}}</td><td>{{a.location}}</td><td><span :class="['tag',a.statusClass]">{{a.status}}</span></td><td><button class="btn btn-outline btn-sm">查看</button></td></tr></tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const clubs = ref([
  { id:1, name:'篮球社', icon:'🏀', desc:'热血篮球，激情飞扬！组织校内联赛和校际友谊赛', stars:4, role:'社员', roleClass:'tag-gray', joinDate:'2025-09-15', members:203, gradient:'linear-gradient(135deg,#FEF3C7,#FDE68A)' },
  { id:2, name:'计算机协会', icon:'💻', desc:'致力于推广计算机技术，组织编程竞赛、技术分享', stars:5, role:'核心成员', roleClass:'tag-blue', joinDate:'2025-09-10', members:156, gradient:'linear-gradient(135deg,#DBEAFE,#BFDBFE)' },
])
const activities = ref([
  { id:1, name:'新生杯篮球赛', club:'篮球社', time:'2026-03-20 14:00', location:'体育馆', status:'即将开始', statusClass:'tag-orange' },
  { id:2, name:'编程马拉松大赛', club:'计算机协会', time:'2026-03-25 09:00', location:'信息楼A301', status:'已报名', statusClass:'tag-green' },
  { id:3, name:'技术分享会 - AI专题', club:'计算机协会', time:'2026-03-12 19:00', location:'信息楼B205', status:'已结束', statusClass:'tag-gray' },
])
</script>

<style scoped>
.club-row { display:flex; border:1px solid #F3F4F6; border-radius:12px; overflow:hidden; margin-bottom:12px; }
.club-row:last-child { margin-bottom:0; }
.club-cover { width:120px; display:flex; align-items:center; justify-content:center; font-size:36px; flex-shrink:0; }
.club-detail { padding:16px; flex:1; }
.club-detail h4 { font-size:15px; margin-bottom:4px; }
.club-detail p { font-size:12px; color:#6B7280; margin-bottom:8px; }
.club-tags { display:flex; gap:8px; align-items:center; }
.meta-text { font-size:12px; color:#9CA3AF; }
.meta-sep { font-size:12px; color:#D1D5DB; }
</style>