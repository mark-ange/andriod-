<template>
  <div class="tickets-page">

    <!-- ========================= -->
    <!-- PAGE HEADER -->
    <!-- ========================= -->

    <div class="page-header">
      <div>
        <h1>Incident Management</h1>
        <p>Review, dispatch, and track active civic issues.</p>
      </div>

      <div class="header-actions">
        <button type="button" class="export-btn">
          ↓ Export PDF
        </button>

        <button type="button" class="export-btn">
          ▣ Export CSV
        </button>
      </div>
    </div>


    <!-- ========================= -->
    <!-- FILTERS -->
    <!-- ========================= -->

    <div class="filter-card">

      <label class="search-box">
        <span aria-hidden="true">⌕</span>

        <input
          v-model="search"
          type="search"
          placeholder="Search tickets, ID, location, or keyword..."
          aria-label="Search tickets"
        />
      </label>


      <select
        v-model="district"
        aria-label="Filter by district"
      >
        <option value="">All Districts</option>
        <option>District 1</option>
        <option>District 2</option>
        <option>District 3</option>
      </select>


      <select
        v-model="barangay"
        aria-label="Filter by barangay"
      >
        <option value="">All Barangays</option>
        <option>Carmen</option>
        <option>Balulang</option>
        <option>Lapasan</option>
        <option>Macasandig</option>
        <option>Lumbia</option>
      </select>


      <select
        v-model="category"
        aria-label="Filter by category"
      >
        <option value="">All Categories</option>

        <option
          v-for="item in categories"
          :key="item"
          :value="item"
        >
          {{ item }}
        </option>
      </select>


      <select
        v-model="status"
        aria-label="Filter by status"
      >
        <option value="">All Statuses</option>

        <option
          v-for="item in statuses"
          :key="item"
          :value="item"
        >
          {{ item }}
        </option>
      </select>


      <button
        type="button"
        class="clear-btn"
        @click="clearFilters"
      >
        Clear
      </button>

    </div>


    <!-- ========================= -->
    <!-- TICKETS TABLE -->
    <!-- ========================= -->

    <div class="table-card">

      <table>

        <thead>
          <tr>
            <th>TICKET ID</th>
            <th>DATE/TIME</th>
            <th>LOCATION</th>
            <th>CATEGORY</th>
            <th>MEDIA</th>
            <th>STATUS</th>
            <th>
              <span class="sr-only">Actions</span>
            </th>
          </tr>
        </thead>


        <tbody>

          <tr
            v-for="ticket in paginatedTickets"
            :key="ticket.id"
          >

            <td class="ticket-id">
              {{ ticket.id }}
            </td>


            <td>
              <div>{{ ticket.date }}</div>
              <small>{{ ticket.time }}</small>
            </td>


            <td>
              {{ ticket.location }}
            </td>


            <td>
              <span class="category">

                <span aria-hidden="true">
                  {{ categoryIcon(ticket.category) }}
                </span>

                {{ ticket.category }}

              </span>
            </td>


            <td>

              <div
                class="media-thumb"
                :aria-label="
                  ticket.media
                    ? 'Photo evidence attached'
                    : 'No media attached'
                "
              >
                {{ ticket.media ? "📷" : "—" }}
              </div>

            </td>


            <td>

              <span
                class="status"
                :class="statusClass(ticket.status)"
              >
                {{ ticket.status }}
              </span>

            </td>


            <td>

              <button
                type="button"
                class="view-btn"
                @click="viewTicket(ticket)"
              >
                View
              </button>

            </td>

          </tr>


          <tr v-if="paginatedTickets.length === 0">

            <td
              colspan="7"
              class="empty-state"
            >
              No tickets found.
            </td>

          </tr>

        </tbody>

      </table>


      <!-- ========================= -->
      <!-- TABLE FOOTER -->
      <!-- ========================= -->

      <div class="table-footer">

        <span>
          Showing {{ showingFrom }}–{{ showingTo }}
          of {{ filteredTickets.length }} tickets
        </span>


        <div
          v-if="totalPages > 1"
          class="pagination"
          aria-label="Ticket pages"
        >

          <button
            type="button"
            :disabled="currentPage === 1"
            aria-label="Previous page"
            @click="goToPage(currentPage - 1)"
          >
            ‹
          </button>


          <button
            v-for="page in totalPages"
            :key="page"
            type="button"
            :class="{ active: currentPage === page }"
            :aria-label="`Page ${page}`"
            :aria-current="
              currentPage === page ? 'page' : undefined
            "
            @click="goToPage(page)"
          >
            {{ page }}
          </button>


          <button
            type="button"
            :disabled="currentPage === totalPages"
            aria-label="Next page"
            @click="goToPage(currentPage + 1)"
          >
            ›
          </button>

        </div>

      </div>

    </div>


    <!-- ========================= -->
    <!-- DISPATCH DRAWER -->
    <!-- ========================= -->

    <div
      v-if="selectedTicket"
      class="drawer-overlay"
      @click.self="closeTicket"
    >

      <aside
        class="dispatch-drawer"
        role="dialog"
        aria-modal="true"
        aria-labelledby="dispatch-title"
      >

        <!-- Drawer Header -->

        <div class="drawer-header">

          <div>

            <span class="drawer-label">
              DISPATCH ACTION REQUIRED
            </span>

            <h2 id="dispatch-title">
              {{ selectedTicket.id }}
            </h2>

          </div>


          <button
            type="button"
            class="close-btn"
            aria-label="Close dispatch drawer"
            @click="closeTicket"
          >
            ×
          </button>

        </div>


        <!-- Drawer Body -->

        <div class="drawer-body">

          <!-- Status -->

          <div class="drawer-status">

            <span
              class="status"
              :class="statusClass(selectedTicket.status)"
            >
              {{ selectedTicket.status }}
            </span>


            <span
              v-if="selectedTicket.team"
              class="assigned-summary"
            >
              {{ selectedTicket.team }}

              <span v-if="selectedTicket.personnel">
                · {{ selectedTicket.personnel }}
              </span>
            </span>

          </div>


          <!-- ========================= -->
          <!-- PHOTO -->
          <!-- ========================= -->

          <div class="drawer-section">

            <div class="section-title">
              Evidence
            </div>


            <div
              v-if="selectedTicket.media"
              class="photo-placeholder"
            >
              <span class="photo-icon">📷</span>

              <div>
                <strong>Photo evidence attached</strong>

                <small>
                  Actual image preview can be connected
                  when uploaded media is available.
                </small>
              </div>
            </div>


            <div
              v-else
              class="no-photo"
            >
              No photo evidence attached.
            </div>

          </div>


          <!-- ========================= -->
          <!-- INCIDENT INFORMATION -->
          <!-- ========================= -->

          <div class="drawer-section">

            <div class="section-title">
              Incident Information
            </div>


            <div class="info-grid">

              <div class="info-item">

                <span>Category</span>

                <strong>
                  {{ categoryIcon(selectedTicket.category) }}
                  {{ selectedTicket.category }}
                </strong>

              </div>


              <div class="info-item">

                <span>Date / Time</span>

                <strong>
                  {{ selectedTicket.date }}
                  ·
                  {{ selectedTicket.time }}
                </strong>

              </div>


              <div class="info-item">

                <span>Location</span>

                <strong>
                  {{ selectedTicket.location }}
                </strong>

              </div>


              <div class="info-item">

                <span>District</span>

                <strong>
                  {{ selectedTicket.district }}
                </strong>

              </div>


              <div class="info-item">

                <span>Barangay</span>

                <strong>
                  {{ selectedTicket.barangay }}
                </strong>

              </div>


              <div class="info-item">

                <span>Purok</span>

                <strong>
                  {{ selectedTicket.purok }}
                </strong>

              </div>


              <div class="info-item">

                <span>GPS Coordinates</span>

                <strong>
                  {{ selectedTicket.gps }}
                </strong>

              </div>


              <div class="info-item">

                <span>Landmark</span>

                <strong>
                  {{ selectedTicket.landmark }}
                </strong>

              </div>


              <div class="info-item full-width">

                <span>Reported By</span>

                <strong>
                  {{ selectedTicket.reporter }}
                </strong>

              </div>

            </div>

          </div>


          <!-- ========================= -->
          <!-- DESCRIPTION -->
          <!-- ========================= -->

          <div class="drawer-section">

            <div class="section-title">
              Incident Description
            </div>

            <p class="description">
              {{ selectedTicket.description }}
            </p>

          </div>


          <!-- ========================= -->
          <!-- DISPATCH CONFIG -->
          <!-- ========================= -->

          <div class="dispatch-section">

            <div class="section-heading">

              <div>
                <span>DISPATCH CONFIGURATION</span>

                <small>
                  Assign a response team and personnel.
                </small>
              </div>

            </div>


            <div class="assignment-grid">

              <label>
                Assign Team

                <select v-model="assignment.team">

                  <option value="">
                    Select a response team
                  </option>

                  <option
                    v-for="team in teams"
                    :key="team"
                    :value="team"
                  >
                    {{ team }}
                  </option>

                </select>

              </label>


              <label>
                Assign Personnel

                <select
                  v-model="assignment.personnel"
                  :disabled="!assignment.team"
                >

                  <option value="">
                    Select personnel
                  </option>

                  <option
                    v-for="person in availablePersonnel"
                    :key="person"
                    :value="person"
                  >
                    {{ person }}
                  </option>

                </select>

              </label>


              <label>
                Status

                <select v-model="assignment.status">

                  <option
                    v-for="item in statuses"
                    :key="item"
                    :value="item"
                  >
                    {{ item }}
                  </option>

                </select>

              </label>

            </div>


            <label class="notes-field">

              Dispatch Remarks

              <textarea
                v-model="assignment.notes"
                rows="4"
                placeholder="Add dispatch instructions, follow-up notes, or resolution details..."
              ></textarea>

            </label>

          </div>

        </div>


        <!-- ========================= -->
        <!-- DRAWER FOOTER -->
        <!-- ========================= -->

        <div class="drawer-footer">

          <button
            type="button"
            class="reject-btn"
            @click="rejectTicket"
          >
            Reject
          </button>


          <button
            type="button"
            class="secondary-btn"
            @click="closeTicket"
          >
            Close
          </button>


          <button
            type="button"
            class="primary-btn"
            :disabled="!canSaveAssignment"
            @click="saveTicket"
          >
            {{ saveButtonLabel }}
          </button>

        </div>

      </aside>

    </div>

  </div>
