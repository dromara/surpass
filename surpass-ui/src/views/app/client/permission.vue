<template>
  <div class="app-container modern-layout">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <el-button class="back-btn" type="text" icon="ArrowLeft" size="large" plain @click="handleBack"></el-button>
          <el-tag type="primary" size="large">{{ clientName }}:{{ clientId }} </el-tag>
        </div>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-wrapper">
      <el-card class="content-card">
        <div class="content-body">
          <!-- 左侧树形结构 -->
          <el-card class="tree-card">
            <div class="tree-container">
              <el-tree
                  node-key="id"
                  :data="dataOptions"
                  :props="defaultProps"
                  :expand-on-click-node="false"
                  :filter-node-method="filterNode"
                  ref="tree"
                  :default-expanded-keys="treeData"
                  @node-click="handleNodeClick"
                  highlight-current
                  class="modern-tree"
              >
                <template #default="{ node, data }">
                  <span class="tree-node">
                    <!-- 资源图标 -->
                    <svg-icon :icon-class="getResourceIcon(data.classify, data.resStyle)" class="resource-icon"/>

                    <!-- 资源名称 -->
                    <span class="tree-label">
                      <el-tooltip
                          :content="node.label"
                          placement="top"
                          :disabled="node.label.length <= 15"
                      >
                        <span>{{ node.label.length > 15 ? node.label.slice(0, 15) + '...' : node.label }}</span>
                      </el-tooltip>
                    </span>

                    <!-- 资源类型标签 -->
                    <el-tag
                        size="small"
                        :type="resourceTagType(data.classify)"
                        class="tree-tag"
                        effect="light"
                    >
                      {{ resourceLabel(data.classify) }}
                    </el-tag>
                  </span>
                </template>
              </el-tree>

              <!-- 空状态 -->
              <div v-if="!dataOptions || dataOptions.length === 0" class="tree-empty">
                <svg-icon icon-class="empty" class="empty-icon"/>
                <p>暂无资源数据</p>
              </div>
            </div>
          </el-card>

          <!-- 右侧详情卡片 -->
          <el-card class="list-card">
            <template #header>
              <div class="detail-header">
                <span class="detail-title">
                  <svg-icon icon-class="document" class="title-icon"/>
                  资源详情
                </span>
              </div>
            </template>

            <!-- 未选择资源时的提示 -->
            <div v-if="!selectedResource" class="detail-empty">
              <svg-icon icon-class="select" class="empty-icon"/>
              <p>请在左侧选择一个资源查看详情</p>
            </div>

            <!-- 资源详情内容 -->
            <div v-else class="detail-content">
              <!-- 基本信息 -->
              <div class="detail-section">
                <div class="section-title">
                  <svg-icon icon-class="info" class="section-icon"/>
                  基本信息
                </div>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="资源名称">
                    {{ selectedResource.name || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="资源类型">
                    <el-tag :type="resourceTagType(selectedResource.classify)" size="small">
                      {{ resourceLabel(selectedResource.classify) }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="资源ID">
                    {{ selectedResource.id || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="来源应用">
                    <el-tag type="success" size="small">{{ selectedResource.belongApp || clientName }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="应用上下文">
                    {{ selectedResource.contextPath || '-' }}
                  </el-descriptions-item>
<!--                  <el-descriptions-item label="资源路径">
                    {{ selectedResource.path || '-' }}
                  </el-descriptions-item>-->
                  <el-descriptions-item label="父级资源">
                    {{ selectedResource.parentName || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="描述" :span="2">
                    {{ selectedResource.description || '暂无描述' }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>

              <!-- API特有信息 -->
              <div v-if="isApiResource" class="detail-section">
                <div class="section-title">
                  <svg-icon icon-class="api" class="section-icon"/>
                  API接口信息
                </div>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="请求方法">
                    <el-tag
                        :type="getMethodTagType(selectedResource.method)"
                        size="small"
                    >
                      {{ selectedResource.method || 'GET' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="接口地址">
                    <el-text type="primary" style="font-family: monospace;">
                      {{ selectedResource.url || selectedResource.path || '-' }}
                    </el-text>
                  </el-descriptions-item>
                  <el-descriptions-item label="所属数据源" v-if="selectedResource.classify === 'openApi'">
                    {{ selectedResource.datasourceName || '-' }}
                  </el-descriptions-item>
<!--                  <el-descriptions-item label="认证方式">
                    {{ selectedResource.authType || '默认认证' }}
                  </el-descriptions-item>-->
                </el-descriptions>

                <!-- 请求参数 -->
                <div class="params-section">
                  <div class="params-title">
                    <el-icon><Upload /></el-icon>
                    <span>请求参数</span>
                  </div>
                  <el-table
                      :data="parsedParamDefinition"
                      border
                      stripe
                      size="small"
                      style="margin-top: 12px;"
                      :empty-text="'暂无请求参数'"
                  >
                    <el-table-column prop="name" label="参数名" width="150" />
                    <el-table-column prop="type" label="类型" width="120">
                      <template #default="{ row }">
                        <el-tag size="small" :type="getTypeTagType(row.type)">
                          {{ row.type || 'String' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="required" label="必填" width="80" align="center">
                      <template #default="{ row }">
                        <el-tag :type="getRequired(row) ? 'danger' : ''" size="small">
                          {{ getRequired(row) ? '是' : '否' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="说明" width="120" show-overflow-tooltip />
                    <el-table-column label="校验规则" min-width="200">
                      <template #default="{ row }">
                        <div v-if="hasRules(row)" class="rules-container">
                          <!-- 数值范围 -->
                          <el-tag v-if="row.rules.minValue || row.rules.maxValue" size="small" type="warning">
                            范围: {{ getRangeText(row.rules) }}
                          </el-tag>

                          <!-- 格式校验 -->
                          <el-tag v-if="row.rules.format" size="small" type="success">
                            格式: {{ row.rules.format }}
                          </el-tag>

                          <!-- 正则表达式 -->
                          <el-tooltip v-if="row.rules.pattern" placement="top" :content="row.rules.pattern">
                            <el-tag size="small" type="primary">
                              正则校验
                            </el-tag>
                          </el-tooltip>

                          <!-- 长度限制 -->
                          <el-tag v-if="row.rules.minLength || row.rules.maxLength" size="small" type="info">
                            长度: {{ getLengthText(row.rules) }}
                          </el-tag>

                          <!-- 最小值 -->
                          <el-tag v-if="row.rules.min !== undefined" size="small">
                            最小: {{ row.rules.min }}
                          </el-tag>

                          <!-- 最大值 -->
                          <el-tag v-if="row.rules.max !== undefined" size="small">
                            最大: {{ row.rules.max }}
                          </el-tag>
                        </div>
                        <span v-else class="no-rules">无</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="默认值" width="100" align="center">
                      <template #default="{ row }">
                        <span>{{ row.defaultValue || row.rules?.defaultValue || '-' }}</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="只读" width="70" align="center">
                      <template #default="{ row }">
                        <el-tag v-if="row.readOnly" size="small" type="info">是</el-tag>
                        <span v-else>-</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>

                <!-- 响应参数 -->
                <div class="params-section">
                  <div class="params-title">
                    <el-icon><Download /></el-icon>
                    <span>响应参数</span>
                    <el-tag size="small" type="success" v-if="parsedResponseDefinition.length">
                      {{ parsedResponseDefinition.length }} 个字段
                    </el-tag>
                  </div>
                  <el-table
                      :data="parsedResponseDefinition"
                      border
                      stripe
                      size="small"
                      style="margin-top: 12px;"
                      :empty-text="'暂无响应参数'"
                  >
                    <el-table-column prop="name" label="参数名" width="180" />
                    <el-table-column prop="type" label="类型" width="140">
                      <template #default="{ row }">
                        <el-tag size="small" :type="getTypeTagType(row.type)">
                          {{ row.type || 'String' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                    <el-table-column prop="description" label="说明" min-width="150" show-overflow-tooltip>
                      <template #default="{ row }">
                        <span v-if="row.description">{{ row.description }}</span>
                        <span v-else class="no-data">暂无说明</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="示例值" width="180">
                      <template #default="{ row }">
                        <el-tooltip
                            v-if="row.example || row.defaultValue"
                            :content="String(row.example || row.defaultValue)"
                            placement="top"
                        >
                          <code class="example-code">{{ formatExample(row.example || row.defaultValue) }}</code>
                        </el-tooltip>
                        <span v-else class="no-data">-</span>
                      </template>
                    </el-table-column>
                    <el-table-column label="只读" width="80" align="center">
                      <template #default="{ row }">
                        <el-tag v-if="row.readOnly" size="small" type="info">是</el-tag>
                        <span v-else class="no-data">否</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>

                <!-- 示例代码 -->
                <div v-if="selectedResource.example" class="params-section">
                  <div class="params-title">
                    <el-icon><Document /></el-icon>
                    <span>请求示例</span>
                  </div>
                  <el-input
                      v-model="selectedResource.example"
                      type="textarea"
                      :rows="6"
                      readonly
                      style="margin-top: 12px; font-family: monospace;"
                  />
                </div>
              </div>

              <!-- 菜单特有信息 -->
              <div v-if="selectedResource.classify === 'menu'" class="detail-section">
                <div class="section-title">
                  <svg-icon icon-class="menu" class="section-icon"/>
                  菜单配置
                </div>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="菜单图标">
                    <div style="display: flex; align-items: center; gap: 8px;">
                      <svg-icon
                          :icon-class="selectedResource.resStyle || 'menu'"
                          style="width: 18px; height: 18px;"
                      />
                      <span>{{ selectedResource.resStyle || '默认图标' }}</span>
                    </div>
                  </el-descriptions-item>
                  <el-descriptions-item label="排序">
                    {{ selectedResource.sortIndex || 0 }}
                  </el-descriptions-item>
                  <el-descriptions-item label="是否显示">
                    <el-tag :type="selectedResource.isVisible === 'y'? 'success' : 'info'" size="small">
                      {{ selectedResource.isVisible !== 'n' ? '显示' : '隐藏' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="是否缓存">
                    <el-tag :type="selectedResource.isCache === 'y'? 'success' : 'info'" size="small">
                      {{ selectedResource.isCache === 'y'? '是' : '否' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="组件路径" :span="2">
                    <el-text type="primary" style="font-family: monospace;">
                      {{ selectedResource.path || '-' }}
                    </el-text>
                  </el-descriptions-item>
                </el-descriptions>
              </div>

              <!-- 按钮特有信息 -->
              <div v-if="selectedResource.classify === 'button'" class="detail-section">
                <div class="section-title">
                  <svg-icon icon-class="button" class="section-icon"/>
                  按钮配置
                </div>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="按钮标识">
                    {{ selectedResource.permission || selectedResource.code || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="按钮类型">
                    <el-tag :type="selectedResource.buttonType || 'primary'" size="small">
                      {{ selectedResource.buttonType || 'primary' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="所属页面" :span="2">
                    {{ selectedResource.parentName || '-' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="事件处理" :span="2">
                    <el-text type="primary" style="font-family: monospace;">
                      {{ selectedResource.handler || '-' }}
                    </el-text>
                  </el-descriptions-item>
                </el-descriptions>
              </div>

              <!-- 权限信息 -->
              <div class="detail-section">
                <div class="section-title">
                  <svg-icon icon-class="lock" class="section-icon"/>
                  权限信息
                </div>
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="权限标识" :span="2">
                    <el-tag type="warning" size="small">
                      {{ selectedResource.permission || selectedResource.code || '-' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="创建时间">
                    {{ formatDateTime(selectedResource.createdDate) }}
                  </el-descriptions-item>
                  <el-descriptions-item label="更新时间">
                    {{ formatDateTime(selectedResource.modifiedDate) }}
                  </el-descriptions-item>
                  <el-descriptions-item label="状态">
                    <el-tag :type="selectedResource.status == 1 ? 'success' : 'danger'" size="small">
                      {{ selectedResource.status == 1 ? '启用' : '禁用' }}
                    </el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="备注" :span="2">
                    {{ selectedResource.remark || '-' }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>
          </el-card>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, nextTick, computed} from 'vue'
import {useRoute, useRouter} from "vue-router";
import * as appResourcesApi from "@/api/app/resources";
import { Upload, Download, Document } from '@element-plus/icons-vue'
import {getById} from "@/api/api-service/apiDefinitionApi";

const route = useRoute();
const router = useRouter();

const clientId: any = ref(undefined);
const id: any = ref(undefined);
const clientName: any = ref(undefined);
const treeData = ref([]);//当前选中节点
const dataOptions = ref([]);
const selectedResource = ref<any>(null); // 当前选中的资源
const defaultProps = ref({
  children: "children",
  label: "name"
});

// 通用JSON解析函数
const parseJsonString = (data) => {
  try {
    // 处理 null 或 undefined
    if (data === null || data === undefined) {
      return []
    }
    // 如果已经是数组，直接返回
    if (Array.isArray(data)) {
      return data
    }
    // 如果是字符串，尝试解析
    if (typeof data === 'string') {
      // 处理空字符串
      if (data.trim() === '') {
        return []
      }
      return JSON.parse(data)
    }
    return []
  } catch (error) {
    console.error('JSON解析失败:', error, data)
    return []
  }
}

// 解析请求参数
const parsedParamDefinition = computed(() => {
  const data = selectedResource.value?.paramDefinition
      ?? selectedResource.value?.params
      ?? []
  return parseJsonString(data)
})

// 解析响应参数
const parsedResponseDefinition = computed(() => {
  const data = selectedResource.value?.responseDefinition
      ?? selectedResource.value?.params
      ?? []
  return parseJsonString(data)
})

// 获取必填状态
const getRequired = (row: any) => {
  if (row.rules && typeof row.rules.required !== 'undefined') {
    return row.rules.required
  }
  if (typeof row.required !== 'undefined') {
    return row.required
  }
  return false
}

// 判断是否有校验规则
const hasRules = (row: any) => {
  if (!row.rules) return false
  const ruleKeys = Object.keys(row.rules).filter(key => key !== 'required')
  return ruleKeys.length > 0
}

// 获取数值范围文本
const getRangeText = (rules: any) => {
  const min = rules.minValue ?? rules.min
  const max = rules.maxValue ?? rules.max

  if (min !== undefined && max !== undefined) {
    return `${min} ~ ${max}`
  } else if (min !== undefined) {
    return `≥ ${min}`
  } else if (max !== undefined) {
    return `≤ ${max}`
  }
  return ''
}

// 获取类型标签颜色
const getTypeTagType = (type: any) => {
  if (!type) return 'success'
  const t = type.toLowerCase()
  if (t.includes('string')) return 'success'
  if (t.includes('int') || t.includes('number')) return 'warning'
  if (t.includes('bool')) return 'danger'
  if (t.includes('array') || t.includes('[]')) return 'primary'
  if (t.includes('object')) return 'info'
  return 'success'
}

// 获取长度限制文本
const getLengthText = (rules: any) => {
  const min = rules.minLength
  const max = rules.maxLength

  if (min !== undefined && max !== undefined) {
    return `${min} ~ ${max}`
  } else if (min !== undefined) {
    return `≥ ${min}`
  } else if (max !== undefined) {
    return `≤ ${max}`
  }
  return ''
}

// 格式化示例值
const formatExample = (value: any) => {
  if (!value) return '-'
  const str = String(value)
  return str.length > 30 ? str.substring(0, 30) + '...' : str
}

// 判断是否为API资源
const isApiResource = computed(() => {
  return selectedResource.value?.classify === 'api' || selectedResource.value?.classify === 'openApi'
})

const handleBack = () => {
  router.back()
}

function loadTree() {
  let params = {
    clientId: id.value,
  }

  appResourcesApi.getTreeClient(params).then((res: any) => {
    // 清空,避免多次调用重复 push
    treeData.value = []

    collectExpandIds(res.data.resources || [], treeData, 1)

    dataOptions.value = res.data.resources;
  })
}

const collectExpandIds = (nodes: any, targetRef: any, maxLevel = 1) => {
  const traverse = (node: any, level: any) => {
    if (level <= maxLevel) {
      targetRef.value.push(node.id)
    }
    if (Array.isArray(node.children)) {
      node.children.forEach((child: any) => traverse(child, level + 1))
    }
  }

  nodes.forEach((root: any) => traverse(root, 1))
}

/** 通过条件过滤节点  */
const filterNode = (value: any, data: any) => {
  if (!value) return true;
  return data.label.indexOf(value) !== -1;
};

/** 节点单击事件 */
function handleNodeClick(data: any) {
  console.log('选中的资源:', data)

  // 从服务器获取详细信息,可以在这里调用API
  loadResourceDetail(data.id)
}

// 加载详细信息的API
const loadResourceDetail = async (resourceId: string) => {
  try {
    const res = await getById(resourceId)
    selectedResource.value = res.data
  } catch (error) {
    console.error('加载资源详情失败:', error)
  }
}

const iconMap: Record<string, string> = {
  menu: 'menu',
  button: 'button',
  api: 'api',
  openApi: 'api'
}

const getResourceIcon = (classify?: string, icon?: string): string => {
  return icon || (classify ? iconMap[classify] : undefined) || 'resource'
}

const tagTypeMap: Record<string, string> = {
  menu: 'success',
  button: 'info',
  api: 'warning',
  openApi: 'danger'
}

const resourceTagType = (classify?: string): string => {
  return classify ? tagTypeMap[classify] ?? '' : ''
}

const labelMap: Record<string, string> = {
  menu: '菜单',
  button: '按钮',
  api: 'API',
  openApi: 'OpenAPI'
}

const resourceLabel = (classify?: string) => {
  return classify ? labelMap[classify] ?? '未知' : '未知'
}

// HTTP方法标签类型
const getMethodTagType = (method: string) => {
  const methodMap: Record<string, string> = {
    'GET': 'success',
    'POST': 'primary',
    'PUT': 'warning',
    'DELETE': 'danger',
    'PATCH': 'info'
  }
  return methodMap[method?.toUpperCase()] || ''
}

// 格式化时间
const formatDateTime = (dateTime?: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  const q = route.query.clientId
  const w = route.query.clientName
  const e = route.query.id
  clientId.value = typeof q === 'string' ? q : clientId.value
  clientName.value = typeof w === 'string' ? w : clientName.value
  id.value = typeof e === 'string' ? e : id.value
  nextTick(() => {
    loadTree();
  });
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

    .content-body {
      flex: 1;
      overflow: auto;
      background: white;
      height: calc(100vh - 260px);
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      min-height: 0;
    }
  }
}

.content-body {
  .tree-card {
    width: 320px;
    margin-right: 20px;
    height: 100%;
    border-radius: 12px;
    border: 1px solid #e4e7ed;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;

    :deep(.el-card__body) {
      padding: 0 !important;
      height: 100%;
      overflow: auto;
    }

    .tree-container {
      min-height: 600px;
      max-height: 100%;
      overflow: auto;
      padding: 20px 12px;

      :deep(.el-tree-node__content) {
        padding: 20px 12px;

        border-radius: 6px;
        transition: all 0.3s;

        &:hover {
          background: #f5f7fa;
        }
      }

      :deep(.el-tree-node.is-current > .el-tree-node__content) {
        background: #ecf5ff;
        font-weight: 500;
      }

      .modern-tree {
        .tree-node {
          display: flex;
          align-items: center;
          width: 100%;
          gap: 8px;

          .resource-icon {
            width: 16px;
            height: 16px;
            color: #909399;
            flex-shrink: 0;
            transition: all 0.3s;
          }

          .tree-label {
            flex: 1;
            font-size: 14px;
            color: #606266;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .tree-tag {
            flex-shrink: 0;
            font-size: 12px;
            padding: 0 6px;
            height: 20px;
            line-height: 18px;
          }
        }
      }

      .tree-empty {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 200px;
        color: #909399;

        .empty-icon {
          width: 60px;
          height: 60px;
          color: #dcdfe6;
          margin-bottom: 12px;
        }

        p {
          margin: 0;
          font-size: 14px;
        }
      }
    }
  }

  .list-card {
    flex: 1;
    height: 100%;
    border-radius: 12px;
    border: 1px solid #e4e7ed;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
    overflow: hidden;

    :deep(.el-card__header) {
      padding: 16px 20px;
      border-bottom: 1px solid #f0f2f5;
      background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);

      .detail-header {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .detail-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 16px;
          font-weight: 600;
          color: #303133;

          .title-icon {
            width: 18px;
            height: 18px;
            color: #409eff;
          }
        }
      }
    }

    :deep(.el-card__body) {
      flex: 1;
      overflow: auto;
      padding: 20px;
    }

    .detail-empty {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100%;
      min-height: 400px;
      color: #909399;

      .empty-icon {
        width: 80px;
        height: 80px;
        color: #dcdfe6;
        margin-bottom: 16px;
      }

      p {
        margin: 0;
        font-size: 14px;
      }
    }

    .detail-content {
      .detail-section {
        margin-bottom: 24px;

        &:last-child {
          margin-bottom: 0;
        }

        .section-title {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 15px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 16px;
          padding-bottom: 8px;
          border-bottom: 2px solid #e4e7ed;

          .section-icon {
            width: 16px;
            height: 16px;
            color: #409eff;
          }
        }

        .params-section {
          margin-top: 20px;
          padding: 16px;
          background: #f9fafc;
          border-radius: 8px;

          .params-title {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: 600;
            color: #606266;

            .el-icon {
              color: #409eff;
            }
          }
        }

        :deep(.el-descriptions) {
          .el-descriptions__label {
            width: 120px;
            font-weight: 500;
            background: #fafafa;
          }

          .el-descriptions__content {
            color: #606266;
          }
        }
      }
    }
  }
}

.tree-card {
  animation-delay: 0.2s;
}

.list-card {
  animation-delay: 0.3s;
}

.rules-container {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}

.no-rules {
  color: #909399;
  font-size: 12px;
}

:deep(.el-table) {
  font-size: 13px;
}

:deep(.el-table td) {
  padding: 8px 0;
}
</style>
