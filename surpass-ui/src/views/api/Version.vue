<template>
  <div class="version-page">
    <!-- 页面头部 -->
    <PageHeader @back="handleBack"/>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- API选择卡片 -->
      <ApiSelector
          :api-list="apiList"
          v-model:selected-api-id="selectedApiId"
          :loading="apiLoading"
          :has-published-version="hasPublishedVersion"
          @create="showCreateDialog"
          @create-next="createNextVersion"
          @refresh="refreshList"
      />

      <!-- 版本管理区域 -->
      <div v-if="selectedApiId" class="version-management">
        <!-- 统计信息和操作栏 -->
        <div class="management-header">
          <!-- 统计卡片 -->
          <StatisticsCards :statistics="versionStatistics"/>
        </div>

        <!-- 版本列表 -->
        <VersionList
            :version-list="versionList"
            :loading="loading"
            @view="viewVersion"
            @edit="editVersion"
            @submit-review="submitForReview"
            @publish="publishVersion"
            @reject="rejectVersion"
            @offline="offlineVersion"
            @online="onlineVersion"
            @delete="deleteVersion"
            @create="showCreateDialog"
        />
      </div>

      <!-- 未选择API的提示 -->
      <div v-else class="no-api-selected">
        <el-empty description="请选择API开始版本管理" :image-size="200"/>
      </div>
    </div>

    <!-- 新增/编辑抽屉 -->
    <VersionForm
        :visible="drawerVisible"
        :is-edit="isEdit"
        :form-data="formData"
        :param-list="paramList"
        :response-list="responseList"
        :param-info-list="paramInfoList"
        :submitting="submitting"
        @update:visible="drawerVisible = $event"
        @update:form-data="updateFormData($event)"
        @update:param-list="paramList = $event"
        @update:response-list="responseList = $event"
        @close="handleDrawerClose"
        @submit="handleSubmit"
        @rule-config="openRuleConfig"
        @paging-params-change="handlePagingParams"
    />

    <!-- 规则配置对话框 -->
    <RuleConfig
        :visible="ruleConfigDialog"
        :current-param="currentEditingParam"
        @update:visible="ruleConfigDialog = $event"
        @save="saveRuleConfig"
    />

    <!-- 版本详情对话框 -->
    <VersionDetail
        :visible="detailDialogVisible"
        :current-version="currentVersion"
        :param-info-list="paramInfoList"
        @update:visible="detailDialogVisible = $event"
    />
  </div>
</template>

<script setup>
import {computed, onMounted, reactive, ref, watch} from 'vue'
import {useRoute} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import * as publishApi from '@/api/api-service/publishApi.ts'
import * as apiDefinitionApi from '@/api/api-service/apiDefinitionApi.ts'
import * as apiVersionApi from '@/api/api-service/apiVersionApi.ts'
import {apiParamTypeList} from '@/utils/enums/ApiContants.ts'

// 导入拆分后的组件
import PageHeader from './version/components/PageHeader.vue'
import ApiSelector from './version/components/ApiSelector.vue'
import StatisticsCards from './version/components/StatisticsCards.vue'
import VersionList from './version/components/VersionList.vue'
import VersionForm from './version/components/VersionForm.vue'
import RuleConfig from './version/components/RuleConfig.vue'
import VersionDetail from './version/components/VersionDetail.vue'

const route = useRoute()

// 响应式数据
const loading = ref(false)
const apiLoading = ref(false)
const drawerVisible = ref(false)
const detailDialogVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)

const selectedApiId = ref(null)
const apiList = ref([])
const versionList = ref([])
const currentVersion = ref(null)
const versionStatistics = ref(null)

const paramInfoList = ref([...apiParamTypeList])

const formData = reactive({
  id: null,
  apiId: null,
  version: 1,
  sqlTemplate: '',
  paramDefinition: '',
  responseTemplate: '',
  responseDefinition: '',
  description: '',
  supportsPaging: '1',
  pageSizeMax: 20,
  rateLimit: 0
})

// 参数列表
const paramList = ref([])
// 响应参数列表
const responseList = ref([])

// 规则配置相关
const ruleConfigDialog = ref(false)
const currentEditingParam = ref(null)

// 计算属性
const hasPublishedVersion = computed(() => {
  return versionList.value.some(version => version.status === 2) // 2 = 已发布
})

// 生命周期
onMounted(() => {
  loadApis()
  // 如果有URL参数，自动选择API
  if (route.query.apiId) {
    selectedApiId.value = route.query.apiId
    loadVersions()
  }
})

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

const loadVersions = async () => {
  if (!selectedApiId.value) return

  try {
    loading.value = true
    const [versionsResponse, statisticsResponse] = await Promise.all([
      apiVersionApi.getByApiId(selectedApiId.value),
      apiVersionApi.getVersionStatistics(selectedApiId.value)
    ])

    versionList.value = versionsResponse.data || []
    versionStatistics.value = statisticsResponse.data || null
  } catch (error) {
    ElMessage.error('加载版本列表失败')
    console.error('加载版本列表失败:', error)
  } finally {
    loading.value = false
  }
}

