<template>
  <div>
    <div class="search-bar">
      <input class="search-input" placeholder="搜索社团名称、类型..." v-model="keyword" />
      <select class="filter-select"><option>全部类型</option><option>学术科技</option><option>文化艺术</option><option>体育竞技</option><option>公益志愿</option></select>
      <button class="btn btn-primary">🔍 搜索</button>
    </div>
    <div class="club-grid">
      <div class="club-card" v-for="c in clubs" :key="c.id">
        <div class="club-cover" :style="{ background: c.gradient }">{{ c.icon }}</div>
        <div class="club-info">
          <h4>{{ c.name }}</h4>
          <p class="club-desc">{{ c.desc }}</p>
          <div class="club-meta">
            <div><span :class="['tag', c.typeClass]">{{ c.type }}</span> <span class="tag tag-orange">⭐{{c.stars}}星</span><br><span class="club-members">{{c.members}}位社员</span></div>
            <button :class="['btn', 'btn-sm', c.joined ? 'btn-outline' : 'btn-primary']">{{ c.joined ? '已加入 ✓' : '申请加入' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const keyword = ref('')
const clubs = ref([
  { id:1, name:'计算机协会', icon:'💻', desc:'致力于推广计算机技术，组织编程竞赛、技术分享', type:'学术科技', typeClass:'tag-blue', stars:5, members:156, gradient:'linear-gradient(135deg,#DBEAFE,#BFDBFE)', joined:false },
  { id:2, name:'摄影社', icon:'📷', desc:'用镜头记录美好瞬间，定期举办摄影展', type:'文化艺术', typeClass:'tag-purple', stars:4, members:89, gradient:'linear-gradient(135deg,#FCE7F3,#FBCFE8)', joined:false },
  { id:3, name:'篮球社', icon:'🏀', desc:'热血篮球，组织校内联赛和校际友谊赛', type:'体育竞技', typeClass:'tag-orange', stars:4, members:203, gradient:'linear-gradient(135deg,#FEF3C7,#FDE68A)', joined:true },
  { id:4, name:'文学社', icon:'📖', desc:'以文会友，品味经典，开展读书会和创作交流', type:'文化艺术', typeClass:'tag-purple', stars:5, members:127, gradient:'linear-gradient(135deg,#D1FAE5,#A7F3D0)', joined:false },
  { id:5, name:'辩论社', icon:'🎤', desc:'培养逻辑思维和表达能力，参加各级辩论赛事', type:'学术科技', typeClass:'tag-blue', stars:4, members:68, gradient:'linear-gradient(135deg,#EDE9FE,#DDD6FE)', joined:false },
  { id:6, name:'志愿者协会', icon:'❤️', desc:'传递温暖与关爱，组织社区服务和公益活动', type:'公益志愿', typeClass:'tag-green', stars:5, members:312, gradient:'linear-gradient(135deg,#FEE2E2,#FECACA)', joined:false },
])
</script>

<style scoped>
.club-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:16px; }
.club-card { background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 1px 3px rgba(0,0,0,0.06); transition:transform 0.2s; }
.club-card:hover { transform:translateY(-2px); box-shadow:0 4px 12px rgba(0,0,0,0.1); }
.club-cover { height:120px; display:flex; align-items:center; justify-content:center; font-size:40px; }
.club-info { padding:16px; }
.club-info h4 { font-size:15px; margin-bottom:6px; }
.club-desc { font-size:12px; color:#6B7280; line-height:1.6; margin-bottom:12px; }
.club-meta { display:flex; justify-content:space-between; align-items:center; }
.club-members { font-size:11px; color:#9CA3AF; }
</style>