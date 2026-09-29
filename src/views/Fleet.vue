<template>
  <div class="fleet-page">

    <!-- ========================= -->
    <!-- PAGE HEADER -->
    <!-- ========================= -->

    <div class="page-header">

      <div>
        <h1>Fleet Management</h1>
        <p>Monitor vehicles, drivers, and field operations.</p>
      </div>

      <button
        type="button"
        class="add-btn"
        @click="openAddForm"
      >
        + Add New Vehicle / Driver
      </button>

    </div>


    <!-- ========================= -->
    <!-- FLEET STATUS -->
    <!-- ========================= -->

    <section class="status-grid">

      <div class="status-card">

        <div class="status-card-top">
          <span>IN TRANSIT</span>
          <span class="status-dot green"></span>
        </div>

        <strong>{{ fleetStats.inTransit }}</strong>

        <small>Currently deployed</small>

      </div>


      <div class="status-card">

        <div class="status-card-top">
          <span>IDLE</span>
          <span class="status-dot yellow"></span>
        </div>

        <strong>{{ fleetStats.idle }}</strong>

        <small>Available for dispatch</small>

      </div>


      <div class="status-card">

        <div class="status-card-top">
          <span>MAINTENANCE</span>
          <span class="status-dot red"></span>
        </div>

        <strong>{{ fleetStats.maintenance }}</strong>

        <small>Currently unavailable</small>

      </div>

    </section>


    <!-- ========================= -->
    <!-- LIVE TRACKING -->
    <!-- ========================= -->

    <section class="tracking-card">

      <div class="section-header">

        <div>
          <h2>Live Tracking</h2>
          <p>Current vehicle locations and deployment status.</p>
        </div>

        <div class="map-actions">
          <button type="button">＋</button>
          <button type="button">−</button>
        </div>

      </div>


      <div class="map-area">

        <!-- Decorative roads -->
        <div class="road road-1"></div>
        <div class="road road-2"></div>
        <div class="road road-3"></div>
        <div class="road road-4"></div>
        <div class="road road-5"></div>


        <!-- Map labels -->
        <span class="map-label label-carmen">
          Carmen
        </span>

        <span class="map-label label-lapasan">
          Lapasan
        </span>

        <span class="map-label label-macasandig">
          Macasandig
        </span>

        <span class="map-label label-bugo">
          Bugo
        </span>


        <!-- Vehicle markers -->

        <button
          v-for="truck in trucks"
          :key="truck.id"
          type="button"
          class="truck-marker"
          :class="truck.status.toLowerCase().replace(' ', '-')"
          :style="truck.position"
          @click="selectTruck(truck)"
          :title="`${truck.id} - ${truck.driver}`"
        >
          🚛
        </button>


        <!-- Map legend -->

        <div class="map-legend">

          <span>
            <i class="legend-dot green"></i>
            In Transit
          </span>

          <span>
            <i class="legend-dot yellow"></i>
            Idle
          </span>

          <span>
            <i class="legend-dot red"></i>
            Maintenance
          </span>

        </div>


        <!-- Selected truck -->

        <div
  v-if="selectedTruck"
  class="truck-popup"
>
  <button
    type="button"
    class="popup-close"
    @click="selectedTruck = null"
  >
    ×
  </button>

  <div class="popup-header">
    <div>
      <span class="popup-label">VEHICLE</span>
      <strong>{{ selectedTruck.id }}</strong>
    </div>

    <span
      class="status"
      :class="statusClass(selectedTruck.status)"
    >
      {{ selectedTruck.status }}
    </span>
  </div>

  <div class="vehicle-details">
    <div>
      <span>License Plate</span>
      <strong>{{ selectedTruck.plate }}</strong>
    </div>

    <div>
      <span>Driver</span>
      <strong>{{ selectedTruck.driver }}</strong>
    </div>

    <div>
      <span>Barangay</span>
      <strong>{{ selectedTruck.barangay }}</strong>
    </div>
  </div>

  <div class="popup-actions">
    <button
      type="button"
      @click="viewVehicle"
    >
      View Details
    </button>

    <button
      type="button"
      @click="editVehicle"
    >
      Edit Vehicle
    </button>

    <button
      type="button"
      @click="changeVehicleStatus"
    >
      Change Status
    </button>
  </div>
