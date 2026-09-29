<template>
  <div class="barangays-page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1>Barangay Management</h1>
        <p>Monitor incidents and service activity across barangays.</p>
      </div>

      <button
        type="button"
        class="add-btn"
        @click="openAddForm"
      >
        + Add Barangay
      </button>
    </div>


    <!-- Summary Cards -->
    <section class="summary-grid">

      <div class="summary-card">
        <span class="summary-label">TOTAL BARANGAYS</span>
        <strong>{{ barangays.length }}</strong>
        <small>Registered in the system</small>
      </div>

      <div class="summary-card">
        <span class="summary-label">ACTIVE INCIDENTS</span>
        <strong>{{ activeIncidents }}</strong>
        <small>Pending or in progress</small>
      </div>

      <div class="summary-card">
        <span class="summary-label">RESOLVED THIS MONTH</span>
        <strong>{{ resolvedIncidents }}</strong>
        <small>Successfully resolved</small>
      </div>

    </section>


    <!-- Barangay List -->
    <section class="table-card">

      <div class="section-header">

        <div>
          <h2>Barangay Overview</h2>
          <p>Incident activity by barangay.</p>
        </div>

        <input
          v-model="search"
          type="search"
          placeholder="Search barangay..."
        />

      </div>


      <div class="table-wrapper">

        <table>

          <thead>
            <tr>
              <th>BARANGAY</th>
              <th>DISTRICT</th>
              <th>ACTIVE INCIDENTS</th>
              <th>RESOLVED</th>
              <th>TOTAL REPORTS</th>
              <th>STATUS</th>
              <th>ACTIONS</th>
            </tr>
          </thead>

          <tbody>

            <tr
              v-for="barangay in filteredBarangays"
              :key="barangay.id"
            >

              <td class="barangay-name">
                {{ barangay.name }}
              </td>

              <td>
                {{ barangay.district }}
              </td>

              <td>
                <span
                  class="incident-count"
                  :class="{
                    warning: barangay.active > 5,
                    normal: barangay.active <= 5
                  }"
                >
                  {{ barangay.active }}
                </span>
              </td>

              <td>
                {{ barangay.resolved }}
              </td>

              <td>
                {{ barangay.total }}
              </td>

              <td>
                <span
                  class="status"
                  :class="barangay.status.toLowerCase()"
                >
                  {{ barangay.status }}
                </span>
              </td>

              <td>

                <button
                  type="button"
                  class="view-btn"
                  @click="viewBarangay(barangay)"
                >
                  View
                </button>

              </td>

            </tr>

            <tr v-if="filteredBarangays.length === 0">
              <td colspan="7" class="empty-state">
                No barangays found.
              </td>
            </tr>

          </tbody>

        </table>

      </div>


      <div class="table-footer">
        <span>
          Showing {{ filteredBarangays.length }}
          of {{ barangays.length }} barangays
        </span>
      </div>

    </section>


    <!-- Barangay Details Drawer -->
    <div
      v-if="selectedBarangay"
      class="overlay"
      @click.self="closeBarangay"
    >

      <aside class="details-drawer">

        <div class="drawer-header">

          <div>
            <span>BARANGAY DETAILS</span>
            <h2>{{ selectedBarangay.name }}</h2>
          </div>

          <button
            type="button"
            class="close-btn"
            @click="closeBarangay"
          >
            ×
          </button>

        </div>


        <div class="drawer-body">

          <div class="status-row">

            <span
              class="status"
              :class="selectedBarangay.status.toLowerCase()"
            >
              {{ selectedBarangay.status }}
            </span>

            <span>
              {{ selectedBarangay.district }}
            </span>

          </div>


          <div class="detail-grid">

            <div>
              <span>Active Incidents</span>
              <strong>{{ selectedBarangay.active }}</strong>
            </div>

            <div>
              <span>Resolved</span>
              <strong>{{ selectedBarangay.resolved }}</strong>
            </div>

            <div>
              <span>Total Reports</span>
              <strong>{{ selectedBarangay.total }}</strong>
            </div>

            <div>
              <span>Assigned Teams</span>
              <strong>{{ selectedBarangay.teams }}</strong>
            </div>

          </div>


          <div class="activity-section">

            <h3>Recent Activity</h3>

            <div class="activity-item">
              <span class="activity-dot pending"></span>
              <div>
                <strong>Waste Collection</strong>
                <small>2 active reports</small>
              </div>
            </div>

            <div class="activity-item">
              <span class="activity-dot resolved"></span>
              <div>
                <strong>Road Repair</strong>
                <small>Recently resolved</small>
              </div>
            </div>

            <div class="activity-item">
              <span class="activity-dot assigned"></span>
              <div>
                <strong>Street Lighting</strong>
                <small>Team assigned</small>
              </div>
            </div>

          </div>

        </div>


        <div class="drawer-footer">

          <button
            type="button"
            class="secondary-btn"
            @click="closeBarangay"
          >
            Close
          </button>

        </div>

      </aside>

    </div>


    <!-- Add Barangay Modal -->
    <div
      v-if="showAddForm"
      class="overlay"
      @click.self="closeAddForm"
    >

      <section class="add-modal">

        <div class="drawer-header">

          <div>
            <span>BARANGAY MANAGEMENT</span>
            <h2>Add Barangay</h2>
          </div>

          <button
            type="button"
            class="close-btn"
            @click="closeAddForm"
          >
            ×
          </button>

        </div>


        <form @submit.prevent="addBarangay">

          <label>
            Barangay Name

            <input
              v-model="newBarangay.name"
              type="text"
              placeholder="Carmen"
              required
            />
          </label>


          <label>
            District

            <select v-model="newBarangay.district">
              <option>District 1</option>
              <option>District 2</option>
              <option>District 3</option>
            </select>
          </label>


          <div class="form-footer">

            <button
              type="button"
              class="secondary-btn"
              @click="closeAddForm"
            >
              Cancel
            </button>

            <button
              type="submit"
              class="primary-btn"
            >
              Add Barangay
            </button>

          </div>

        </form>

      </section>

    </div>

  </div>
