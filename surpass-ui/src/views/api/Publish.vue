<template>
  <div class="publish-page">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- API选择卡片 -->
      <el-card class="api-selector-card" shadow="never">
        <template #header>
          <div class="card-header">
            <div style="display: flex;justify-content: flex-start;align-items: center">
              <span>选择API：</span>
              <div class="selector-content">
                <el-select
                    v-model="selectedApiId"
                    placeholder="请选择要管理的API"
                    style="width: 500px"
                    @change="loadPublishHistory"
                    :loading="apiLoading"
                >
                  <el-option
                      v-for="api in apiList"
                      :key="api.id"
                      :label="api.name + '(' + api.path + ', '+ (api.description || '') +')'"
                      :value="api.id"
                  >
                    <div class="api-option">
                      <span class="api-name">{{ api.name }}</span>
                      <span class="api-path">{{ api.path }}</span>
                    </div>
                  </el-option>
                </el-select>
              </div>
            </div>
            <div>
              <el-button type="primary" @click="refreshList" :loading="loading">
                刷新
              </el-button>
            </div>
          </div>
        </template>


        <!-- 发布状态区域 -->
        <div v-if="selectedApiId" class="publish-management">
          <!-- 当前发布状态 -->
          <div class="current-publish" v-if="latestPublish">
            <el-card class="publish-status-card" shadow="never">
              <template #header>
                <div class="card-header">
                  <span>当前发布状态</span>
                  <div>
                    <el-button
                        link
                        type="primary"
                        icon="View"
                        @click="viewApiDefinition"
                        title="查看接口定义详情"
                    >
                    </el-button>
                  </div>
                </div>
              </template>
              <div class="publish-info">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="当前版本">
                    <el-tag type="success">v{{ latestPublish.apiVersion?.version }}</el-tag>
                  </el-descriptions-item>
                  <el-descriptions-item label="发布时间">
                    {{ latestPublish.publishTime }}
                  </el-descriptions-item>
                  <el-descriptions-item label="操作人">
                    {{ latestPublish.operator || '系统' }}
                  </el-descriptions-item>
                  <el-descriptions-item label="描述">
                    {{ latestPublish.description || '无' }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </el-card>
          </div>

          <!-- 发布历史 -->
          <el-card class="publish-history-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>发布历史</span>
                <div class="list-info">
                  <span class="total-count">共 {{ publishHistory.length }} 条记录</span>
                </div>
              </div>
            </template>

            <el-table
                :data="publishHistory"
                v-loading="loading"
                border
            >
              <el-table-column header-align="center" align="center" prop="id" label="ID" width="200"/>
              <el-table-column header-align="center" align="center" label="版本" width="100">
                <template #default="{ row }">
                  <span class="version-number">v{{ row.apiVersion?.version }}</span>
                </template>
              </el-table-column>
              <el-table-column header-align="center" prop="publishTime" label="发布时间" width="180"/>
              <el-table-column header-align="center" prop="operator" label="操作人" width="120"/>
              <el-table-column header-align="center" prop="description" label="描述">
                <template #default="{ row }">
                  <span v-if="row.description">{{ row.description }}</span>
                  <span v-else>{{ row.apiVersion?.description }}</span>
                </template>
              </el-table-column>
              <el-table-column header-align="center" align="center" label="操作" width="80" fixed="right">
                <template #default="{ row }">
                  <div class="action-buttons-group">
                    <el-tooltip content="查看" placement="top">
                      <el-button link type="primary" icon="View" @click="viewVersionDetail(row)"></el-button>
                    </el-tooltip>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 空状态 -->
            <div v-if="!loading && publishHistory.length === 0" class="empty-state">
              <el-empty description="暂无发布历史" :image-size="200"/>
            </div>
          </el-card>
        </div>

        <!-- 未选择API的提示 -->
        <div v-else class="no-api-selected">
          <el-empty description="请选择API开始查看发布记录" :image-size="200"/>
        </div>
      </el-card>

    </div>

    <!-- 版本详情对话框 -->
    <el-dialog
        title="版本详情"
        v-model="detailDialogVisible"
        width="900px"
        class="version-detail-dialog"
    >
      <div class="version-detail-content" v-if="currentVersion">
        <!-- 基本信息卡片 -->
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="版本号">
              <span class="version-number">v{{ currentVersion.version }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusTagType(currentVersion.status)" size="small">
                {{ getStatusText(currentVersion.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ currentVersion.createdDate || currentVersion.createTime }}
            </el-descriptions-item>
            <el-descriptions-item label="描述">
              {{ currentVersion.description || '无' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- SQL模板卡片 -->
        <el-card class="template-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>SQL模板</span>
            </div>
          </template>
          <div class="code-block">
            <pre><code class="sql">{{ currentVersion.sqlTemplate }}</code></pre>
          </div>
        </el-card>

        <!-- 参数定义卡片 -->
        <el-card class="param-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>参数定义</span>
              <el-tag v-if="parsedParams && parsedParams.length > 0" type="info" size="small">
                {{ parsedParams.length }} 个参数
              </el-tag>
            </div>
          </template>
          <div v-if="parsedParams && parsedParams.length > 0">
            <el-table :data="parsedParams" border size="small" class="param-table">
              <el-table-column prop="name" label="参数名" width="120" align="center">
                <template #default="{ row }">
                  <el-tag type="primary" size="small">{{ row.name }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getParamTypeTagType(row.type)" size="small">
                    {{ row.type }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="描述">
                <template #default="{ row }">
                  {{ row.description || '-' }}
                </template>
              </el-table-column>
              <el-table-column prop="required" label="必填" width="80" align="center">
                <template #default="{ row }">
                  <el-tag v-if="row.rules?.required" type="danger" size="small">是</el-tag>
                  <el-tag v-else type="info" size="small">否</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="rules" label="输入规则">
                <template #default="{ row }">
                  {{ getRuleDisplayText(row.rules) || '无' }}
                </template>
              </el-table-column>
            </el-table>
          </div>
          <div v-else class="empty-state">
            <el-empty description="无参数定义" :image-size="80"/>
          </div>
        </el-card>

        <!-- 响应模板卡片 -->
        <el-card class="response-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>响应模板</span>
              <el-tag v-if="currentVersion.responseTemplate" type="success" size="small">
                已配置
              </el-tag>
            </div>
          </template>
          <div v-if="currentVersion.responseTemplate">
            <div class="code-block">
              <pre><code class="json">{{ formatResponseTemplate(currentVersion.responseTemplate) }}</code></pre>
            </div>
            <div class="template-tips">
              <p><strong>模板说明：</strong></p>
              <p>• <code>#{data}</code> 占位符将被实际查询结果替换</p>
              <p>• 支持JSON格式的响应模板</p>
            </div>
          </div>
          <div v-else class="empty-state">
            <el-empty description="无响应模板" :image-size="80"/>
          </div>
        </el-card>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue'
import {ElMessage} from 'element-plus'
import * as publishApi from '@/api/api-service/publishApi.ts'
import * as apiDefinitionApi from '@/api/api-service/apiDefinitionApi.ts'
import * as apiVersionApi from '@/api/api-service/apiVersionApi.ts'

// 响应式数据
const loading = ref(false)
const apiLoading = ref(false)
const detailDialogVisible = ref(false)

const selectedApiId = ref(null)
const apiList = ref([])
const publishHistory = ref([])
const latestPublish = ref(null)
const currentVersion = ref(null)

// 解析参数定义
const parsedParams = computed(() => {
  if (!currentVersion.value || !currentVersion.value.paramDefinition) {
    return []
  }

  try {
    return JSON.parse(currentVersion.value.paramDefinition)
  } catch (error) {
    console.error('解析参数定义失败:', error)
    return []
  }
})

// 生命周期
// 方法
const loadApis = async () => {
  try {
    apiLoading.value = true
    const response = await apiDefinitionApi.list()
    apiList.value = response || []
  } catch (error) {
    ElMessage.error('加载API列表失败')
    console.error('加载API列表失败:', error)
  } finally {
    apiLoading.value = false
  }
}

const loadPublishHistory = async () => {
  if (!selectedApiId.value) return

  try {
    loading.value = true

    // 加载发布历史
    const historyResponse = await publishApi.getPublishHistory(selectedApiId.value)
    publishHistory.value = historyResponse.data || []

    // 加载已发布版本
    const publishRecordResponse = await publishApi.getLatestPublishRecord(selectedApiId.value)
    if (publishRecordResponse.data) {
      latestPublish.value = publishRecordResponse.data
    } else {
      latestPublish.value = null
    }
  } catch (error) {
    ElMessage.error('加载发布历史失败')
    console.error('加载发布历史失败:', error)
  } finally {
    loading.value = false
  }
}

const refreshList = () => {
  loadPublishHistory()
}

const viewVersionDetail = async (row) => {
  try {
    // 如果没有版本信息，通过API获取
    const versionResponse = await apiVersionApi.getById(row.versionId)
    currentVersion.value = versionResponse.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载版本详情失败')
    console.error('加载版本详情失败:', error)
  }
}

const getStatusTagType = (status) => {
  const types = {
    0: 'info',    // 草稿
    1: 'warning', // 待发布
    2: 'success', // 已发布
    3: 'danger'   // 下线
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '草稿',
    1: '待发布',
    2: '已发布',
    3: '下线'
  }
  return texts[status] || '未知'
}

// 获取参数类型标签样式
const getParamTypeTagType = (type) => {
  const types = {
    'string': 'success',
    'number': 'warning',
    'boolean': 'danger',
    'array': 'info',
    'object': 'primary'
  }
  return types[type] || 'info'
}

// 格式化响应模板显示
const formatResponseTemplate = (template) => {
  if (!template) return ''

  try {
    // 尝试解析为JSON进行格式化
    const jsonStr = template.replace(/#\{data\}/g, '"#{data}"')
    const parsed = JSON.parse(jsonStr)
    return JSON.stringify(parsed, null, 2).replace(/"#\{data\}"/g, '#{data}')
  } catch (error) {
    // 如果不是有效的JSON，直接返回原内容
    return template
  }
}

const getRuleDisplayText = (rulesObj) => {
  if (!rulesObj || Object.keys(rulesObj).length === 0) return ''

  try {
    const descriptions = []

    if (rulesObj.required) descriptions.push('必填')
    if (rulesObj.minLength !== undefined) descriptions.push(`最短${rulesObj.minLength}字符`)
    if (rulesObj.maxLength !== undefined) descriptions.push(`最长${rulesObj.maxLength}字符`)
    if (rulesObj.pattern) descriptions.push('正则匹配')
    if (rulesObj.minValue !== undefined) descriptions.push(`最小值${rulesObj.minValue}`)
    if (rulesObj.maxValue !== undefined) descriptions.push(`最大值${rulesObj.maxValue}`)
    if (rulesObj.enumValues) descriptions.push(`枚举值(${rulesObj.enumValues.length}个)`)
    if (rulesObj.format) descriptions.push(`${rulesObj.format}格式`)

    return descriptions.join(', ') || '已配置'
  } catch (e) {
    return '规则格式错误'
  }
}

// 新增查看接口定义的方法
const viewApiDefinition = async () => {
  try {
    // 获取选中的API信息
    const selectedApi = apiList.value.find(api => api.id === selectedApiId.value);
    if (selectedApi) {
      currentVersion.value = {
        ...selectedApi,
        version: latestPublish.value.apiVersion?.version,
        status: latestPublish.value.apiVersion?.status,
        sqlTemplate: latestPublish.value.apiVersion?.sqlTemplate,
        paramDefinition: latestPublish.value.apiVersion?.paramDefinition,
        responseTemplate: latestPublish.value.apiVersion?.responseTemplate,
        createdDate: latestPublish.value.apiVersion?.createdDate,
        description: latestPublish.value.apiVersion?.description
      };
      detailDialogVisible.value = true;
    }
  } catch (error) {
    ElMessage.error('查看接口定义详情失败');
    console.error('查看接口定义详情失败:', error);
  }
};

loadApis()
</script>

<style scoped>
.publish-page {
  min-height: calc(100vh - 140px);
  padding: 0;
}

/* 主要内容区域 */
.main-content {
  margin: 0 auto;
}

/* API选择卡片 */
.api-selector-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.api-selector-card .card-header {
  font-weight: 600;
  color: #303133;
}

.api-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.api-name {
  font-weight: 500;
  color: #303133;
}

.api-path {
  font-size: 12px;
  color: #909399;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 发布管理区域 */
.publish-management {
  margin-top: 24px;
}

/* 当前发布状态卡片 */
.publish-status-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.publish-status-card .card-header {
  font-weight: 600;
  color: #303133;
}

/* 发布历史卡片 */
.publish-history-card {
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.publish-history-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.list-info {
  font-size: 14px;
  color: #909399;
}

.total-count {
  background: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
}

/* 表格样式 */
.version-number {
  font-weight: 600;
  color: #409eff;
  font-size: 14px;
}

.action-buttons-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.action-buttons-group .el-button {
  margin: 0;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
}

.no-api-selected {
  padding: 80px 0;
  text-align: center;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 版本详情对话框样式 */
.version-detail-dialog .el-dialog__body {
  padding: 0;
}

.version-detail-content {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.version-detail-content .el-card {
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.version-detail-content .el-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.version-number {
  font-size: 16px;
  font-weight: bold;
  color: #409eff;
}

.code-block {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: hidden;
}

.code-block pre {
  margin: 0;
  padding: 16px;
  font-family: 'JetBrains Mono', 'Fira Code', 'Courier New', monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #24292e;
  background: transparent;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-all;
}

.code-block code.sql {
  color: #d73a49;
}

.code-block code.json {
  color: #032f62;
}

.code-block code.json .data-placeholder {
  color: #e36209;
  font-weight: bold;
}

.param-table {
  margin-top: 0;
}

.param-table .el-table {
  border-radius: 6px;
}

.param-table .el-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.empty-state {
  padding: 40px 0;
}

/* 响应模板提示样式 */
.response-card .template-tips {
  margin-top: 12px;
  padding: 12px;
  background: #f0f9ff;
  border: 1px solid #e1f5fe;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
}

.response-card .template-tips p {
  margin: 4px 0;
}

.response-card .template-tips code {
  background: #e8f4fd;
  padding: 2px 6px;
  border-radius: 3px;
  color: #1890ff;
  font-family: 'Courier New', monospace;
  font-weight: 500;
}

pre {
  background: #f5f7fa;
  padding: 8px;
  border-radius: 4px;
  margin: 0;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>