</template>


<script setup>

import {
  computed,
  ref,
  watch
} from "vue"


/* =================================
   PAGINATION
================================= */

const pageSize = 4


/* =================================
   FILTERS
================================= */

const search = ref("")
const district = ref("")
const barangay = ref("")
const category = ref("")
const status = ref("")


/* =================================
   PAGE
================================= */

const currentPage = ref(1)


/* =================================
   SELECTED TICKET
================================= */

const selectedTicket = ref(null)


/* =================================
   ASSIGNMENT
================================= */

const assignment = ref({
  team: "",
  personnel: "",
  status: "Pending",
  notes: ""
})


/* =================================
   OPTIONS
================================= */

const categories = [
  "Waste Collection",
  "Road Repair",
  "Fallen Tree",
  "Street Lighting"
]


const statuses = [
  "Pending",
  "Assigned",
  "In Progress",
  "Resolved",
  "Rejected"
]


const teams = [
  "Sanitation Response Team",
  "Roads & Infrastructure Team",
  "Parks & Emergency Team",
  "Electrical Maintenance Team"
]


const personnelByTeam = {

  "Sanitation Response Team": [
    "R. Dela Cruz",
    "M. Villanueva",
    "J. Santos"
  ],

  "Roads & Infrastructure Team": [
    "A. Ramos",
    "C. Garcia",
    "P. Flores"
  ],

  "Parks & Emergency Team": [
    "L. Navarro",
    "D. Reyes",
    "K. Lim"
  ],

  "Electrical Maintenance Team": [
    "T. Aquino",
    "N. Bautista",
    "S. Cruz"
  ]

}