</div>

      </div>

    </section>


    <!-- ========================= -->
    <!-- ACTIVE FLEET ROSTER -->
    <!-- ========================= -->

    <section class="roster-card">

      <div class="section-header roster-header">

        <div>
          <h2>Active Fleet Roster</h2>
          <p>Vehicles and drivers currently registered in the system.</p>
        </div>

        <input
          v-model="search"
          type="search"
          placeholder="Search vehicle or driver..."
          aria-label="Search vehicle or driver"
        />

      </div>


      <div class="table-wrapper">

        <table>

          <thead>

            <tr>
              <th>TRUCK ID</th>
              <th>LICENSE PLATE</th>
              <th>ASSIGNED BARANGAY</th>
              <th>DRIVER NAME</th>
              <th>STATUS</th>
              <th>ACTIONS</th>
            </tr>

          </thead>


          <tbody>

            <tr
              v-for="truck in filteredTrucks"
              :key="truck.id"
            >

              <td class="truck-id">
                {{ truck.id }}
              </td>


              <td>
                {{ truck.plate }}
              </td>


              <td>
                {{ truck.barangay }}
              </td>


              <td>

                <div class="driver-cell">

                  <div class="driver-avatar">
                    {{ getInitials(truck.driver) }}
                  </div>

                  <span>
                    {{ truck.driver }}
                  </span>

                </div>

              </td>


              <td>

                <span
                  class="status"
                  :class="statusClass(truck.status)"
                >
                  {{ truck.status }}
                </span>

              </td>


              <td>

                <button
                  type="button"
                  class="action-btn"
                  @click="selectTruck(truck)"
                >
                  ⋮
                </button>

              </td>

            </tr>


            <tr v-if="filteredTrucks.length === 0">

              <td
                colspan="6"
                class="empty-state"
              >
                No vehicles found.
              </td>

            </tr>

          </tbody>

        </table>

      </div>


      <div class="table-footer">

        <span>
          Showing 1 to {{ filteredTrucks.length }}
          of {{ trucks.length }} entries
        </span>

        <div class="pagination">

          <button
            type="button"
            disabled
          >
            ‹
          </button>

          <button
            type="button"
            class="active"
          >
            1
          </button>

          <button type="button">
            2
          </button>

          <button type="button">
            3
          </button>

          <button type="button">
            ›
          </button>

        </div>

      </div>

    </section>


    <!-- ========================= -->
    <!-- ADD VEHICLE MODAL -->
    <!-- ========================= -->

    <div
      v-if="showAddForm"
      class="modal-overlay"
      @click.self="closeAddForm"
    >

      <section class="add-modal">

        <div class="modal-header">

          <div>
            <span class="modal-label">
              FLEET MANAGEMENT
            </span>

            <h2>
              Add New Vehicle / Driver
            </h2>
          </div>

          <button
            type="button"
            class="close-btn"
            @click="closeAddForm"
          >
            ×
          </button>

        </div>


        <form @submit.prevent="addTruck">

          <div class="form-grid">

            <label>
              Truck ID

              <input
                v-model="newTruck.id"
                type="text"
                placeholder="TRK-001"
                required
              />
            </label>


            <label>
              License Plate

              <input
                v-model="newTruck.plate"
                type="text"
                placeholder="KGA-0000"
                required
              />
            </label>


            <label>
              Barangay

              <input
                v-model="newTruck.barangay"
                type="text"
                placeholder="Carmen"
                required
              />
            </label>


            <label>
              Driver Name

              <input
                v-model="newTruck.driver"
                type="text"
                placeholder="Juan Dela Cruz"
                required
              />
            </label>


            <label class="full-width">
              Initial Status

              <select v-model="newTruck.status">

                <option>Idle</option>
                <option>In Transit</option>
                <option>Maintenance</option>

              </select>

            </label>

          </div>


          <div class="modal-footer">

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
              Add Vehicle
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
   SEARCH
================================= */

const search = ref("")


/* =================================
   SELECTED TRUCK
================================= */

const selectedTruck = ref(null)