const refreshList = () => {
  loadVersions()
}

const showCreateDialog = () => {
  if (!selectedApiId.value) {
    ElMessage.warning('请先选择API')
    return
  }

  isEdit.value = false
  resetForm()
  formData.apiId = selectedApiId.value

  // 自动设置下一个版本号
  if (versionList.value.length > 0) {
    const maxVersion = Math.max(...versionList.value.map(v => v.version))
    formData.version = maxVersion + 1
  } else {
    formData.version = 1
  }

  // 处理分页参数
  handlePagingParams()

  drawerVisible.value = true
}

const editVersion = async (row) => {
  if (row.status !== 0) {
    ElMessage.warning('只有草稿状态的版本可以编辑')
    return
  }

  isEdit.value = true
  const apiVersionResponse = await apiVersionApi.getById(row.id)
  Object.assign(formData, {...apiVersionResponse.data})

  // 解析参数定义
  if (row.paramDefinition) {
    try {
      paramList.value = Array.isArray(row.paramDefinition) ? row.paramDefinition : JSON.parse(row.paramDefinition)
    } catch (error) {
      console.error('解析参数定义失败:', error)
      paramList.value = []
    }
  } else {
    paramList.value = []
  }

  // 解析响应参数定义
  if (row.responseDefinition) {
    try {
      responseList.value = Array.isArray(row.responseDefinition) ? row.responseDefinition : JSON.parse(row.responseDefinition)
    } catch (error) {
      console.error('解析响应参数定义失败:', error)
      responseList.value = []
    }
  } else {
    responseList.value = []
  }

  // 处理分页参数
  handlePagingParams()

  drawerVisible.value = true
}

const viewVersion = (row) => {
  currentVersion.value = row
  detailDialogVisible.value = true
}

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    apiId: null,
    version: 1,
    sqlTemplate: '',
    paramDefinition: '',
    responseTemplate: '',
    responseDefinition: '',
    description: '',
    supportsPaging: '0',
    pageSizeMax: 20,
    rateLimit: 0
  })
  paramList.value = []
  responseList.value = []
}

const handleDrawerClose = () => {
  drawerVisible.value = false
  resetForm()
}

const handleSubmit = async () => {
  try {
    submitting.value = true

    // 处理分页参数
    handlePagingParams()

    // 将参数列表转换为JSON字符串
    const validParams = paramList.value.filter(param => param.name.trim() !== '')
    // 将 paramDefinition 转换为 JSON 字符串后提交
    formData.paramDefinition = validParams.length > 0 ? JSON.stringify(validParams, null, 2) : ''

    // 将响应参数列表转换为JSON字符串
    const validResponses = responseList.value.filter(response => response.name.trim() !== '')
    // 将 responseDefinition 转换为 JSON 字符串后提交
    formData.responseDefinition = validResponses.length > 0 ? JSON.stringify(validResponses, null, 2) : ''

    if (isEdit.value) {
      await apiVersionApi.update(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      await apiVersionApi.create(formData)
      ElMessage.success('创建成功')
    }

    loadVersions()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    console.error('保存失败:', error)
  } finally {
    submitting.value = false
  }
}

const submitForReview = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要提交版本 v${row.version} 审核吗？`,
        '确认提交审核',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await publishApi.submitForReview(row.apiId, row.id)
    ElMessage.success('提交审核成功')
    loadVersions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提交审核失败')
      console.error('提交审核失败:', error)
    }
  }
}

const publishVersion = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要发布版本 v${row.version} 吗？`,
        '确认发布',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await publishApi.publishVersion(row.apiId, row.id)
    ElMessage.success('发布成功')
    loadVersions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发布失败')
      console.error('发布失败:', error)
    }
  }
}

const rejectVersion = async (row) => {
  try {
    const {value: reason} = await ElMessageBox.prompt(
        '请输入驳回原因',
        '驳回版本',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputType: 'textarea',
          inputPlaceholder: '请输入驳回原因...'
        }
    )

    if (reason) {
      await publishApi.rejectVersion(row.apiId, row.id, reason)
      ElMessage.success('版本已驳回')
      loadVersions()
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('驳回失败')
      console.error('驳回失败:', error)
    }
  }
}

const offlineVersion = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要下线版本 v${row.version} 吗？`,
        '确认下线',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await publishApi.offlineVersion(row.apiId, row.id)
    ElMessage.success('下线成功')
    loadVersions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('下线失败')
      console.error('下线失败:', error)
    }
  }
}

const onlineVersion = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要重新上线版本 v${row.version} 吗？`,
        '确认上线',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await publishApi.onlineVersion(row.apiId, row.id)
    ElMessage.success('上线成功')
    loadVersions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('上线失败')
      console.error('上线失败:', error)
    }
  }
}