/* =================================
   TICKETS
================================= */

const tickets = ref([

  {
    id: "#TK-2023-8901",
    date: "Oct 24, 2023",
    time: "08:15 AM",
    location: "Brgy. Carmen, Purok 4",
    district: "District 1",
    barangay: "Carmen",
    purok: "Purok 4",
    gps: "8.4832, 124.6454",
    landmark: "Carmen Public Market",
    category: "Waste Collection",
    media: true,
    status: "Pending",
    reporter: "Maria Santos",
    description:
      "Uncollected household waste has accumulated along the roadside for two collection cycles.",
    team: "",
    personnel: "",
    notes: ""
  },


  {
    id: "#TK-2023-8895",
    date: "Oct 23, 2023",
    time: "02:40 PM",
    location: "Brgy. Balulang, Macanhan Road",
    district: "District 2",
    barangay: "Balulang",
    purok: "Purok 2",
    gps: "8.4491, 124.6178",
    landmark: "Macanhan Road Intersection",
    category: "Road Repair",
    media: true,
    status: "Assigned",
    reporter: "Carlos Reyes",
    description:
      "A growing pothole is affecting vehicles near the intersection and needs inspection.",
    team: "Roads & Infrastructure Team",
    personnel: "A. Ramos",
    notes:
      "Inspect the road surface and prepare a repair estimate."
  },


  {
    id: "#TK-2023-8891",
    date: "Oct 21, 2023",
    time: "10:20 AM",
    location: "Brgy. Lumbia, Masterson Ave",
    district: "District 3",
    barangay: "Lumbia",
    purok: "Purok 7",
    gps: "8.3787, 124.6076",
    landmark: "Masterson Avenue",
    category: "Fallen Tree",
    media: true,
    status: "Resolved",
    reporter: "Ana Cruz",
    description:
      "A fallen tree was blocking one lane after heavy rain.",
    team: "Parks & Emergency Team",
    personnel: "L. Navarro",
    notes:
      "Tree cleared and roadway reopened."
  },


  {
    id: "#TK-2023-8905",
    date: "Oct 24, 2023",
    time: "09:30 AM",
    location: "Brgy. Macasandig, Tomas Saco St.",
    district: "District 1",
    barangay: "Macasandig",
    purok: "Purok 3",
    gps: "8.4708, 124.6498",
    landmark: "Tomas Saco Street",
    category: "Street Lighting",
    media: false,
    status: "Pending",
    reporter: "Joel Mendoza",
    description:
      "The street light near the pedestrian crossing has been out for several nights.",
    team: "",
    personnel: "",
    notes: ""
  },


  {
    id: "#TK-2023-8887",
    date: "Oct 20, 2023",
    time: "04:05 PM",
    location: "Brgy. Lapasan, Corrales Extension",
    district: "District 1",
    barangay: "Lapasan",
    purok: "Purok 5",
    gps: "8.4886, 124.6581",
    landmark: "Public Market",
    category: "Street Lighting",
    media: false,
    status: "In Progress",
    reporter: "Grace Tan",
    description:
      "Two consecutive street lamps are not functioning beside the public market.",
    team: "Electrical Maintenance Team",
    personnel: "T. Aquino",
    notes:
      "Replacement bulbs requested from the depot."
  },


  {
    id: "#TK-2023-8882",
    date: "Oct 19, 2023",
    time: "11:10 AM",
    location: "Brgy. Carmen, Velez Street",
    district: "District 1",
    barangay: "Carmen",
    purok: "Purok 1",
    gps: "8.4867, 124.6442",
    landmark: "Velez Street",
    category: "Waste Collection",
    media: true,
    status: "Assigned",
    reporter: "Ben Flores",
    description:
      "Public bin is overflowing after the weekend market.",
    team: "Sanitation Response Team",
    personnel: "M. Villanueva",
    notes:
      "Schedule an additional collection run."
  },


  {
    id: "#TK-2023-8876",
    date: "Oct 18, 2023",
    time: "01:25 PM",
    location: "Brgy. Balulang, Riverside Road",
    district: "District 2",
    barangay: "Balulang",
    purok: "Purok 6",
    gps: "8.4544, 124.6162",
    landmark: "Riverside Road",
    category: "Fallen Tree",
    media: true,
    status: "Pending",
    reporter: "Ivy Lopez",
    description:
      "A leaning tree branch may fall onto the sidewalk and utility line.",
    team: "",
    personnel: "",
    notes: ""
  },


  {
    id: "#TK-2023-8869",
    date: "Oct 17, 2023",
    time: "07:50 AM",
    location: "Brgy. Lumbia, Xavier Heights",
    district: "District 3",
    barangay: "Lumbia",
    purok: "Purok 2",
    gps: "8.3817, 124.6115",
    landmark: "School Gate",
    category: "Road Repair",
    media: false,
    status: "Resolved",
    reporter: "Paolo Diaz",
    description:
      "Cracked pavement was creating a hazard near the school gate.",
    team: "Roads & Infrastructure Team",
    personnel: "C. Garcia",
    notes:
      "Temporary patch completed; scheduled for permanent resurfacing."
  }

])


