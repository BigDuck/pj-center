<template>
  <div class="app-container calendar-list-container">
    <div class="filter-container">
    </div>
    <el-table :key='tableKey' :data="list" v-loading="listLoading" element-loading-text="给我一点时间" border fit
              highlight-current-row style="width: 100%">

      <el-table-column align="center" label="序号">
        <template scope="scope">
          <span>{{scope.row.id}}</span>
        </template>
      </el-table-column>

      <el-table-column label="请求接口" show-overflow-tooltip>
        <template scope="scope">
          <span>{{ scope.row.requestUri}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="IP地址">
        <template scope="scope">
          <span>{{scope.row.remoteAddr}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="请求方式">
        <template scope="scope">
          <span>{{scope.row.method}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="传入参数" show-overflow-tooltip>
        <template scope="scope">
          <span>{{scope.row.params}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="请求时间">
        <template scope="scope">
          <span>{{scope.row.time}}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="创建时间">
        <template scope="scope">
          <span>{{scope.row.createTime | parseTime('{y}-{m}-{d} {h}:{i}')}}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作">
        <template scope="scope">
          <el-button size="mini" type="danger" v-if="sys_dict_add"
                     @click="handleDelete(scope.row)">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div v-show="!listLoading" class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                     :current-page.sync="listQuery.page"
                     :page-sizes="[10,20,30, 50]" :page-size="listQuery.limit"
                     layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>
  </div>
</template>

<script>
  import { fetchList, delObj } from '@/api/log'
  import waves from '@/directive/waves/index.js' // 水波纹指令
  import { mapGetters } from 'vuex'
  export default {
    name: 'table_log',
    directives: {
      waves
    },
    data() {
      return {
        list: null,
        total: null,
        sys_dict_add: false,
        listLoading: true,
        listQuery: {
          page: 1,
          limit: 20
        },
        tableKey: 0
      }
    },
    computed: {
      ...mapGetters([
        'permissions'
      ])
    },
    created() {
      this.getList()
      this.sys_log_del = this.permissions['sys_log_del']
    },
    methods: {
      getList() {
        this.listLoading = true
        this.listQuery.orderByField = "create_time"
        this.listQuery.isAsc = false
        fetchList(this.listQuery).then(response => {
          this.list = response.data.records
          this.total = response.data.total
          this.listLoading = false
        })
      },
      handleSizeChange(val) {
        this.listQuery.limit = val
        this.getList()
      },
      handleCurrentChange(val) {
        this.listQuery.page = val
        this.getList()
      },
      handleDelete(row) {
        delObj(row.id)
          .then(response => {
            this.dialogFormVisible = false
            this.getList()
            this.$notify({
              title: '成功',
              message: '删除成功',
              type: 'success',
              duration: 2000
            })
          })
      }
    }
  }
</script>
