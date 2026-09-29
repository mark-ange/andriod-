<template>
  <div class="reports-page">

    <!-- ========================= -->
    <!-- PAGE HEADER -->
    <!-- ========================= -->

    <div class="page-header">

      <div>
        <h1>Analytics & Audit Reports</h1>
        <p>
          Comprehensive performance metrics and operational
          analytics for CLENRO.
        </p>
      </div>

      <button
        type="button"
        class="generate-btn"
        @click="generateReport"
      >
        ↓ Generate Monthly CLENRO Report (PDF)
      </button>

    </div>


    <!-- ========================= -->
    <!-- KPI CARDS -->
    <!-- ========================= -->

    <section class="kpi-grid">

      <div class="kpi-card">

        <div class="kpi-top">
          <span>Total Reports</span>
          <span class="kpi-icon">▣</span>
        </div>

        <strong>1,248</strong>

        <small>
          ↑ 7.2% from last month
        </small>

      </div>


      <div class="kpi-card">

        <div class="kpi-top">
          <span>Avg. Resolution Time</span>
          <span class="kpi-icon">◷</span>
        </div>

        <strong>4.2 <small class="unit">hrs</small></strong>

        <small>
          ↓ 12% from last month
        </small>

      </div>


      <div class="kpi-card">

        <div class="kpi-top">
          <span>Resolution Rate</span>
          <span class="kpi-icon">✓</span>
        </div>

        <strong>92%</strong>

        <small>
          1,148 / 1,248 resolved
        </small>

      </div>

    </section>


    <!-- ========================= -->
    <!-- CHART ROW -->
    <!-- ========================= -->

    <section class="chart-grid">


      <!-- BAR CHART -->

      <div class="chart-card">

        <div class="chart-header">

          <div>
            <h2>Reports per Barangay</h2>
            <p>Top 5 barangays by number of reports.</p>
          </div>

        </div>


        <div class="bar-chart">

          <div
            v-for="item in barangayReports"
            :key="item.name"
            class="bar-group"
          >

            <div class="bar-value">
              {{ item.value }}
            </div>

            <div
              class="bar"
              :style="{ height: `${item.height}%` }"
            ></div>

            <span>
              {{ item.name }}
            </span>

          </div>

        </div>

      </div>


      <!-- DONUT CHART -->

      <div class="chart-card">

        <div class="chart-header">

          <div>
            <h2>Incident Categories</h2>
            <p>Distribution of reported incidents.</p>
          </div>

        </div>


        <div class="donut-layout">

          <div
            class="donut"
            aria-label="Incident category distribution"
          >
            <div class="donut-center">
              <strong>1.2k</strong>
              <span>Total</span>
            </div>
          </div>


          <div class="legend-list">

            <div
              v-for="item in categories"
              :key="item.name"
              class="legend-item"
            >

              <span
                class="legend-dot"
                :class="item.className"
              ></span>

              <span class="legend-name">
                {{ item.name }}
              </span>

              <strong>
                {{ item.percent }}%
              </strong>

            </div>

          </div>

        </div>

      </div>

    </section>


    <!-- ========================= -->
    <!-- LINE CHART -->
    <!-- ========================= -->

    <section class="chart-card line-chart-card">

      <div class="chart-header">

        <div>
          <h2>Average Response Time (Last 7 Days)</h2>
          <p>
            Trending resolution speed across operating barangays.
          </p>
        </div>

      </div>


      <div class="line-chart">

        <!-- Grid -->

        <div class="grid-line line-1">
          <span>8h</span>
        </div>

        <div class="grid-line line-2">
          <span>6h</span>
        </div>

        <div class="grid-line line-3">
          <span>4h</span>
        </div>

        <div class="grid-line line-4">
          <span>2h</span>
        </div>

        <div class="grid-line line-5">
          <span>0h</span>
        </div>


        <!-- SVG Line -->

        <svg
          viewBox="0 0 700 260"
          preserveAspectRatio="none"
          class="line-svg"
          aria-label="Average response time chart"
        >

          <defs>

            <linearGradient
              id="areaGradient"
              x1="0"
              y1="0"
              x2="0"
              y2="1"
            >
              <stop
                offset="0%"
                stop-opacity=".18"
              />

              <stop
                offset="100%"
                stop-opacity="0"
              />

            </linearGradient>

          </defs>


          <!-- Area -->

          <polygon
            :points="areaPoints"
            fill="url(#areaGradient)"
          />


          <!-- Line -->

          <polyline
            :points="linePoints"
            fill="none"
            stroke="#278c39"
            stroke-width="3"
            stroke-linecap="round"
            stroke-linejoin="round"
          />


          <!-- Points -->

          <circle
            v-for="point in chartPoints"
            :key="point.label"
            :cx="point.x"
            :cy="point.y"
            r="5"
            fill="#ffffff"
            stroke="#278c39"
            stroke-width="3"
          />

        </svg>


        <!-- X labels -->

        <div class="x-axis">

          <span
            v-for="point in chartPoints"
            :key="point.label"
          >
            {{ point.label }}
          </span>

        </div>

      </div>

    </section>


    <!-- ========================= -->
    <!-- REPORT INFO -->
    <!-- ========================= -->

    <section class="info-card">

      <div>
        <strong>Report Period</strong>
        <span>Current Month</span>
      </div>

      <div>
        <strong>Data Source</strong>
        <span>CityCare Incident Records</span>
      </div>

      <div>
        <strong>Last Updated</strong>
        <span>Today</span>
      </div>

    </section>

  </div>
