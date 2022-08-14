<template>
  <div class="finerMark-container">
    <Tabs :tabsData="tabsData" />
    <div class="topContent">
      <div class="seachBox">
          <el-form :inline="true" :model="formInline" class="demo-form-inline">
          <el-form-item label="index:">
            <el-input v-model="formInline.quotaName" placeholder="Please enter the name of the indicator"></el-input>
          </el-form-item>
            <div class="searchBut" @click="onSubmit()"><i class="iconfont icon-sousuo"></i> search</div>
        </el-form>
      </div>
      <div class="addCont">
        <el-button type="primary" style="width:110px; height:36px" size="small" @click="addItem()" > <span class="iconfont icon-tianjia"></span>add indicator</el-button>
        <!-- <el-button type="primary" style="width:110px; height:36px" size="small" @click="addGpsItem()" >GPS indicator setting</el-button> -->
      </div>
    </div>
    <div class="contBox" v-infinite-scroll="handleCurrentChange" style="overflow:auto" v-loading="loading" infinite-scroll-immediate="false" >
      <div :class="['item', setTitColor(index)]" v-for="(item,index) in tableData" :key="index" >
        <div @click="changeItem(index)">
          <div class="title">
            <div class="tit"> <span class="iconfont icon-zhibiao"></span><span class="long">{{item.name}}</span> </div>
            <div class="del el-icon-close" @click.stop="delItem(item.id)"></div>
          </div>
          <div class="cont">
            <div class=""><span> unit: </span> {{item.unit}}</div>
            <div class=""><span> reference value: </span> {{item.referenceValue}}</div>
          </div>
        </div>
      </div>
      <div class="noData" v-if="total <= 0">
          <img src="./../../assets/nodata.png" alt="" width="129">
      </div>
    </div>

    <el-dialog
      :visible.sync="quotaDialogVisible"
      width="500px"
      center
      @close="resetForm('quota')"
    >
      <div
        slot="title"
        class="dialog-title"
      >
        {{title}}
      </div>

      <el-form
        :rules="rules"
        :model="form"
        ref="quota"
        label-width="140px"
      >
        <el-form-item
          prop="name"
          label="indicator name: "
        >
          <el-input
            placeholder="please enter the name of indicator"
            v-model="form.name"
            size="small"
            maxlength='10'
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="unit"
          label="unit: "
        >
          <el-input
            placeholder="please enter the unit"
            v-model="form.unit"
            maxlength='10'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="subject"
          label="quota subject"
        >
          <el-input
            placeholder="please enter the quota subject"
            v-model="form.subject"
            maxlength='50'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="valueType"
          label="value type"
        >
          <el-select
            placeholder="please choose the value type"
            v-model="form.valueType"
            size="small"
          >
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          prop="valueKey"
          label="value key"
        >
          <el-input
            placeholder="please enter the value key"
            v-model="form.valueKey"
            size="small"
          maxlength='50'
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="snKey"
          label="device identifier field: "
        >
          <el-input
            placeholder="device identifier field"
            v-model="form.snKey"
            maxlength='50'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="webhook"
          label="web hook："
        >
          <el-input
            placeholder="please enter the web hook"
            v-model="form.webhook"
            maxlength='50'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="referenceValue"
          label="reference value"
        >
          <el-input
            placeholder="please enter the reference value"
            v-model="form.referenceValue"
            maxlength='20'
            size="small"
          ></el-input>
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          @click="resetForm('quota')"
          size="small"
          class="butBox"
        >cancel</el-button>
        <el-button
          type="primary"
          @click="submitQuotaForm('quota')"
          size="small"
          class="butBox"
          style="margin-left:30px"
        >confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog
      :visible.sync="GPSDialogVisible"
      width="500px"
      center
    >
      <!-- <div
        slot="title"
        class="dialog-title"
      >
        GPS indicator
      </div> -->

      <el-form
        :model="gpsForm"
        :rules="gpsRules"
        ref="gps"
        label-width="140px"
      >
        <el-form-item
          prop="subject"
          label="subject:"
        >
          <el-input
            placeholder="please enter the subject"
            v-model="gpsForm.subject"
            maxlength='20'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="snKey"
          label="sn key:"
        >
          <el-input
            placeholder="please enter the sn"
            maxlength='50'
            v-model="gpsForm.snKey"
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          prop="singleField"
          label="indicator type"
        >
          <el-radio-group v-model="gpsForm.singleField">
            <el-radio :label="true">single field</el-radio>
            <el-radio :label="false">multiple field</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="gpsForm.singleField"
          prop="valueKey"
        >
          <el-input
            placeholder="Please enter the indicator value field (latitude and longitude)"
            v-model="gpsForm.valueKey"
            maxlength='20'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          v-if="gpsForm.singleField"
          prop="separation"
        >
          <el-input
            placeholder="please enter the separation"
            v-model="gpsForm.separation"
            maxlength='20'
            size="small"
          ></el-input>
        </el-form-item>
        <el-form-item
          v-if="!gpsForm.singleField"
          prop="longitude"
        >
          <el-input
            placeholder="please enter the latitude"
            v-model="gpsForm.longitude"
            size="small"
            maxlength='20'
          ></el-input>
        </el-form-item>
        <el-form-item
          v-if="!gpsForm.singleField"
          prop="latitude"
        >
          <el-input
            placeholder="please enter the longitude"
            v-model="gpsForm.latitude"
            size="small"
            maxlength='20'
          ></el-input>
        </el-form-item>
      </el-form>
      <div
        slot="footer"
        class="dialog-footer"
      >
        <el-button
          @click="GPSDialogVisible = false"
          size="small"
          class="butBox"
        >cancel</el-button>
        <el-button
          type="primary"
          @click="submitGPSForm('gps')"
          size="small"
          class="butBox"
          style="margin-left:30px"
        >confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Provide, Watch } from "vue-property-decorator";