</template>


<script setup>

import {
  computed,
  ref
} from "vue"


/* =================================
   STATE
================================= */

const search = ref("")

const selectedBarangay = ref(null)

const showAddForm = ref(false)


/* =================================
   NEW BARANGAY
================================= */

const newBarangay = ref({
  name: "",
  district: "District 1"
})


/* =================================
   BARANGAY DATA
================================= */

const barangays = ref([

  {
    id: 1,
    name: "Carmen",
    district: "District 1",
    active: 8,
    resolved: 42,
    total: 50,
    teams: 3,
    status: "Active"
  },

  {
    id: 2,
    name: "Lapasan",
    district: "District 1",
    active: 5,
    resolved: 37,
    total: 42,
    teams: 2,
    status: "Active"
  },

  {
    id: 3,
    name: "Macasandig",
    district: "District 1",
    active: 6,
    resolved: 31,
    total: 37,
    teams: 2,
    status: "Active"
  },

  {
    id: 4,
    name: "Balulang",
    district: "District 2",
    active: 4,
    resolved: 34,
    total: 38,
    teams: 2,
    status: "Active"
  },

  {
    id: 5,
    name: "Lumbia",
    district: "District 3",
    active: 3,
    resolved: 29,
    total: 32,
    teams: 1,
    status: "Active"
  },

  {
    id: 6,
    name: "Bugo",
    district: "District 1",
    active: 2,
    resolved: 25,
    total: 27,
    teams: 1,
    status: "Active"
  }

])


/* =================================
   SUMMARY
================================= */

const activeIncidents = computed(() => {

  return barangays.value.reduce(
    (total, barangay) =>
      total + barangay.active,
    0
  )

})


const resolvedIncidents = computed(() => {

  return barangays.value.reduce(
    (total, barangay) =>
      total + barangay.resolved,
    0
  )

})


/* =================================
   SEARCH
================================= */

const filteredBarangays = computed(() => {

  const keyword =
    search.value
      .toLowerCase()
      .trim()

  if (!keyword) {
    return barangays.value
  }

  return barangays.value.filter(
    barangay =>
      barangay.name
        .toLowerCase()
        .includes(keyword) ||
      barangay.district
        .toLowerCase()
        .includes(keyword)
  )

})


/* =================================
   VIEW
================================= */

function viewBarangay(barangay) {

  selectedBarangay.value = barangay

}


/* =================================
   CLOSE
================================= */

function closeBarangay() {

  selectedBarangay.value = null

}


/* =================================
   ADD
================================= */