const createNextVersion = async () => {
  if (!selectedApiId.value) {
    ElMessage.warning('请先选择API')
    return
  }

  try {
    // 查找当前已发布的版本
    const publishedVersion = versionList.value.find(v => v.status === 2)
    if (!publishedVersion) {
      ElMessage.warning('没有找到已发布的版本')
      return
    }

    // 计算下一个版本号
    const maxVersion = Math.max(...versionList.value.map(v => v.version))
    const nextVersion = maxVersion + 1

    // 填充表单数据
    isEdit.value = false
    resetForm()
    formData.apiId = selectedApiId.value
    formData.version = nextVersion
    formData.sqlTemplate = publishedVersion.sqlTemplate
    formData.paramDefinition = publishedVersion.paramDefinition
    formData.responseTemplate = publishedVersion.responseTemplate
    formData.responseDefinition = publishedVersion.responseDefinition
    formData.description = `基于版本 v${publishedVersion.version} 迭代`
    formData.pageSizeMax = publishedVersion.pageSizeMax
    formData.supportsPaging = publishedVersion.supportsPaging
    formData.rateLimit = publishedVersion.rateLimit

    // 解析参数定义
    if (publishedVersion.paramDefinition) {
      try {
        paramList.value = Array.isArray(publishedVersion.paramDefinition) ? publishedVersion.paramDefinition : JSON.parse(publishedVersion.paramDefinition)
      } catch (error) {
        console.error('解析参数定义失败:', error)
        paramList.value = []
      }
    }

    // 解析响应参数定义
    if (publishedVersion.responseDefinition) {
      try {
        responseList.value = Array.isArray(publishedVersion.responseDefinition) ? publishedVersion.responseDefinition : JSON.parse(publishedVersion.responseDefinition)
      } catch (error) {
        console.error('解析响应参数定义失败:', error)
        responseList.value = []
      }
    }

    // 处理分页参数
    handlePagingParams()

    drawerVisible.value = true

  } catch (error) {
    ElMessage.error('创建下一个版本失败')
    console.error('创建下一个版本失败:', error)
  }
}

const deleteVersion = async (row) => {
  try {
    await ElMessageBox.confirm(
        `确定要删除版本 v${row.version} 吗？此操作不可恢复。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
    )

    await apiVersionApi.deleteData(row.id)
    ElMessage.success('删除成功')
    loadVersions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
      console.error('删除失败:', error)
    }
  }
}

const handleBack = () => {
  // 返回逻辑
  window.history.back()
}

// 当 selectedApiId 变化时加载版本数据
watch(selectedApiId, (newValue) => {
  if (newValue) {
    loadVersions()
  }
})

const openRuleConfig = (param) => {
  // 不允许编辑只读参数的规则
  if (param.readOnly) {
    ElMessage.warning('不能编辑只读参数的规则')
    return
  }

  currentEditingParam.value = param
  ruleConfigDialog.value = true
}

const saveRuleConfig = (rules) => {
  if (!currentEditingParam.value) return

  // 直接存储为对象而不是序列化为字符串
  currentEditingParam.value.rules = Object.keys(rules).length > 0 ? rules : {}
  ruleConfigDialog.value = false
}

const updateFormData = (newData) => {
  Object.assign(formData, newData)
}

const handlePagingParams = () => {
  // 如果启用了分页，则确保分页参数存在
  if (formData.supportsPaging === '1') {
    // 检查是否已经存在分页参数
    const pageNumIndex = paramList.value.findIndex(param => param.name === '_pageNum')
    const pageSizeIndex = paramList.value.findIndex(param => param.name === '_pageSize')

    // 添加或更新 _pageNum 参数
    if (pageNumIndex === -1) {
      paramList.value.unshift({
        name: '_pageNum',
        type: 'Integer',
        rules: {required: true, minValue: 1},
        description: '页码',
        readOnly: true
      })
    } else {
      // 确保只读属性设置正确
      paramList.value[pageNumIndex].readOnly = true
      paramList.value[pageNumIndex].type = 'Integer'
      paramList.value[pageNumIndex].rules = {required: true, minValue: 1}
    }

    // 添加或更新 _pageSize 参数
    if (pageSizeIndex === -1) {
      paramList.value.unshift({
        name: '_pageSize',
        type: 'Integer',
        rules: {required: true, minValue: 1, maxValue: formData.pageSizeMax || 10000},
        description: '每页条数',
        readOnly: true
      })
    } else {
      // 确保只读属性设置正确
      paramList.value[pageSizeIndex].readOnly = true
      paramList.value[pageSizeIndex].type = 'Integer'
      paramList.value[pageSizeIndex].rules = {required: true, minValue: 1, maxValue: formData.pageSizeMax || 10000}
    }
  } else {
    // 如果禁用了分页，则移除分页参数
    paramList.value = paramList.value.filter(param =>
        param.name !== '_pageNum' && param.name !== '_pageSize'
    )
  }
}
</script>

<style scoped lang="scss">
@import "./css/version.css";
</style>