<script setup>
import AdminSidebar from '../components/AdminSidebar.vue'
import AdminHeader from '../components/AdminHeader.vue'
import StatCard from '../components/StatCard.vue'
import HeatmapPanel from '../components/HeatmapPanel.vue'
import LiveQueue from '../components/LiveQueue.vue'

</script>

<template>
  <div class="dashboard-layout">

    <!-- Sidebar -->
    <AdminSidebar />

    <!-- Main Content -->
    <main class="dashboard-content">

      <!-- Header -->
      <AdminHeader />

      <!-- Page Title -->
      <div class="page-heading">
        <div>
          <h1>CLENRO Executive Dashboard</h1>
          <p>Monitor city-wide waste management operations.</p>
        </div>
      </div>

      <!-- Statistics -->
      <section class="stats-grid">

        <StatCard
          label="TOTAL TICKETS"
          value="1,240"
          description="All reported incidents"
          color="green"
        />

        <StatCard
          label="PENDING"
          value="142"
          description="Awaiting action"
          color="yellow"
        />

        <StatCard
          label="ASSIGNED"
          value="85"
          description="Currently assigned"
          color="blue"
        />

        <StatCard
          label="RESOLVED"
          value="1,013"
          description="Successfully resolved"
          color="green"
        />

      </section>

      <div class="dashboard-grid">

  <HeatmapPanel />

  <LiveQueue />

</div>

    </main>

  </div>
</template>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;

  background: #f5f7fa;
}

.dashboard-content {
  flex: 1;
  min-width: 0;

  padding: 0 30px 30px;
}

/* Page heading */

.page-heading {
  margin-top: 15px;
  margin-bottom: 18px;
}

.page-heading h1 {
  margin: 0;

  font-size: 22px;
  font-weight: 700;

  color: #182230;
}

.page-heading p {
  margin: 5px 0 0;

  font-size: 11px;
  color: #667085;
}

/* Statistics */

.stats-grid {
  display: flex;
  gap: 14px;
}

/* Responsive */

@media (max-width: 900px) {
  .stats-grid {
    flex-wrap: wrap;
  }

  .stat-card {
    min-width: calc(50% - 7px);
  }
}

@media (max-width: 600px) {
  .dashboard-content {
    padding: 0 16px 20px;
  }

  .search-box {
    width: 200px;
  }

  .stats-grid {
    flex-direction: column;
  }
}
.dashboard-grid {
  display: grid;
  grid-template-columns: minmax(0, 2fr) minmax(280px, 1fr);

  gap: 14px;

  margin-top: 14px;
}

@media (max-width: 900px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}
</style>