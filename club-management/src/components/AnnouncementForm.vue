<template>
  <div class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3>{{ isEdit ? '编辑公告' : '发布公告' }}</h3>
        <button class="close-btn" @click="$emit('close')">&times;</button>
      </div>
      
      <form @submit.prevent="handleSubmit" class="announcement-form">
        <div class="form-group">
          <label>公告标题 *</label>
          <input 
            v-model="formData.title" 
            type="text" 
            placeholder="请输入公告标题"
            required
          />
        </div>
        
        <div class="form-row">
          <div class="form-group">
            <label>公告分类 *</label>
            <select v-model="formData.type" required>
              <option value="">请选择分类</option>
              <option value="通知公告">通知公告</option>
              <option value="活动预告">活动预告</option>
              <option value="系统公告">系统公告</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>发布范围 *</label>
            <select v-model="formData.targetType" required>
              <option value="all">全校</option>
              <option value="club">社团</option>
            </select>
          </div>
        </div>
        
        <div class="form-group">
          <label>是否置顶</label>
          <label class="checkbox-label">
            <input 
              v-model="formData.pinned" 
              type="checkbox"
            />
            <span>设为置顶公告</span>
          </label>
        </div>
        
        <div class="form-group">
          <label>公告内容 *</label>
          <textarea 
            v-model="formData.content" 
            placeholder="请输入公告详细内容"
            rows="8"
            required
          ></textarea>
        </div>
        
        <div class="form-actions">
          <button type="button" class="btn btn-outline" @click="$emit('close')">
            取消
          </button>
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '提交中...' : (isEdit ? '保存修改' : '发布') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  isEdit: {
    type: Boolean,
    default: false
  },
  initialData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['close', 'submit'])

const loading = ref(false)

// 表单数据
const formData = reactive({
  title: '',
  type: '',
  targetType: 'all',
  targetId: null,
  pinned: false,
  content: ''
})

// 初始化表单数据
watch(() => props.initialData, (newVal) => {
  if (props.isEdit && newVal) {
    Object.assign(formData, {
      title: newVal.title || '',
      type: newVal.type || '',
      targetType: newVal.targetType || 'all',
      targetId: newVal.targetId || null,
      pinned: newVal.pinned || false,
      content: newVal.content || ''
    })
  }
}, { immediate: true })

const handleSubmit = async () => {
  if (!formData.title || !formData.type || !formData.targetType || !formData.content) {
    alert('请填写所有必填项')
    return
  }
  
  loading.value = true
  
  try {
    emit('submit', { ...formData })
  } catch (error) {
    console.error('提交失败:', error)
    alert('提交失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleOverlayClick = () => {
  if (!loading.value) {
    emit('close')
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #333;
}

.announcement-form {
  padding: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: normal;
}

.checkbox-label input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3B82F6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background: #3B82F6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563EB;
}

.btn-outline {
  background: white;
  color: #6B7280;
  border: 1px solid #D1D5DB;
}

.btn-outline:hover {
  background: #F9FAFB;
  border-color: #9CA3AF;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 10px;
  }
}
</style>