/* =================================
   FILTERING
================================= */

const filteredTickets = computed(() => {

  const keyword =
    search.value
      .toLowerCase()
      .trim()


  return tickets.value.filter((ticket) => {

    const searchable = [
      ticket.id,
      ticket.location,
      ticket.category,
      ticket.reporter,
      ticket.description
    ]
      .join(" ")
      .toLowerCase()


    return (

      (!keyword ||
        searchable.includes(keyword))

      &&

      (!district.value ||
        ticket.district === district.value)

      &&

      (!barangay.value ||
        ticket.barangay === barangay.value)

      &&

      (!category.value ||
        ticket.category === category.value)

      &&

      (!status.value ||
        ticket.status === status.value)

    )

  })

})


/* =================================
   PAGINATION
================================= */

const totalPages = computed(() => {

  return Math.max(
    1,
    Math.ceil(
      filteredTickets.value.length / pageSize
    )
  )

})


const paginatedTickets = computed(() => {

  const start =
    (currentPage.value - 1) * pageSize

  const end =
    currentPage.value * pageSize

  return filteredTickets.value.slice(
    start,
    end
  )

})


const showingFrom = computed(() => {

  return filteredTickets.value.length
    ? (currentPage.value - 1) * pageSize + 1
    : 0

})


const showingTo = computed(() => {

  return Math.min(
    currentPage.value * pageSize,
    filteredTickets.value.length
  )

})