import Tabs from "@/components/Tabs/index.vue";
import {
  quota,
  addQuota,
  putQuota,
  delQuota,
  gps,
  addGps,
  putGps
} from "@/api/fingerMark";

@Component({
  name: "equipment",
  components: {
    Tabs
  }
})

export default class extends Vue {
  private formInline: object = {
    quotaName: ""
  };
  private page: number = 1;
  private pageSize: number = 20;
  private total: number = 1;
  private tableData: any = [];
  private loading = false
  private title = 'add indicator'
  private changeInd = ''

  private setTitColor(index: number){
    switch(index%6) {
      case 1 :{
        return 'colStyB'
        break
      }
      case 2 :{
        return 'colStyC'
        break
      }
      case 3 :{
        return 'colStyD'
        break
      }
      case 4 :{
        return 'colStyE'
        break
      }
    }
  }
  private tabsData: object = [
    {
      label: "equipment management",
      url: "/equipment/list"
    },
    {
      label: "indicator management",
      url: "/fingerMark/list",
      selected: true
    }
  ];

  private options: object = [
    {
      value: "Integer",
      label: "integer"
    },
    {
      value: "Double",
      label: "double"
    }
  ];

  private quotaDialogVisible = false;
  private GPSDialogVisible = false;
  private getDataType = ''
  //添加指标数据
  private form: object = {
    name: "",
    unit: "",
    subject: "",
    valueKey: "",
    valueType: "",
    snKey: "",
    webhook: "",
    referenceValue: ""
  };

  private rules: any = {
    name: [{ required: true, message: "Pleaseenter the indicator name", trigger: "blur" }],
    unit: [{ required: true, message: "Please enter the unit", trigger: "blur" }],
    subject: [{ required: true, message: "Please enter the subject", trigger: "blur" }],
    valueKey: [
      { required: true, message: "Please enter the value key", trigger: "blur" }
    ],
    valueType: [
      { required: true, message: "Please enter the value type", trigger: "change" }
    ],
    snKey: [
      { required: true, message: "Please enter the sn key", trigger: "blur" }
    ],
    webhook: [{ required: true, message: "Please enter the web hook", trigger: "blur" }],
    referenceValue: [
      { required: true, message: "Please enter the reference value", trigger: "blur" }
    ]
  };

