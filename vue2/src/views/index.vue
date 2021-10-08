<template>
  <div class="app-container home">
    <el-alert type="success" :center="true"><i class="el-icon-success"></i> 欢迎使用PayPal管理系统，您本次登录时间为2021年09月06日16点00分，登录IP：123.232.42.22</el-alert>
    
    
    <!-- 实时统计 -->
    <el-card class="box-card stats-card">
      <div slot="header" class="card-head">
        <h3>实时统计</h3>
        <div class="tip">
          统计时间：2020-03-05 11:22:22
        </div>
      </div>
      <el-row class="card-body">
    
        <el-col class="stats-col" :span="24" :md="8">
            <div class="stats-item">
              <p class="stats-value">
                780000
              </p>
              <h5>昨日销售总额（元）</h5>
            </div>
            <div class="stats-item">
              <p class="stats-value">
                300
              </p>
              <h5>昨日订单总数</h5>
            </div>  
        </el-col>
        
        <el-col class="stats-col" :span="24" :md="8">
            <div class="stats-item">
              <p class="stats-value">
                780000
              </p>
              <h5>昨日销售总额（元）</h5>
            </div>
            <div class="stats-item">
              <p class="stats-value">
                300
              </p>
              <h5>昨日订单总数</h5>
            </div>  
        </el-col>

        <el-col class="stats-col" :span="24" :md="8">
            <div class="stats-item">
              <p class="stats-value">
                780000
              </p>
              <h5>昨日销售总额（元）</h5>
            </div>
            <div class="stats-item">
              <p class="stats-value">
                300
              </p>
              <h5>昨日订单总数</h5>
            </div>  
        </el-col>
      </el-row>
    </el-card>

    <!-- 待办事项 -->
    <el-card class="box-card todolist-card">
      <div slot="header" class="card-head">
        <h3>待办事项</h3>

      </div>
      <el-row class="card-body">
    
        <el-col class="todolist-col" :span="12" :md="{span: '24-5'}">
          
            <p class="todolist-value">
              780000
            </p>
            <h5 class="todolist-name">待审核订单</h5>
            <el-button size="mini" type="text" icon="el-icon-edit" >处理</el-button> 
        </el-col>
        <el-col class="todolist-col"  :span="12" :md="{span: '24-5'}">
          
            <p class="todolist-value">
              780000
            </p>
            <h5 class="todolist-name">待审核订单</h5>
             <el-button size="mini" type="text" icon="el-icon-edit" >处理</el-button>
        </el-col>
        <el-col class="todolist-col" :span="12" :md="{span: '24-5'}">
          
            <p class="todolist-value">
              780000
            </p>
            <h5 class="todolist-name">待审核订单</h5>
             <el-button size="mini" type="text" icon="el-icon-edit" >处理</el-button>
        </el-col>
        <el-col class="todolist-col" :span="12" :md="{span: '24-5'}">
          
            <p class="todolist-value">
              780000
            </p>
            <h5 class="todolist-name">待审核订单</h5>
             <el-button size="mini" type="text" icon="el-icon-edit" >处理</el-button>
        </el-col>
        <el-col class="todolist-col" :span="12" :md="{span: '24-5'}">
          
            <p class="todolist-value">
              780000
            </p>
            <h5 class="todolist-name">待审核订单</h5>
             <el-button size="mini" type="text" icon="el-icon-edit" >处理</el-button>
        </el-col>
        
        
      </el-row>
    </el-card>

    <el-row>
      <el-col class="chart-box" :span="24" :md="11">
        <el-card class="box-card">
          <div slot="header" class="card-head">
            <h3>当月销售</h3>
          </div>

           <div ref="monthstats" class="echart-box" />
          
        </el-card>
      </el-col>
      <el-col :span="2" :md="2" class="middle-space"></el-col>

      <el-col class="chart-box" :span="24" :md="11">
        <el-card class="box-card">
          <div slot="header" class="card-head">
            <h3>昨日销售</h3>
          </div>

           <div ref="yesterdaystats" class="echart-box" />
          
        </el-card>  
      </el-col>
    </el-row>

  </div>
</template>

<script>
import echarts from "echarts";
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.6.0",
    };
  },
  computed:{

  },
  watch:{
    '$store.state.app.appSize': function(newAppSize){
      this.monthstats && this.monthstats.resize();
      this.yesterdaystats && this.yesterdaystats.resize();
    }
  },
  mounted(){
    this.monthstats = echarts.init(this.$refs.monthstats, "macarons");
    this.monthstats.setOption({
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b} : {c} ({d}%)",
      },
      series: [
        {
          name: "命令",
          type: "pie",
          roseType: "radius",
          radius: [15, 95],
          center: ["50%", "38%"],
          data:[{"name":"set","value":"27"},{"name":"keys","value":"2624"},{"name":"auth","value":"22"},{"name":"setex","value":"56681"},{"name":"del","value":"21316"},{"name":"info","value":"4063"},{"name":"get","value":"1137447"},{"name":"dbsize","value":"2021"}],
          animationEasing: "cubicInOut",
          animationDuration: 1000,
        },
      ],
    });

    this.monthstats.resize()


    this.yesterdaystats = echarts.init(this.$refs.yesterdaystats, 'macarons');
    this.yesterdaystats.setOption({
      tooltip: {
        trigger: "item",
        formatter: "{a} <br/>{b} : {c} ({d}%)",
      },
      series: [
        {
          name: "命令",
          type: "pie",
          roseType: "radius",
          radius: [15, 95],
          center: ["50%", "38%"],
          data:[{"name":"set","value":"27"},{"name":"keys","value":"2624"},{"name":"auth","value":"22"},{"name":"setex","value":"56681"},{"name":"del","value":"21316"},{"name":"info","value":"4063"},{"name":"get","value":"1137447"},{"name":"dbsize","value":"2021"}],
          animationEasing: "cubicInOut",
          animationDuration: 1000,
        },
      ],
    });

  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
  }
};
</script>

<style scoped lang="scss">

.box-card {
  margin-top:20px;
  .card-head{
    display: flex;
    align-items: center;
    h3{
      font-size:16px;
      padding:0;
      margin:5px 0;
    }
    .tip{
      margin-left: 20px;
      font-size:12px;
      color: $gray;
    }
  }

  .stats-col{
    display: flex;
    justify-content: center;
    .stats-item{
      width:50%;
      text-align: center;
      .stats-value{
        font-size: 24px;
        font-weight: bold;
      }

      h5{
        font-size:14px;
      }
    }
    
    @media ( min-width : 992px) {

      .stats-item{
        width:40%;
      }
      &:not(:first-child){
        border-left: 1px solid $light-gray;
      }
    }
  }

  .todolist-col{
    text-align: center;

    .todolist-value{
      font-size: 24px;
      font-weight: bold;
    }
  }

  .echart-box{
    width:100%;
    height:420px;
  }

   
}
@media ( min-width : 992px) {
    
  .middle-space{
    height:1px;
  }
}
</style>