/* =================================
   PERSONNEL
================================= */

const availablePersonnel = computed(() => {

  return (
    personnelByTeam[
      assignment.value.team
    ] || []
  )

})


/* =================================
   SAVE VALIDATION
================================= */

const canSaveAssignment = computed(() => {

  /*
   * Any Pending ticket with no
   * assignment can still have its
   * status changed.
   */

  if (
    assignment.value.status === "Pending"
  ) {
    return true
  }


  return (
    Boolean(assignment.value.team) &&
    Boolean(assignment.value.personnel)
  )

})


const saveButtonLabel = computed(() => {

  if (
    assignment.value.status === "Assigned"
  ) {
    return "Assign Ticket"
  }


  if (
    assignment.value.status === "In Progress"
  ) {
    return "Start Response"
  }


  if (
    assignment.value.status === "Resolved"
  ) {
    return "Mark Resolved"
  }


  return "Save Changes"

})


/* =================================
   WATCHERS
================================= */

watch(
  [
    search,
    district,
    barangay,
    category,
    status
  ],
  () => {

    currentPage.value = 1

  }
)


watch(
  () => assignment.value.team,
  () => {

    assignment.value.personnel = ""

  }
)


watch(
  totalPages,
  (pages) => {

    if (
      currentPage.value > pages
    ) {
      currentPage.value = pages
    }

  }
)


/* =================================
   HELPERS
================================= */

function categoryIcon(value) {

  return {

    "Waste Collection": "♻",
    "Road Repair": "▰",
    "Fallen Tree": "♣",
    "Street Lighting": "☀"

  }[value] || "•"

}


function statusClass(value) {

  return value
    .toLowerCase()
    .replace(/\s+/g, "-")

}


/* =================================
   PAGINATION
================================= */

function goToPage(page) {

  currentPage.value =
    Math.min(
      Math.max(page, 1),
      totalPages.value
    )

}


/* =================================
   CLEAR FILTERS
================================= */

function clearFilters() {

  search.value = ""
  district.value = ""
  barangay.value = ""
  category.value = ""
  status.value = ""

}


