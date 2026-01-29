<template>
  <div class="app-container modern-layout">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <el-button class="back-btn" type="text" icon="ArrowLeft" size="large" plain @click="handleBack"></el-button>
          <el-tag type="primary" size="large">{{ appName }}:{{ appId }} </el-tag>
        </div>
        <div class="header-right">
          <div class="tab-navigation">
            <el-tabs v-model="active" class="modern-tabs">
              <el-tab-pane
                  v-for="item in tabs"
                  :key="item.key"
                  :name="item.key"
              >
                <template #label>
                  <div class="tab-label">
                    <svg-icon :icon-class="item.icon" class="tab-icon" />
                    <span>{{ item.label }}</span>
                  </div>
                </template>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <el-card class="content-card">
        <div class="content-body">
          <component :is="activeComponent" :app-id="appId"/>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import Resources from './auth/resources.vue'
import ClientAuth from './auth/ClientAuth.vue'
import RoleAuth from './auth/RoleAuth.vue'
import PublishPage from '@/views/api/Publish.vue'
import { useRoute, useRouter } from "vue-router";

const tabs = [
  { key: 'resources', label: '资源管理', component: Resources, icon: 'menu' },
  { key: 'client', label: '客户端授权', component: ClientAuth, icon: 'link' },
  { key: 'role', label: '角色授权', component: RoleAuth, icon: 'group' },
  { key: 'publish', label: '接口发布记录', component: PublishPage, icon: 'history' }
]

const active = ref('resources')
const route = useRoute();
const router = useRouter();
const appId: any = ref(undefined);
const appName: any = ref(undefined);

const activeComponent = computed(() => {
  return tabs.find(t => t.key === active.value)?.component
})

const activeTab = computed(() => {
  return tabs.find(t => t.key === active.value)
})

const handleBack = () => {
  router.back()
}

// 生命周期
onMounted(() => {
  const q = route.query.appId
  const w = route.query.appName
  appId.value = typeof q === 'string' ? q : appId.value
  appName.value = typeof w === 'string' ? w : appName.value
})
</script>

<style scoped lang="scss">
.modern-layout {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 页面头部样式 */
.page-header {
  background: white;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
    }

    .header-right {
      .el-tag {
        padding: 8px 16px;
        border-radius: 8px;
        font-weight: 500;
        background: linear-gradient(135deg, #f6f8fa 0%, #eaeef2 100%);
        border: 1px solid rgba(0, 0, 0, 0.08);
      }
    }
  }
}

/* 选项卡导航样式 */
.tab-navigation {

  .modern-tabs {
    :deep(.el-tabs__header) {
      margin: 0;

      .el-tabs__nav-wrap {
        &::after {
          display: none;
        }
      }

      .el-tabs__nav-scroll {
        overflow: visible;
      }

      .el-tabs__nav {
        border: none;
      }

      .el-tabs__item {
        padding: 20px 16px;
        font-weight: 500;
        color: #606266;
        transition: all 0.3s ease;
        border: none;
        position: relative;

        &:hover {
          color: #409eff;
          background: rgba(64, 158, 255, 0.05);
        }

        &.is-active {
          color: #409eff;
          font-weight: 600;

          &::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 80%;
            height: 3px;
            background: linear-gradient(90deg, #409eff 0%, #66b1ff 100%);
            border-radius: 2px 2px 0 0;
          }
        }

        .tab-label {
          display: flex;
          align-items: center;
          gap: 10px;

          .tab-icon {
            width: 20px;
            height: 20px;
          }
        }
      }

      .el-tabs__active-bar {
        display: none;
      }
    }
  }
}

/* 内容区域样式 */
.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;

  .content-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    border: 1px solid rgba(0, 0, 0, 0.05);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    overflow: hidden;

    .content-header {
      padding: 24px;
      border-bottom: 1px solid #f0f2f5;
      background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
      display: flex;
      justify-content: space-between;
      align-items: center;

      .content-title {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        display: flex;
        align-items: center;
        gap: 12px;

        .title-icon {
          width: 22px;
          height: 22px;
          color: #409eff;
        }
      }

      .content-actions {
        .el-button {
          padding: 10px 20px;
          border-radius: 8px;
          font-weight: 500;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
          }
        }
      }
    }

    .content-body {
      flex: 1;
      overflow: auto;
      background: white;
      height: calc(100vh - 260px);

      :deep(.app-container) {
        margin: 0;
        height: auto;
        padding: 0;
        background: transparent;
      }

      :deep(.common-card) {
        border: none;
        box-shadow: none;
        margin-bottom: 0;

        &.query-box {
          margin-bottom: 20px;
          border: 1px solid #f0f2f5;
          border-radius: 8px;
          overflow: hidden;
        }
      }
    }
  }
}

.page-header {
  animation-delay: 0.1s;
}

.tab-navigation {
  animation-delay: 0.2s;
}

.content-wrapper {
  animation-delay: 0.3s;
}
</style>
