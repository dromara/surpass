<template>
  <div class="app-container">
    <el-card class="common-card query-box">
      <div class="queryForm">
        <el-form :model="queryParams" ref="queryRef" :inline="true"
                 @submit.native.prevent>
          <el-form-item label="客户端名称">
            <el-input
                v-model="queryParams.clientName"
                clearable
                style="width: 200px"
                @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="客户端ID">
            <el-input
                v-model="queryParams.clientId"
                clearable
                style="width: 200px"
                @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="客户端类型">
            <el-select
                v-model="queryParams.clientType"
                clearable
                style="width: 200px"
            >
              <el-option v-for="item in client_type"
                         :key="item.value"
                         :value="item.value"
                         :label="item.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button @click="handleQuery">{{ t('org.button.query') }}</el-button>
            <el-button @click="resetQuery">{{ t('org.button.reset') }}</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="common-card">
      <div class="btn-form">
        <el-button
            type="primary"
            @click="handleAdd"
        >{{ t('org.button.add') }}
        </el-button>
        <el-button
            type="danger"
            :disabled="ids.length === 0"
            @click="onBatchDelete"
        >{{ t('org.button.deleteBatch') }}
        </el-button>
      </div>
      <el-table
          border
          v-loading="loading"
          :data="clientList"
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column prop="clientId" label="客户端ID" align="center" min-width="200"
                         :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="clientName" label="客户端名称" align="center" min-width="120"
                         :show-overflow-tooltip="true"></el-table-column>

        <el-table-column prop="clientType" label="客户端类型" align="center" min-width="100">
          <template #default="scope">
            <dict-tag-number :options="client_type" :value="scope.row.clientType"/>
          </template>
        </el-table-column>
        <el-table-column prop="contactName" label="联系人" align="center" min-width="80"
                         :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="contactPhone" label="联系电话" align="center" min-width="120"
                         :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="department" label="所属部门" align="center" min-width="100"
                         :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录时间" align="center" min-width="160"
                         :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="status" :label="t('org.status')" align="center" min-width="80">
          <template #default="scope">
            <span v-if="scope.row.status === 1"><el-icon color="green"><SuccessFilled
                class="success"/></el-icon></span>
            <span v-if="scope.row.status === 0"><el-icon color="#808080"><CircleCloseFilled/></el-icon></span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('jbx.text.action')" align="center" width="170" fixed="right">
          <template #default="scope">
            <el-tooltip content="我的权限">
              <el-button link icon="SetUp" @click="handlePermission(scope.row)"></el-button>
            </el-tooltip>
            <el-tooltip content="编辑">
              <el-button link icon="Edit" @click="handleUpdate(scope.row)"></el-button>
            </el-tooltip>
<!--            <el-tooltip content="应用授权">
              <el-button link icon="SetUp" @click="handleAppAuth(scope.row)"></el-button>
            </el-tooltip>-->
            <el-tooltip content="移除">
              <el-button link icon="Delete" type="danger" @click="handleDelete(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNumber"
          v-model:limit="queryParams.pageSize"
          :page-sizes="queryParams.pageSizeOptions"
          @pagination="getList"
      />
    </el-card>
    <!--新增或修改对话框-->
    <clientEdit :title="title" :open="open"
                :formId="id"
                :client-type="client_type"
                @dialogOfClosedMethods="dialogOfClosedMethods"></clientEdit>
    <!--应用授权对话框-->
    <clientAppAuth :open="authOpen" :clientId="authClientId" :clientName="authClientName" @dialogOfClosedMethods="authDialogOfClosedMethods"></clientAppAuth>
  </div>
</template>

<script setup lang="ts">
import {ref, getCurrentInstance, reactive, toRefs} from "vue";
import modal from "@/plugins/modal";
import {useI18n} from "vue-i18n";
import {deleteBatch, list} from "@/api/api-service/client";
import {set2String} from "@/utils"
import clientEdit from "./edit.vue";
import clientAppAuth from "./appAuth.vue";
import DictTagNumber from "@/components/DIctTagNumber/index.vue";
import {useRouter} from "vue-router";