/* =================================
   OPEN DRAWER
================================= */

function viewTicket(ticket) {

  selectedTicket.value = ticket


  assignment.value = {

    team: ticket.team || "",

    personnel:
      ticket.personnel || "",

    status:
      ticket.status || "Pending",

    notes:
      ticket.notes || ""

  }

}


/* =================================
   CLOSE DRAWER
================================= */

function closeTicket() {

  selectedTicket.value = null

}


/* =================================
   SAVE ASSIGNMENT
================================= */

function saveTicket() {

  if (!selectedTicket.value) {
    return
  }


  const ticket =
    selectedTicket.value


  ticket.team =
    assignment.value.team


  ticket.personnel =
    assignment.value.personnel


  ticket.status =
    assignment.value.status


  ticket.notes =
    assignment.value.notes.trim()


  /*
   * If the admin assigned a team
   * but left status as Pending,
   * automatically move it to Assigned.
   */

  if (
    assignment.value.status === "Pending"
    &&
    assignment.value.team
    &&
    assignment.value.personnel
  ) {

    ticket.status = "Assigned"

  }


  assignment.value.status =
    ticket.status

}


/* =================================
   REJECT TICKET
================================= */

function rejectTicket() {

  if (!selectedTicket.value) {
    return
  }


  selectedTicket.value.status =
    "Rejected"


  selectedTicket.value.team = ""
  selectedTicket.value.personnel = ""


  assignment.value.status =
    "Rejected"

  assignment.value.team = ""
  assignment.value.personnel = ""


  assignment.value.notes =
    "Ticket rejected by administrator."


}

</script>


<style scoped>

/* =================================
   PAGE
================================= */

.tickets-page {

  padding: 32px;

  color: #17233b;

}


/* =================================
   PAGE HEADER
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

  margin-top: 6px;

  color: #7b8798;

}


.header-actions {

  display: flex;

  gap: 10px;

}


.export-btn {

  border: 1px solid #d8dee8;

  background: white;

  padding: 10px 14px;

  border-radius: 7px;

  cursor: pointer;

  color: #39465c;

}


.export-btn:hover {

  background: #f8fafc;

}


/* =================================
   FILTERS
================================= */

.filter-card {

  display: grid;

  grid-template-columns:
    2fr
    repeat(4, 1fr)
    auto;

  gap: 10px;

  padding: 16px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 10px;

  margin-bottom: 20px;

}


.search-box {

  display: flex;

  align-items: center;

  gap: 8px;

  border: 1px solid #d8dee8;

  border-radius: 7px;

  padding: 0 12px;

  background: #ffffff;

}


.search-box span {

  color: #667085;

}


.search-box input {

  width: 100%;

  border: 0;

  outline: 0;

  padding: 11px 0;

  background: #ffffff;

  color: #17233b;

}


.search-box input::placeholder {

  color: #7b8798;

  opacity: 1;

}


select,
textarea {

  width: 100%;

  box-sizing: border-box;

  border: 1px solid #d8dee8;

  border-radius: 7px;

  padding: 10px;

  background: white;

  color: #39465c;

  font: inherit;

}


select:focus,
textarea:focus,
input:focus,
button:focus-visible {

  outline: 2px solid #73b980;

  outline-offset: 2px;

}


.clear-btn {

  border: 0;

  background: transparent;

  color: #248c3b;

  cursor: pointer;

}


.clear-btn:hover {

  text-decoration: underline;

}


/* =================================
   TABLE
================================= */

.table-card {

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 10px;

  overflow: hidden;

}


table {

  width: 100%;

  border-collapse: collapse;

}


th {

  background: #f8fafc;

  color: #68758a;

  font-size: 12px;

  text-align: left;

  padding: 14px 16px;

}


td {

  padding: 16px;

  border-top: 1px solid #edf0f4;

  font-size: 14px;

}


.ticket-id {

  font-weight: 600;

  color: #236b35;

}


td small {

  color: #8a94a5;

}


.media-thumb {

  font-size: 18px;

}


.category {

  color: #39465c;

  white-space: nowrap;

}


/* =================================
   STATUS
================================= */