function openAddForm() {

  newBarangay.value = {
    name: "",
    district: "District 1"
  }

  showAddForm.value = true

}


function closeAddForm() {

  showAddForm.value = false

}


function addBarangay() {

  barangays.value.push({

    id: Date.now(),

    name: newBarangay.value.name,

    district: newBarangay.value.district,

    active: 0,

    resolved: 0,

    total: 0,

    teams: 0,

    status: "Active"

  })

  closeAddForm()

}

</script>


<style scoped>

/* =================================
   PAGE
================================= */

.barangays-page {

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

  margin-bottom: 24px;

}


.page-header h1 {

  margin: 0;

  font-size: 28px;

}


.page-header p {

  margin: 6px 0 0;

  color: #7b8798;

}


.add-btn {

  border: 0;

  background: #278c39;

  color: white;

  padding: 11px 16px;

  border-radius: 7px;

  font-size: 13px;

  font-weight: 600;

  cursor: pointer;

}


.add-btn:hover {

  background: #217831;

}


/* =================================
   SUMMARY
================================= */

.summary-grid {

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 14px;

  margin-bottom: 14px;

}


.summary-card {

  padding: 17px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

}


.summary-label {

  display: flex;

  justify-content: space-between;

  color: #68758a;

  font-size: 11px;

  font-weight: 700;

}


.summary-card strong {

  display: block;

  margin-top: 10px;

  font-size: 28px;

  color: #17233b;

}


.summary-card small {

  display: block;

  margin-top: 5px;

  color: #98a2b3;

  font-size: 10px;

}


/* =================================
   TABLE CARD
================================= */

.table-card {

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

  overflow: hidden;

}


.section-header {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 17px;

  border-bottom: 1px solid #edf0f4;

}


.section-header h2 {

  margin: 0;

  font-size: 16px;

}


.section-header p {

  margin: 4px 0 0;

  font-size: 10px;

  color: #8a94a5;

}


.section-header input {

  width: 210px;

  padding: 9px 10px;

  box-sizing: border-box;

  border: 1px solid #d8dee8;

  border-radius: 6px;

  outline: none;

}


.table-wrapper {

  overflow-x: auto;

}


/* =================================
   TABLE
================================= */

table {

  width: 100%;

  border-collapse: collapse;

}


th {

  background: #f8fafc;

  color: #68758a;

  font-size: 10px;

  text-align: left;

  padding: 13px 15px;

}


td {

  padding: 14px 15px;

  border-top: 1px solid #edf0f4;

  font-size: 12px;

}


.barangay-name {

  font-weight: 700;

  color: #17233b;

}


.incident-count {

  display: inline-block;

  min-width: 28px;

  padding: 5px 8px;

  text-align: center;

  border-radius: 15px;

  font-size: 10px;

  font-weight: 700;

}


.incident-count.normal {

  background: #e0f2e4;

  color: #217831;

}


.incident-count.warning {

  background: #fff2c9;

  color: #9b7200;

}


/* =================================
   STATUS
================================= */

.status {

  display: inline-block;

  padding: 5px 9px;

  border-radius: 20px;

  font-size: 9px;

  font-weight: 700;

}


.status.active {

  background: #e0f2e4;

  color: #217831;

}


/* =================================
   VIEW
================================= */

.view-btn {

  padding: 7px 13px;

  border: 1px solid #2d943d;

  background: white;

  color: #248638;

  border-radius: 6px;

  cursor: pointer;

}


.view-btn:hover {

  background: #f0f9f2;

}


/* =================================
   FOOTER
================================= */

.table-footer {

  padding: 14px 15px;

  color: #7c8798;

  font-size: 11px;

}


/* =================================
   OVERLAY / DRAWER
================================= */

.overlay {

  position: fixed;

  inset: 0;

  z-index: 1000;

  background: rgba(15, 23, 42, .38);

}


.details-drawer {

  position: absolute;

  top: 0;

  right: 0;

  width: min(470px, 100%);

  height: 100%;

  display: flex;

  flex-direction: column;

  background: white;

  box-shadow:
    -15px 0 40px rgba(15, 23, 42, .16);

}


/* =================================
   DRAWER
================================= */

.drawer-header {

  display: flex;

  justify-content: space-between;

  align-items: flex-start;

  padding: 21px 23px;

  border-bottom: 1px solid #edf0f4;

}