const {t} = useI18n()

const {proxy} = getCurrentInstance()!;

const {client_type}
    = proxy?.useDict("client_type");

const router: any = useRouter(); // 获取路由实例

const data: any = reactive({
  queryParams: {
    pageNumber: 1,
    pageSize: 10,
    pageSizeOptions: [10, 20, 50]
  }
});

const {queryParams} = toRefs(data);
const clientList: any = ref<any>([]);
const open: any = ref(false);
const authOpen: any = ref(false);
const permissionOpen: any = ref(false);
const loading: any = ref(true);
const title: any = ref("");
const id: any = ref(undefined);
const authClientId: any = ref(undefined);
const authClientName: any = ref("");
const total: any = ref(0);
const ids: any = ref<any>([]);
const selectionlist: any = ref<any>([]);

/**
 * 获取列表
 */
function getList(): any {
  list(queryParams.value).then((res: any) => {
    if (res.code === 0) {
      loading.value = false;
      clientList.value = res.data.rows;
      total.value = res.data.records;
    }
  })
}

/**
 * 查询
 */
function handleQuery(): any {
  queryParams.value.pageNumber = 1;
  getList();
}

/**
 * 重置
 */
function resetQuery(): any {
  queryParams.value.clientName = undefined;
  queryParams.value.clientId = undefined;
  queryParams.value.clientType = undefined;
  handleQuery();
}

function dialogOfClosedMethods(val: any): any {
  open.value = false;
  id.value = undefined;
  if (val) {
    getList();
  }
}

function authDialogOfClosedMethods(val: any): any {
  authOpen.value = false;
  authClientId.value = undefined;
  authClientName.value = "";
}

function handleAdd(): any {
  id.value = undefined;
  title.value = t('jbx.text.add')
  open.value = true;
}

function handleUpdate(row: any): any {
  id.value = row.id;
  title.value = t('jbx.text.edit')
  open.value = true;
}

function handleAppAuth(row: any): any {
  authClientId.value = row.id;
  authClientName.value = row.clientName;
  authOpen.value = true;
}

const handlePermission = (row: any): any => {
  // 确保 router 实例存在并且是有效的
  if (router && typeof router.push === 'function') {
    router.push(`/app/client/permission?clientId=${row.clientId}&clientName=${row.clientName}&id=${row.id}`);
  } else {
    console.error('Router instance is not available.');
  }
}

/** 多选操作*/
function handleSelectionChange(selection: any): any {
  selectionlist.value = selection;
  ids.value = selectionlist.value.map((item: any) => item.id);
}

/** 多选删除操作*/
function onBatchDelete(): any {
  modal.confirm(t('jbx.confirm.text.delete')).then(function () {
    let setIds: any = set2String(ids.value);
    return deleteBatch(setIds);
  }).then((res: any) => {
    if (res.code === 0) {
      handleQuery();
      modal.msgSuccess(t('jbx.alert.delete.success'));
    } else {
      modal.msgError(t('jbx.alert.delete.error'));
    }
  }).catch(() => {
  });
}

/** 删除按钮操作 */
function handleDelete(row: any): any {
  modal.confirm(t('org.deleteTip1') + row.clientName + t('org.deleteTip2')).then(function () {
    return deleteBatch(row.id);
  }).then((res: any) => {
    if (res.code === 0) {
      getList();
      modal.msgSuccess(t('jbx.alert.delete.success'));
    } else {
      modal.msgError(t('jbx.alert.delete.error'));
    }
  }).catch(() => {
  });
}

getList();
</script>

<style lang="scss" scoped>
.btn-form {
  margin-bottom: 10px;
}

.common-card {
  margin-bottom: 15px;
}

.app-container {
  padding: 0;
  background-color: #f5f7fa;
}
</style>