.status {

  display: inline-block;

  padding: 6px 10px;

  border-radius: 20px;

  font-size: 12px;

  font-weight: 600;

  white-space: nowrap;

}


.status.pending {

  background: #fff2c9;

  color: #9b7200;

}


.status.assigned {

  background: #e5f0ff;

  color: #2870bd;

}


.status.in-progress {

  background: #efe7ff;

  color: #6b46c1;

}


.status.resolved {

  background: #dff4e3;

  color: #27843b;

}


.status.rejected {

  background: #fde8e8;

  color: #b42318;

}


/* =================================
   VIEW
================================= */

.view-btn {

  border: 1px solid #2d943d;

  background: white;

  color: #248638;

  padding: 7px 14px;

  border-radius: 6px;

  cursor: pointer;

}


.view-btn:hover {

  background: #f0f9f2;

}


/* =================================
   EMPTY
================================= */

.empty-state {

  text-align: center;

  padding: 40px;

  color: #8a94a5;

}


/* =================================
   FOOTER
================================= */

.table-footer {

  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 14px 16px;

  color: #7c8798;

  font-size: 13px;

}


.pagination {

  display: flex;

  gap: 5px;

}


.pagination button {

  border: 1px solid #dce2e9;

  background: white;

  width: 30px;

  height: 30px;

  border-radius: 5px;

  cursor: pointer;

}


.pagination button:disabled {

  cursor: not-allowed;

  opacity: .45;

}


.pagination button:hover:not(:disabled) {

  background: #f8fafc;

}


.pagination .active {

  background: #278c39;

  color: white;

  border-color: #278c39;

}


/* =================================
   DRAWER OVERLAY
================================= */

.drawer-overlay {

  position: fixed;

  inset: 0;

  background: rgba(15, 23, 42, .38);

  z-index: 1000;

}


/* =================================
   DRAWER
================================= */

.dispatch-drawer {

  position: absolute;

  top: 0;

  right: 0;

  width: min(560px, 100%);

  height: 100%;

  box-sizing: border-box;

  display: flex;

  flex-direction: column;

  background: #ffffff;

  box-shadow:
    -15px 0 40px rgba(15, 23, 42, .16);

}


/* =================================
   DRAWER HEADER
================================= */

.drawer-header {

  display: flex;

  justify-content: space-between;

  align-items: flex-start;

  padding: 22px 24px;

  border-bottom: 1px solid #edf0f4;

}


.drawer-label {

  display: block;

  margin-bottom: 5px;

  font-size: 10px;

  font-weight: 700;

  letter-spacing: .08em;

  color: #7c8798;

}


.drawer-header h2 {

  margin: 0;

  font-size: 22px;

  color: #17233b;

}


.close-btn {

  border: 0;

  background: transparent;

  font-size: 28px;

  line-height: 1;

  color: #7c8798;

  cursor: pointer;

}


.close-btn:hover {

  color: #17233b;

}


/* =================================
   DRAWER BODY
================================= */

.drawer-body {

  flex: 1;

  overflow-y: auto;

  padding: 22px 24px;

}


.drawer-status {

  display: flex;

  align-items: center;

  justify-content: space-between;

  gap: 12px;

  margin-bottom: 24px;

}


.assigned-summary {

  font-size: 12px;

  color: #667085;

  text-align: right;

}


/* =================================
   SECTIONS
================================= */

.drawer-section {

  margin-bottom: 24px;

}


.section-title {

  margin-bottom: 10px;

  font-size: 11px;

  font-weight: 700;

  letter-spacing: .06em;

  color: #526078;

  text-transform: uppercase;

}


/* =================================
   PHOTO
================================= */

.photo-placeholder,
.no-photo {

  display: flex;

  align-items: center;

  gap: 12px;

  min-height: 82px;

  padding: 14px;

  box-sizing: border-box;

  background: #f8fafc;

  border: 1px solid #e1e6ed;

  border-radius: 8px;

}


.photo-icon {

  width: 44px;

  height: 44px;

  display: flex;

  align-items: center;

  justify-content: center;

  border-radius: 6px;

  background: #edf2f7;

  font-size: 20px;

}


.photo-placeholder strong {

  display: block;

  color: #344054;

  font-size: 13px;

}