.drawer-header > div > span {

  display: block;

  margin-bottom: 5px;

  color: #7c8798;

  font-size: 10px;

  font-weight: 700;

  letter-spacing: .07em;

}


.drawer-header h2 {

  margin: 0;

  color: #17233b;

  font-size: 21px;

}


.close-btn {

  border: 0;

  background: transparent;

  color: #7c8798;

  font-size: 27px;

  cursor: pointer;

}


/* =================================
   DRAWER BODY
================================= */

.drawer-body {

  flex: 1;

  overflow-y: auto;

  padding: 22px 23px;

}


.status-row {

  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 25px;

  color: #667085;

  font-size: 12px;

}


.detail-grid {

  display: grid;

  grid-template-columns: 1fr 1fr;

  gap: 18px;

}


.detail-grid div {

  display: flex;

  flex-direction: column;

  gap: 5px;

}


.detail-grid span {

  color: #8a94a5;

  font-size: 11px;

}


.detail-grid strong {

  color: #17233b;

  font-size: 15px;

}


/* =================================
   ACTIVITY
================================= */

.activity-section {

  margin-top: 28px;

}


.activity-section h3 {

  margin: 0 0 14px;

  font-size: 13px;

  text-transform: uppercase;

  color: #526078;

}


.activity-item {

  display: flex;

  align-items: center;

  gap: 10px;

  padding: 12px 0;

  border-bottom: 1px solid #edf0f4;

}


.activity-dot {

  width: 9px;

  height: 9px;

  border-radius: 50%;

}


.activity-dot.pending {

  background: #f2b72b;

}


.activity-dot.resolved {

  background: #2e8b3c;

}


.activity-dot.assigned {

  background: #3686d9;

}


.activity-item strong {

  display: block;

  font-size: 12px;

  color: #344054;

}


.activity-item small {

  display: block;

  margin-top: 2px;

  font-size: 10px;

  color: #8a94a5;

}


/* =================================
   DRAWER FOOTER
================================= */

.drawer-footer {

  display: flex;

  justify-content: flex-end;

  padding: 15px 23px;

  border-top: 1px solid #edf0f4;

  background: #f8fafc;

}


/* =================================
   ADD MODAL
================================= */

.add-modal {

  width: min(480px, calc(100% - 40px));

  position: absolute;

  top: 50%;

  left: 50%;

  transform: translate(-50%, -50%);

  background: white;

  border-radius: 10px;

  overflow: hidden;

  box-shadow:
    0 20px 50px rgba(15, 23, 42, .2);

}


.add-modal form {

  padding: 22px;

}


.add-modal label {

  display: flex;

  flex-direction: column;

  gap: 6px;

  margin-bottom: 16px;

  color: #667085;

  font-size: 12px;

  font-weight: 600;

}


.add-modal input,
.add-modal select {

  height: 40px;

  padding: 0 10px;

  border: 1px solid #d8dee8;

  border-radius: 6px;

  outline: none;

}


.form-footer {

  display: flex;

  justify-content: flex-end;

  gap: 9px;

  margin-top: 20px;

  padding-top: 16px;

  border-top: 1px solid #edf0f4;

}


/* =================================
   BUTTONS
================================= */

.secondary-btn,
.primary-btn {

  padding: 9px 16px;

  border-radius: 6px;

  font-size: 12px;

  font-weight: 600;

  cursor: pointer;

}


.secondary-btn {

  border: 1px solid #d8dee8;

  background: white;

  color: #526078;

}


.primary-btn {

  border: 1px solid #278c39;

  background: #278c39;

  color: white;

}


/* =================================
   EMPTY
================================= */

.empty-state {

  text-align: center;

  padding: 35px;

  color: #8a94a5;

}


/* =================================
   RESPONSIVE
================================= */

@media (max-width: 800px) {

  .barangays-page {

    padding: 20px;

  }

  .summary-grid {

    grid-template-columns: 1fr;

  }

  .page-header {

    flex-direction: column;

    gap: 15px;

  }

  .section-header {

    flex-direction: column;

    align-items: flex-start;

    gap: 12px;

  }

  .section-header input {

    width: 100%;

  }

}


@media (max-width: 600px) {

  .detail-grid {

    grid-template-columns: 1fr;

  }

  .details-drawer {

    width: 100%;

  }

}

</style>