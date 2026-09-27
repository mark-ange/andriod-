<template>
  <section class="queue-panel">

    <div class="queue-header">
      <div>
        <h2>Live Queue</h2>
        <p>Tickets requiring attention.</p>
      </div>

      <button class="filter-button">☰</button>
    </div>

    <div class="queue-list">

      <div class="queue-item">
        <div class="ticket-top">
          <span>#TCK-0921</span>
          <span class="status pending">PENDING</span>
        </div>

        <strong>Illegal Dumping</strong>
        <small>Brgy. Carmen, Purok 4</small>

        <button class="assign-button">
          Assign Team
        </button>
      </div>

      <div class="queue-item">
        <div class="ticket-top">
          <span>#TCK-0920</span>
          <span class="status assigned">ASSIGNED</span>
        </div>

        <strong>Blocked Drainage</strong>
        <small>Brgy. Lapasan, Purok 1</small>

        <button class="view-button">
          View Status
        </button>
      </div>

      <div class="queue-item">
        <div class="ticket-top">
          <span>#TCK-0919</span>
          <span class="status pending">PENDING</span>
        </div>

        <strong>Waste Collection</strong>
        <small>Brgy. Macasandig, Zone 5</small>

        <button class="assign-button">
          Assign Team
        </button>
      </div>

    </div>

  </section>
</template>

<style scoped>
.queue-panel {
  background: #ffffff;

  border: 1px solid #e1e6eb;
  border-radius: 7px;

  overflow: hidden;
}

.queue-header {
  min-height: 62px;
  padding: 13px 15px;

  display: flex;
  justify-content: space-between;
  align-items: center;

  border-bottom: 1px solid #edf0f2;
}

.queue-header h2 {
  margin: 0;

  font-size: 14px;
  color: #182230;
}

.queue-header p {
  margin: 4px 0 0;

  font-size: 10px;
  color: #98a2b3;
}

.filter-button {
  border: none;
  background: transparent;

  color: #667085;

  cursor: pointer;
}

/* Queue */

.queue-list {
  padding: 8px 12px;
}

.queue-item {
  padding: 13px 2px;

  border-bottom: 1px solid #edf0f2;
}

.queue-item:last-child {
  border-bottom: none;
}

.ticket-top {
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-bottom: 7px;

  font-size: 9px;
  color: #667085;
}

.queue-item strong {
  display: block;

  font-size: 11px;
  color: #344054;
}

.queue-item small {
  display: block;

  margin-top: 3px;

  font-size: 9px;
  color: #98a2b3;
}

/* Status */

.status {
  padding: 3px 6px;

  border-radius: 10px;

  font-size: 7px;
  font-weight: 700;
}

.pending {
  background: #fff3cd;
  color: #a66b00;
}

.assigned {
  background: #e5efff;
  color: #2769aa;
}

/* Buttons */

.assign-button,
.view-button {
  width: 100%;
  height: 27px;

  margin-top: 9px;

  border-radius: 4px;

  font-size: 9px;
  font-weight: 600;

  cursor: pointer;
}

.assign-button {
  border: none;
  background: #278c37;
  color: white;
}

.view-button {
  border: 1px solid #278c37;
  background: white;
  color: #278c37;
}
</style>