/* =================================
   ADD FORM
================================= */

const showAddForm = ref(false)


const newTruck = ref({
  id: "",
  plate: "",
  barangay: "",
  driver: "",
  status: "Idle"
})


/* =================================
   TRUCK DATA
================================= */

const trucks = ref([

  {
    id: "TRK-042",
    plate: "KGA-9042",
    barangay: "Carmen",
    driver: "Mateo Reyes",
    status: "In Transit",
    position: {
      top: "30%",
      left: "48%"
    }
  },

  {
    id: "TRK-015",
    plate: "KGA-8832",
    barangay: "Lapasan",
    driver: "Juanito Luna",
    status: "Idle",
    position: {
      top: "42%",
      left: "61%"
    }
  },

  {
    id: "TRK-089",
    plate: "KGA-5501",
    barangay: "Macasandig",
    driver: "Eduardo Dalisay",
    status: "Maintenance",
    position: {
      top: "62%",
      left: "68%"
    }
  },

  {
    id: "TRK-022",
    plate: "KGA-2994",
    barangay: "Bugo",
    driver: "Ricardo Cruz",
    status: "In Transit",
    position: {
      top: "68%",
      left: "36%"
    }
  }

])


/* =================================
   FLEET COUNTS
================================= */

const fleetStats = computed(() => {

  return {

    inTransit: trucks.value.filter(
      truck => truck.status === "In Transit"
    ).length + 10,

    idle: trucks.value.filter(
      truck => truck.status === "Idle"
    ).length + 4,

    maintenance: trucks.value.filter(
      truck => truck.status === "Maintenance"
    ).length + 1

  }

})


/* =================================
   SEARCH RESULTS
================================= */

const filteredTrucks = computed(() => {

  const keyword =
    search.value
      .toLowerCase()
      .trim()


  if (!keyword) {
    return trucks.value
  }


  return trucks.value.filter(truck => {

    return [

      truck.id,
      truck.plate,
      truck.barangay,
      truck.driver,
      truck.status

    ]
      .join(" ")
      .toLowerCase()
      .includes(keyword)

  })

})


/* =================================
   HELPERS
================================= */