  //GPS指标
  private gpsForm = {
    subject: "",
    snKey: "",
    singleField: true,
    valueKey: "",
    separation: "",
    latitude: "",
    longitude: ""
  };
  private gpsRules = {
    subject: [{ required: true, message: "Please enter the subject", trigger: "blur" }],
    snKey: [
      { required: true, message: "Please enter the sn key", trigger: "blur" }
    ],
    singleField: [
      { required: true, message: "Please choose indicator type", trigger: "change" }
    ],
    valueKey: [
      { required: true, message: "Please enter the GPS info", trigger: "blur" }
    ],
    separation: [{ required: true, message: "Please enter the separator", trigger: "blur" }],
    latitude: [
      { required: true, message: "Please enter the latitude", trigger: "blur" }
    ],
    longitude: [
      { required: true, message: "Please enter the longitude", trigger: "blur" }
    ]
  };
  //是否新增指标
  private isAddPut: boolean = true;
  //是否新增GPS
  private isGpsAddPut: boolean = true;

  @Watch("gpsForm.singleField")
  onChangeValue(newVal: string, oldVal: string) {
    this.$nextTick(() => {
      (this as any).$refs["gps"].validate();
    });
  }

  //查询
  private onSubmit(refId: any) {
    this.page = 1
    this.getDataType = 'search'
    this.getList();
  }

  private async getList() {
    this.loading = true
    const data: any = await quota({
      page: this.page,
      pageSize: this.pageSize,
      ...this.formInline
    });
    if (this.getDataType === 'page'){
      this.tableData = [...this.tableData, ...data.items];
      this.getDataType = ''
    } else {
      this.tableData = data.items;
    }
    this.total = data.counts;
    this.loading = false
  }

 private handleCurrentChange() {
    if (!this.loading) {
      if (this.tableData.length < this.total) {
        this.page++ 
        this.getDataType = 'page'
        this.getList();
      }else {
        this.$message.success('It is already the last page!')
      }
    }
  }
  //添加指标提交
  private submitQuotaForm(refId: any) {
    (this as any).$refs[refId].validate(async (valid: boolean) => {
      if (valid == true) {
        let data: any = null
        if (this.isAddPut) {
          delete (this as any).form.id;
          data = await addQuota(this.form);
          if (data) {
            this.tableData.unshift(this.form)
            this.getList();
          }
        } else {
          data = await putQuota(this.form);
          if (data) {
            this.tableData[this.changeInd] = this.form
            this.getList();
          }
        }
        this.quotaDialogVisible = false;
      }
    });
  }

  //添加指标
  private addItem(refId: any) {
    this.quotaDialogVisible = true;
    this.isAddPut = true;
    this.title =  'add indicator'
    this.$nextTick(() => {
      (this as any).$refs["quota"].resetFields();
    });
  }

  //修改指标
  private changeItem(refId: any) {
    this.changeInd = refId
    this.quotaDialogVisible = true;
    this.title =  'change indicator'
    this.isAddPut = false;
    this.$nextTick(() => {
      this.form = Object.assign({}, this.tableData[refId]);
    });
  }

  //删除指标
  private async delItem(refId: any) {
    this.$confirm('confirm deletion?', '', {
      confirmButtonText: 'confirm',
      cancelButtonText: 'cancel',
    }).then(async () => {
       const data: any = await delQuota(refId);
    this.getList();
    }).catch((err:any) => {
      console.log('cancel operation!')
    })
  }

  //添加/修改GPS指标提交
  private submitGPSForm(refId: any) {
    (this as any).$refs[refId].validate(async (valid: boolean) => {
      if (valid == true) {
        if (this.isGpsAddPut) {
          const data: any = await addGps(this.gpsForm);
        } else {
          const data: any = await putGps(this.gpsForm);
        }
        this.GPSDialogVisible = false;
      }
    });
  }