</template>


<script setup>

import {
  computed,
  ref
} from "vue"


/* =================================
   BAR CHART DATA
================================= */

const barangayReports = ref([

  {
    name: "Carmen",
    value: 240,
    height: 100
  },

  {
    name: "Lapasan",
    value: 185,
    height: 77
  },

  {
    name: "Balulang",
    value: 128,
    height: 53
  },

  {
    name: "Kauswagan",
    value: 96,
    height: 40
  },

  {
    name: "Macasandig",
    value: 72,
    height: 30
  }

])


/* =================================
   INCIDENT CATEGORIES
================================= */

const categories = [

  {
    name: "Overflowing Bin",
    percent: 45,
    className: "green"
  },

  {
    name: "Illegal Dump",
    percent: 30,
    className: "blue"
  },

  {
    name: "Missed Pickup",
    percent: 15,
    className: "yellow"
  },

  {
    name: "Other",
    percent: 10,
    className: "gray"
  }

]


/* =================================
   LINE CHART DATA
================================= */

const responseTimes = [

  {
    label: "Mon",
    value: 3.4
  },

  {
    label: "Tue",
    value: 2.7
  },

  {
    label: "Wed",
    value: 4.2
  },

  {
    label: "Thu",
    value: 3.8
  },

  {
    label: "Fri",
    value: 5.1
  },

  {
    label: "Sat",
    value: 4.6
  },

  {
    label: "Sun",
    value: 5.8
  }

]


/* =================================
   CHART POINTS
================================= */

const chartPoints = computed(() => {

  const width = 700
  const height = 220

  const left = 20
  const right = 20
  const top = 20
  const bottom = 10

  const maxValue = 8

  const usableWidth =
    width - left - right

  const usableHeight =
    height - top - bottom


  return responseTimes.map(
    (item, index) => {

      const x =
        left +
        (
          index /
          (responseTimes.length - 1)
        ) *
        usableWidth


      const y =
        top +
        (
          1 -
          item.value / maxValue
        ) *
        usableHeight


      return {
        label: item.label,
        x,
        y,
        value: item.value
      }

    }
  )

})


/* =================================
   LINE POINTS
================================= */

const linePoints = computed(() => {

  return chartPoints.value
    .map(
      point =>
        `${point.x},${point.y}`
    )
    .join(" ")

})


/* =================================
   AREA POINTS
================================= */

const areaPoints = computed(() => {

  if (!chartPoints.value.length) {
    return ""
  }


  const first =
    chartPoints.value[0]

  const last =
    chartPoints.value[
      chartPoints.value.length - 1
    ]


  return [

    `${first.x},230`,

    ...chartPoints.value.map(
      point =>
        `${point.x},${point.y}`
    ),

    `${last.x},230`

  ].join(" ")

})


/* =================================
   GENERATE REPORT
================================= */

function generateReport() {

  alert(
    "Monthly CLENRO report generation will be connected to the backend later."
  )

}

</script>


<style scoped>

/* =================================
   PAGE
================================= */

.reports-page {

  padding: 32px;

  color: #17233b;

}


/* =================================
   HEADER
================================= */

.page-header {

  display: flex;

  justify-content: space-between;

  align-items: flex-start;

  gap: 20px;

  margin-bottom: 22px;

}


.page-header h1 {

  margin: 0;

  font-size: 28px;

}


.page-header p {

  margin: 6px 0 0;

  color: #7b8798;

  font-size: 13px;

}


.generate-btn {

  border: 0;

  border-radius: 7px;

  padding: 11px 15px;

  background: #278c39;

  color: white;

  font-size: 12px;

  font-weight: 700;

  cursor: pointer;

}


.generate-btn:hover {

  background: #217831;

}


/* =================================
   KPI
================================= */

.kpi-grid {

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 14px;

  margin-bottom: 14px;

}


.kpi-card {

  padding: 18px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

}


.kpi-top {

  display: flex;

  justify-content: space-between;

  align-items: center;

}


.kpi-top > span:first-child {

  font-size: 11px;

  font-weight: 700;

  color: #68758a;

}


.kpi-icon {

  color: #2e8b3c;

  font-size: 15px;

}


.kpi-card > strong {

  display: block;

  margin-top: 10px;

  font-size: 31px;

  color: #17233b;

}


.kpi-card > small {

  display: block;

  margin-top: 5px;

  color: #7b8798;

  font-size: 10px;

}


.unit {

  font-size: 15px;

  font-weight: 500;

}


/* =================================
   CHART GRID
================================= */

.chart-grid {

  display: grid;

  grid-template-columns:
    minmax(0, 1.65fr)
    minmax(320px, 1fr);

  gap: 14px;

  margin-bottom: 14px;

}