.photo-placeholder small {

  display: block;

  margin-top: 4px;

  color: #8a94a5;

  font-size: 11px;

}


.no-photo {

  color: #8a94a5;

  font-size: 13px;

}


/* =================================
   INFORMATION GRID
================================= */

.info-grid {

  display: grid;

  grid-template-columns: 1fr 1fr;

  gap: 16px;

}


.info-item {

  display: flex;

  flex-direction: column;

  gap: 5px;

}


.info-item.full-width {

  grid-column: 1 / -1;

}


.info-item span {

  font-size: 11px;

  color: #8a94a5;

}


.info-item strong {

  font-size: 13px;

  line-height: 1.45;

  color: #17233b;

}


/* =================================
   DESCRIPTION
================================= */

.description {

  margin: 0;

  color: #526078;

  font-size: 13px;

  line-height: 1.6;

}


/* =================================
   DISPATCH
================================= */

.dispatch-section {

  padding: 18px;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

  background: #fbfcfd;

}


.section-heading {

  margin-bottom: 15px;

}


.section-heading span {

  display: block;

  color: #526078;

  font-size: 11px;

  font-weight: 700;

  letter-spacing: .07em;

}


.section-heading small {

  display: block;

  margin-top: 4px;

  color: #8a94a5;

  font-size: 11px;

}


.assignment-grid {

  display: grid;

  grid-template-columns: 1fr 1fr;

  gap: 14px;

}


.assignment-grid label,
.notes-field {

  display: flex;

  flex-direction: column;

  gap: 6px;

  color: #667085;

  font-size: 12px;

  font-weight: 600;

}


.assignment-grid label:last-child {

  grid-column: 1 / -1;

}


.notes-field {

  margin-top: 14px;

}


textarea {

  resize: vertical;

  min-height: 90px;

  line-height: 1.45;

}


/* =================================
   DRAWER FOOTER
================================= */

.drawer-footer {

  display: flex;

  justify-content: flex-end;

  align-items: center;

  gap: 9px;

  padding: 16px 24px;

  background: #f8fafc;

  border-top: 1px solid #edf0f4;

}


.reject-btn,
.secondary-btn,
.primary-btn {

  padding: 9px 16px;

  border-radius: 6px;

  font-size: 13px;

  font-weight: 600;

  cursor: pointer;

}


.reject-btn {

  margin-right: auto;

  border: 1px solid #e1a6a6;

  background: #fff;

  color: #b42318;

}


.reject-btn:hover {

  background: #fff5f5;

}


.secondary-btn {

  border: 1px solid #d8dee8;

  background: white;

  color: #526078;

}


.secondary-btn:hover {

  background: #f8fafc;

}


.primary-btn {

  border: 1px solid #278c39;

  background: #278c39;

  color: white;

}


.primary-btn:hover:not(:disabled) {

  background: #217831;

}


.primary-btn:disabled {

  cursor: not-allowed;

  opacity: .55;

}


/* =================================
   SCREEN READER
================================= */

.sr-only {

  position: absolute;

  width: 1px;

  height: 1px;

  padding: 0;

  margin: -1px;

  overflow: hidden;

  clip: rect(0, 0, 0, 0);

  white-space: nowrap;

  border: 0;

}


/* =================================
   RESPONSIVE
================================= */

@media (max-width: 1100px) {

  .filter-card {

    grid-template-columns:
      repeat(3, 1fr);

  }

}


@media (max-width: 800px) {

  .tickets-page {

    padding: 20px;

  }


  .page-header {

    flex-direction: column;

    gap: 15px;

  }


  .filter-card {

    grid-template-columns: 1fr;

  }


  .table-card {

    overflow-x: auto;

  }


  table {

    min-width: 900px;

  }

}


@media (max-width: 600px) {

  .dispatch-drawer {

    width: 100%;

  }


  .info-grid,
  .assignment-grid {

    grid-template-columns: 1fr;

  }


  .info-item.full-width,
  .assignment-grid label:last-child {

    grid-column: auto;

  }


  .drawer-footer {

    flex-wrap: wrap;

  }


  .reject-btn {

    margin-right: 0;

  }

}

</style>