function getInitials(name) {

  return name
    .split(" ")
    .map(part => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase()

}


function statusClass(status) {

  return status
    .toLowerCase()
    .replace(/\s+/g, "-")

}


/* =================================
   SELECT TRUCK
================================= */

function selectTruck(truck) {

  selectedTruck.value = truck

}

function viewVehicle() {
  if (!selectedTruck.value) return

  alert(
    `Vehicle: ${selectedTruck.value.id}\n` +
    `License Plate: ${selectedTruck.value.plate}\n` +
    `Driver: ${selectedTruck.value.driver}\n` +
    `Barangay: ${selectedTruck.value.barangay}\n` +
    `Status: ${selectedTruck.value.status}`
  )
}

function editVehicle() {
  alert(`Edit ${selectedTruck.value.id} - editing will be connected later.`)
}

function changeVehicleStatus() {
  if (!selectedTruck.value) return

  const nextStatus = {
    "In Transit": "Idle",
    "Idle": "Maintenance",
    "Maintenance": "Idle"
  }

  selectedTruck.value.status =
    nextStatus[selectedTruck.value.status] || "Idle"

  selectedTruck.value = {
    ...selectedTruck.value
  }
}


/* =================================
   ADD FORM
================================= */

function openAddForm() {

  newTruck.value = {
    id: "",
    plate: "",
    barangay: "",
    driver: "",
    status: "Idle"
  }

  showAddForm.value = true

}


function closeAddForm() {

  showAddForm.value = false

}


function addTruck() {

  const truck = {

    ...newTruck.value,

    position: {
      top: "50%",
      left: "50%"
    }

  }


  trucks.value.push(truck)


  closeAddForm()

}

</script>


<style scoped>

/* =================================
   PAGE
================================= */

.fleet-page {

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

  color: #17233b;

}


.page-header p {

  margin: 6px 0 0;

  color: #7b8798;

}


.add-btn {

  border: 0;

  border-radius: 7px;

  padding: 11px 16px;

  background: #278c39;

  color: white;

  font-size: 13px;

  font-weight: 600;

  cursor: pointer;

}


.add-btn:hover {

  background: #217831;

}


/* =================================
   STATUS CARDS
================================= */

.status-grid {

  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 14px;

  margin-bottom: 14px;

}


.status-card {

  padding: 17px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

}


.status-card-top {

  display: flex;

  align-items: center;

  justify-content: space-between;

}


.status-card-top span:first-child {

  font-size: 11px;

  font-weight: 700;

  color: #68758a;

  letter-spacing: .04em;

}


.status-card strong {

  display: block;

  margin-top: 10px;

  font-size: 28px;

  color: #17233b;

}


.status-card small {

  display: block;

  margin-top: 5px;

  color: #98a2b3;

  font-size: 10px;

}


/* Status dots */

.status-dot {

  width: 9px;

  height: 9px;

  border-radius: 50%;

}


.status-dot.green {

  background: #2e8b3c;

}


.status-dot.yellow {

  background: #f2b72b;

}


.status-dot.red {

  background: #dc4444;

}


/* =================================
   TRACKING
================================= */

.tracking-card,
.roster-card {

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

  overflow: hidden;

  margin-bottom: 14px;

}


.section-header {

  padding: 15px 17px;

  display: flex;

  justify-content: space-between;

  align-items: center;

  border-bottom: 1px solid #edf0f4;

}


.section-header h2 {

  margin: 0;

  font-size: 15px;

  color: #17233b;

}


.section-header p {

  margin: 4px 0 0;

  font-size: 10px;

  color: #8a94a5;

}


.map-actions {

  display: flex;

  gap: 5px;

}


.map-actions button {

  width: 28px;

  height: 28px;

  border: 1px solid #d8dee8;

  background: white;

  border-radius: 5px;

  cursor: pointer;

}


/* =================================
   MAP
================================= */

.map-area {

  position: relative;

  height: 280px;

  overflow: hidden;

  background:

    linear-gradient(
      30deg,
      transparent 48%,
      #d8e0e4 49%,
      transparent 50%
    ),

    linear-gradient(
      120deg,
      transparent 48%,
      #d8e0e4 49%,
      transparent 50%
    ),

    #edf2f3;

}


/* roads */

.road {

  position: absolute;

  height: 3px;

  background: #d0d8dc;

  border-radius: 50%;

}


.road-1 {

  width: 120%;

  top: 38%;

  left: -10%;

  transform: rotate(-8deg);

}


.road-2 {

  width: 90%;

  top: 62%;

  left: 5%;

  transform: rotate(15deg);

}


.road-3 {

  width: 100%;

  top: 52%;

  left: 0;

  transform: rotate(-24deg);

}


.road-4 {

  width: 80%;

  top: 22%;

  left: 10%;

  transform: rotate(40deg);

}


.road-5 {

  width: 2px;

  height: 110%;

  top: -5%;

  left: 54%;

  transform: rotate(9deg);

}


/* Map labels */

.map-label {

  position: absolute;

  z-index: 1;

  padding: 5px 7px;

  background: rgba(255, 255, 255, .9);

  border: 1px solid #e1e6ed;

  border-radius: 4px;

  font-size: 9px;

  color: #526078;

}


.label-carmen {

  top: 22%;

  left: 21%;

}


.label-lapasan {

  top: 38%;

  left: 59%;

}


.label-macasandig {

  top: 66%;

  left: 66%;

}


.label-bugo {

  top: 68%;

  left: 31%;

}


/* Truck markers */

.truck-marker {

  position: absolute;

  z-index: 3;

  transform: translate(-50%, -50%);

  width: 35px;

  height: 35px;

  display: flex;

  align-items: center;

  justify-content: center;

  border-radius: 50%;

  border: 3px solid white;

  box-shadow: 0 2px 8px rgba(15, 23, 42, .18);

  cursor: pointer;

  font-size: 16px;

}


.truck-marker.in-transit {

  background: #2e8b3c;

}


.truck-marker.idle {

  background: #f2b72b;

}


.truck-marker.maintenance {

  background: #dc4444;

}


/* Legend */

.map-legend {

  position: absolute;

  left: 12px;

  bottom: 12px;

  z-index: 4;

  display: flex;

  gap: 12px;

  align-items: center;

  padding: 8px 10px;

  background: rgba(255, 255, 255, .93);

  border: 1px solid #e1e6ed;

  border-radius: 5px;

  font-size: 9px;

  color: #526078;

}


.map-legend span {

  display: flex;

  align-items: center;

  gap: 4px;

}


.legend-dot {

  width: 7px;

  height: 7px;

  border-radius: 50%;

}


.legend-dot.green {

  background: #2e8b3c;

}


.legend-dot.yellow {

  background: #f2b72b;

}


.legend-dot.red {

  background: #dc4444;

}


/* Truck popup */

.truck-popup {
  position: absolute;
  top: 18px;
  right: 18px;
  z-index: 10;

  width: 250px;
  padding: 16px;

  box-sizing: border-box;

  background: white;
  border: 1px solid #dce2e9;
  border-radius: 9px;

  box-shadow: 0 10px 30px rgba(15, 23, 42, .15);
}

.popup-close {
  position: absolute;
  top: 7px;
  right: 9px;

  border: 0;
  background: transparent;

  color: #7c8798;
  font-size: 19px;

  cursor: pointer;
}

.popup-close:hover {
  color: #17233b;
}

.popup-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;

  gap: 10px;
  margin-bottom: 15px;
}