/* =================================
   CHART CARD
================================= */

.chart-card {

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

  overflow: hidden;

}


.chart-header {

  padding: 16px 17px;

  border-bottom: 1px solid #edf0f4;

}


.chart-header h2 {

  margin: 0;

  font-size: 15px;

  color: #17233b;

}


.chart-header p {

  margin: 4px 0 0;

  color: #8a94a5;

  font-size: 10px;

}


/* =================================
   BAR CHART
================================= */

.bar-chart {

  height: 290px;

  display: flex;

  align-items: flex-end;

  justify-content: space-around;

  gap: 12px;

  padding: 24px 22px 18px;

  box-sizing: border-box;

}


.bar-group {

  flex: 1;

  height: 100%;

  max-width: 90px;

  display: flex;

  flex-direction: column;

  justify-content: flex-end;

  align-items: center;

}


.bar-value {

  margin-bottom: 5px;

  color: #526078;

  font-size: 10px;

  font-weight: 600;

}


.bar {

  width: 55px;

  max-width: 100%;

  min-height: 15px;

  background: #278c39;

  border-radius:
    3px 3px 0 0;

}


.bar-group > span {

  margin-top: 9px;

  color: #68758a;

  font-size: 9px;

  text-align: center;

}


/* =================================
   DONUT
================================= */

.donut-layout {

  min-height: 290px;

  display: flex;

  align-items: center;

  justify-content: center;

  gap: 28px;

  padding: 24px;

  box-sizing: border-box;

}


.donut {

  width: 155px;

  height: 155px;

  flex: 0 0 auto;

  display: flex;

  align-items: center;

  justify-content: center;

  border-radius: 50%;

  background:

    conic-gradient(

      #278c39 0deg 162deg,

      #3686d9 162deg 270deg,

      #f2b72b 270deg 324deg,

      #98a2b3 324deg 360deg

    );

}


.donut::before {

  content: "";

  position: absolute;

}


.donut-center {

  width: 92px;

  height: 92px;

  display: flex;

  flex-direction: column;

  align-items: center;

  justify-content: center;

  background: white;

  border-radius: 50%;

}


.donut-center strong {

  font-size: 25px;

  color: #17233b;

}


.donut-center span {

  margin-top: 2px;

  font-size: 10px;

  color: #8a94a5;

}


/* =================================
   LEGEND
================================= */

.legend-list {

  min-width: 145px;

  display: flex;

  flex-direction: column;

  gap: 13px;

}


.legend-item {

  display: grid;

  grid-template-columns:
    9px 1fr auto;

  align-items: center;

  gap: 7px;

  font-size: 10px;

}


.legend-dot {

  width: 8px;

  height: 8px;

  border-radius: 50%;

}


.legend-dot.green {

  background: #278c39;

}


.legend-dot.blue {

  background: #3686d9;

}


.legend-dot.yellow {

  background: #f2b72b;

}


.legend-dot.gray {

  background: #98a2b3;

}


.legend-name {

  color: #526078;

}


/* =================================
   LINE CHART
================================= */

.line-chart-card {

  margin-bottom: 14px;

}


.line-chart {

  position: relative;

  height: 310px;

  margin: 0 18px;

  padding-left: 35px;

  box-sizing: border-box;

}


.grid-line {

  position: absolute;

  left: 35px;

  right: 10px;

  border-top: 1px solid #edf0f4;

}


.grid-line span {

  position: absolute;

  left: -28px;

  top: -7px;

  font-size: 9px;

  color: #98a2b3;

}


.line-1 {
  top: 25px;
}

.line-2 {
  top: 78px;
}

.line-3 {
  top: 131px;
}

.line-4 {
  top: 184px;
}

.line-5 {
  top: 237px;
}


.line-svg {

  position: absolute;

  top: 10px;

  left: 35px;

  right: 5px;

  width: calc(100% - 40px);

  height: 240px;

  overflow: visible;

}


/* =================================
   X AXIS
================================= */

.x-axis {

  position: absolute;

  left: 35px;

  right: 5px;

  bottom: 24px;

  display: flex;

  justify-content: space-between;

  color: #8a94a5;

  font-size: 9px;

}


/* =================================
   INFO
================================= */

.info-card {

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 14px;

  padding: 16px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

}


.info-card div {

  display: flex;

  flex-direction: column;

  gap: 4px;

}


.info-card strong {

  color: #526078;

  font-size: 10px;

  text-transform: uppercase;

}


.info-card span {

  color: #17233b;

  font-size: 12px;

}


/* =================================
   RESPONSIVE
================================= */

@media (max-width: 1000px) {

  .chart-grid {

    grid-template-columns: 1fr;

  }

}


@media (max-width: 800px) {

  .reports-page {

    padding: 20px;

  }


  .page-header {

    flex-direction: column;

  }


  .kpi-grid {

    grid-template-columns: 1fr;

  }


  .info-card {

    grid-template-columns: 1fr;

  }

}


@media (max-width: 600px) {

  .donut-layout {

    flex-direction: column;

  }

  .legend-list {

    width: 100%;

  }

}

</style>