  //添加GPS指标
  private async addGpsItem(refId: any) {
    this.GPSDialogVisible = true;
    const data: any = await gps();
    if (data.id) {
      this.$nextTick(() => {
        this.gpsForm = Object.assign({}, data);
      });
      this.isGpsAddPut = false;
    } else {
      this.$nextTick(() => {
        (this as any).$refs["gps"].resetFields();
      });
    }
  }

  private resetForm(refId: any) {
    (this as any).$refs[refId].resetFields();
    this.quotaDialogVisible = false;
    this.GPSDialogVisible = false;
  }

  mounted() {
    this.getList();
  }
}
</script>


<style lang="scss">
.equipment{
   &-container{
      .el-form-item__label{
        color: #666666;
      }
      .el-form--inline .el-form-item{
        width: 280px;
        .el-select {
          width: 100%;
        }
      }
      .el-form--inline .el-form-item__content {
        width: calc(100% - 70px);
      }
   }
} 
</style>
<style lang="scss" scoped>
.finerMark{
  &-container{
    background: #f3f4f7;
    padding: 28px 35px 20px 35px;
    .topContent{
      display: flex;
      justify-content: space-between;
      .seachBox{
        padding-left: 10px;
        margin-top: 20px;
        .searchBut{
          display: inline-block;
        position: relative;
        left: -10px;
          width: 89px;
          height: 40px;
          line-height: 38px;
          cursor: pointer;
          text-align: center;
          background: #ffffff;
          border: 1px solid #5584ff;
          border-radius: 4px;
          color: #5584ff;
        }
      }
      .addCont{
        display: flex;
        padding-left: 10px;
        font-size: 14px;
        margin-top: 20px;
        .iconfont{
          margin-right: 5px;
          position: relative;
          top:1px;
        }
      }
    }
    
    .contBox{
      display: flex;
      flex-wrap: wrap;
      align-content:flex-start;
      height: calc(100vh - 240px);
      padding-top: 20px;
      .item{
        position: relative;
        height: 120px;
        margin: 0px 10px 30px 10px;
        width: calc(20% - 20px);
        border-radius: 11px;
        background: #ffffff url(./../../assets/img/watermarkfingerItem.png) calc(100% - 10px) bottom no-repeat;
        background-size: 87px 74px;
        .long{
          display: inline-block;
          width: 74px;
          overflow: hidden;
          height: 30px;
        }
        .title{
          width: 100%;
          display: flex;
          justify-content: space-between;
          .tit{
            display: flex;
            position: relative;
            padding: 0 15px;
            width: 130px;
            height: 32px;
            line-height: 32px;
            overflow: hidden;
            color:#fff;
            background: linear-gradient(left, #6EC1FF, #5584FF);
            border-radius:0px 15px 15px 15px;
            .iconfont{
              margin-right: 10px;
            }
          }
          .del{
            display: none;
            color: #D8D8D8;
            position: relative;
            cursor: pointer;
            right: 5px;
            top: 5px;
          }
        }
        &:hover .del{
          display: block;
        }
        .cont{
          padding: 20px 20px 30px 20px;
          color: #333;
          line-height: 23px;
          span{
            color: #818693;
            font-size: 14px;
          }
        }
      }
      .colStyB{
        .title{
          .tit{
            background: linear-gradient(left, #FF5133, #B075FF);
          }
        }
      }
      .colStyC{
        .title{
          .tit{
            background: linear-gradient(left, #5CE2D1, #55C4FF);
          }
        }
      }
      .colStyD{
        .title{
          .tit{
            background: linear-gradient(left, #FF4E6D, #FF6941);
          }
        }
      }
      .colStyE{
        .title{
          .tit{
            background: linear-gradient(left, #FFB16E, #FF7955);
          }
        }
      }
      .colStyF{
        .title{
          .tit{
            background: linear-gradient(left, #45D282, #17CD99);
          }
        }
      }
      @media screen and(max-width:1366px){
    .item{
        width: calc(25% - 20px);
      }
  }
    }
    .pagination{
      text-align: right;
    }
    .butBox{
      width: 80px;
      height: 36px;
      border-radius: 4px;
    }
  }
}
</style>