.popup-label {
  display: block;
  margin-bottom: 3px;

  font-size: 9px;
  font-weight: 700;
  letter-spacing: .06em;

  color: #8a94a5;
}

.popup-header strong {
  color: #17233b;
  font-size: 16px;
}

.vehicle-details {
  display: flex;
  flex-direction: column;
  gap: 10px;

  padding: 12px 0;

  border-top: 1px solid #edf0f4;
  border-bottom: 1px solid #edf0f4;
}

.vehicle-details div {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.vehicle-details span {
  font-size: 9px;
  color: #8a94a5;
}

.vehicle-details strong {
  font-size: 11px;
  color: #344054;
}

.popup-actions {
  display: flex;
  flex-direction: column;
  gap: 7px;

  margin-top: 13px;
}

.popup-actions button {
  width: 100%;
  height: 32px;

  border: 1px solid #d8dee8;
  border-radius: 5px;

  background: white;
  color: #344054;

  font-size: 10px;
  font-weight: 600;

  cursor: pointer;
}

.popup-actions button:hover {
  background: #f3f8f4;
  border-color: #73b980;
  color: #278c39;
}


.truck-popup strong {

  display: block;

  color: #17233b;

  font-size: 13px;

}


.truck-popup span:not(.status) {

  display: block;

  margin-top: 4px;

  color: #526078;

  font-size: 11px;

}


.truck-popup small {

  display: block;

  margin-top: 3px;

  margin-bottom: 9px;

  color: #8a94a5;

}


.popup-close {

  position: absolute;

  top: 5px;

  right: 7px;

  border: 0;

  background: transparent;

  color: #7c8798;

  font-size: 18px;

  cursor: pointer;

}


/* =================================
   ROSTER
================================= */

.roster-header input {

  width: 210px;

  box-sizing: border-box;

  padding: 8px 10px;

  border: 1px solid #d8dee8;

  border-radius: 6px;

  outline: none;

  font-size: 11px;

}


.roster-header input:focus {

  border-color: #73b980;

  box-shadow: 0 0 0 2px rgba(115, 185, 128, .12);

}


.table-wrapper {

  overflow-x: auto;

}


table {

  width: 100%;

  border-collapse: collapse;

}


th {

  background: #f8fafc;

  color: #68758a;

  font-size: 10px;

  font-weight: 700;

  text-align: left;

  padding: 13px 15px;

  white-space: nowrap;

}


td {

  padding: 14px 15px;

  border-top: 1px solid #edf0f4;

  font-size: 12px;

  color: #344054;

}


.truck-id {

  font-weight: 700;

  color: #17233b;

}


/* Driver */

.driver-cell {

  display: flex;

  align-items: center;

  gap: 8px;

}


.driver-avatar {

  width: 25px;

  height: 25px;

  display: flex;

  align-items: center;

  justify-content: center;

  border-radius: 50%;

  background: #e7f2e9;

  color: #278c39;

  font-size: 8px;

  font-weight: 700;

}


/* Status */

.status {

  display: inline-block;

  padding: 5px 9px;

  border-radius: 20px;

  font-size: 9px;

  font-weight: 700;

  white-space: nowrap;

}


.status.in-transit {

  background: #e0f2e4;

  color: #217831;

}


.status.idle {

  background: #fff2c9;

  color: #9b7200;

}


.status.maintenance {

  background: #fde8e8;

  color: #b42318;

}


/* Actions */

.action-btn {

  border: 0;

  background: transparent;

  color: #667085;

  font-size: 18px;

  cursor: pointer;

}


.action-btn:hover {

  color: #278c39;

}


.empty-state {

  text-align: center;

  padding: 35px;

  color: #8a94a5;

}


/* =================================
   TABLE FOOTER
================================= */

.table-footer {

  display: flex;

  align-items: center;

  justify-content: space-between;

  padding: 14px 15px;

  color: #7c8798;

  font-size: 11px;

}


.pagination {

  display: flex;

  gap: 5px;

}


.pagination button {

  width: 30px;

  height: 30px;

  border: 1px solid #dce2e9;

  background: white;

  border-radius: 5px;

  cursor: pointer;

}


.pagination button:disabled {

  opacity: .4;

  cursor: not-allowed;

}


.pagination button.active {

  background: #278c39;

  color: white;

  border-color: #278c39;

}


/* =================================
   ADD MODAL
================================= */

.modal-overlay {

  position: fixed;

  inset: 0;

  z-index: 1000;

  display: flex;

  align-items: center;

  justify-content: center;

  padding: 20px;

  background: rgba(15, 23, 42, .42);

}


.add-modal {

  width: min(520px, 100%);

  background: white;

  border-radius: 10px;

  box-shadow: 0 20px 50px rgba(15, 23, 42, .2);

  overflow: hidden;

}


.modal-header {

  display: flex;

  align-items: flex-start;

  justify-content: space-between;

  padding: 21px 23px;

  border-bottom: 1px solid #edf0f4;

}


.modal-label {

  display: block;

  margin-bottom: 5px;

  color: #7c8798;

  font-size: 10px;

  font-weight: 700;

  letter-spacing: .08em;

}


.modal-header h2 {

  margin: 0;

  color: #17233b;

  font-size: 20px;

}


.close-btn {

  border: 0;

  background: transparent;

  color: #7c8798;

  font-size: 26px;

  cursor: pointer;

}


form {

  padding: 22px;

}


.form-grid {

  display: grid;

  grid-template-columns: 1fr 1fr;

  gap: 15px;

}


.form-grid label {

  display: flex;

  flex-direction: column;

  gap: 6px;

  color: #667085;

  font-size: 12px;

  font-weight: 600;

}


.form-grid label.full-width {

  grid-column: 1 / -1;

}


.form-grid input,
.form-grid select {

  box-sizing: border-box;

  width: 100%;

  height: 40px;

  padding: 0 10px;

  border: 1px solid #d8dee8;

  border-radius: 6px;

  background: white;

  color: #344054;

  outline: none;

}


.form-grid input:focus,
.form-grid select:focus {

  border-color: #73b980;

  box-shadow: 0 0 0 2px rgba(115, 185, 128, .12);

}


.modal-footer {

  display: flex;

  justify-content: flex-end;

  gap: 9px;

  margin-top: 22px;

  padding-top: 16px;

  border-top: 1px solid #edf0f4;

}


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
   RESPONSIVE
================================= */

@media (max-width: 900px) {

  .status-grid {

    grid-template-columns: 1fr;

  }


  .page-header {

    flex-direction: column;

    gap: 15px;

  }

}


@media (max-width: 700px) {

  .fleet-page {

    padding: 20px;

  }


  .form-grid {

    grid-template-columns: 1fr;

  }


  .form-grid label.full-width {

    grid-column: auto;

  }


  .roster-header {

    align-items: flex-start;

    flex-direction: column;

    gap: 12px;

  }


  .roster-header input {

    width: 100%;

  }

}

</style>