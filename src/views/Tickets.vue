<template>
  <div class="tickets-page">
    <div class="page-header">
      <div>
        <h1>Incident Management</h1>
        <p>Review, dispatch, and track active civic issues.</p>
      </div>

      <div class="header-actions">
        <button type="button" class="export-btn">↓ Export PDF</button>
        <button type="button" class="export-btn">▣ Export CSV</button>
      </div>
    </div>

    <div class="filter-card">
      <label class="search-box">
        <span aria-hidden="true">⌕</span>
        <input v-model="search" type="search" placeholder="Search tickets, ID, location, or keyword..." aria-label="Search tickets" />
      </label>

      <select v-model="district" aria-label="Filter by district">
        <option value="">All Districts</option>
        <option>District 1</option>
        <option>District 2</option>
        <option>District 3</option>
      </select>

      <select v-model="barangay" aria-label="Filter by barangay">
        <option value="">All Barangays</option>
        <option>Carmen</option>
        <option>Balulang</option>
        <option>Lapasan</option>
        <option>Macasandig</option>
        <option>Lumbia</option>
      </select>

      <select v-model="category" aria-label="Filter by category">
        <option value="">All Categories</option>
        <option v-for="item in categories" :key="item" :value="item">{{ item }}</option>
      </select>

      <select v-model="status" aria-label="Filter by status">
        <option value="">All Statuses</option>
        <option v-for="item in statuses" :key="item" :value="item">{{ item }}</option>
      </select>

      <button type="button" class="clear-btn" @click="clearFilters">Clear</button>
    </div>

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
            <th><span class="sr-only">Actions</span></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="ticket in paginatedTickets" :key="ticket.id">
            <td class="ticket-id">{{ ticket.id }}</td>
            <td><div>{{ ticket.date }}</div><small>{{ ticket.time }}</small></td>
            <td>{{ ticket.location }}</td>
            <td><span class="category"><span aria-hidden="true">{{ categoryIcon(ticket.category) }}</span> {{ ticket.category }}</span></td>
            <td><div class="media-thumb" :aria-label="ticket.media ? 'Photo evidence attached' : 'No media attached'">{{ ticket.media ? "📷" : "—" }}</div></td>
            <td><span class="status" :class="statusClass(ticket.status)">{{ ticket.status }}</span></td>
            <td><button type="button" class="view-btn" @click="viewTicket(ticket)">View</button></td>
          </tr>
          <tr v-if="paginatedTickets.length === 0">
            <td colspan="7" class="empty-state">No tickets found.</td>
          </tr>
        </tbody>
      </table>

      <div class="table-footer">
        <span>Showing {{ showingFrom }}–{{ showingTo }} of {{ filteredTickets.length }} tickets</span>
        <div v-if="totalPages > 1" class="pagination" aria-label="Ticket pages">
          <button type="button" :disabled="currentPage === 1" aria-label="Previous page" @click="goToPage(currentPage - 1)">‹</button>
          <button v-for="page in totalPages" :key="page" type="button" :class="{ active: currentPage === page }" :aria-label="`Page ${page}`" :aria-current="currentPage === page ? 'page' : undefined" @click="goToPage(page)">{{ page }}</button>
          <button type="button" :disabled="currentPage === totalPages" aria-label="Next page" @click="goToPage(currentPage + 1)">›</button>
        </div>
      </div>
    </div>

    <div v-if="selectedTicket" class="modal-overlay" @click.self="closeTicket">
      <section class="ticket-modal" role="dialog" aria-modal="true" aria-labelledby="ticket-modal-title" @keydown.esc="closeTicket">
        <div class="modal-header">
          <div>
            <span class="modal-label">TICKET DETAILS</span>
            <h2 id="ticket-modal-title">{{ selectedTicket.id }}</h2>
          </div>
          <button type="button" class="close-btn" aria-label="Close ticket details" @click="closeTicket">×</button>
        </div>

        <div class="modal-body">
          <div class="modal-status-row">
            <span class="status" :class="statusClass(selectedTicket.status)">{{ selectedTicket.status }}</span>
            <span v-if="selectedTicket.team" class="assignment-summary">{{ selectedTicket.team }} · {{ selectedTicket.personnel || 'Team pending' }}</span>
          </div>

          <div class="detail-grid">
            <div class="detail-item"><span>Category</span><strong>{{ categoryIcon(selectedTicket.category) }} {{ selectedTicket.category }}</strong></div>
            <div class="detail-item"><span>Date Reported</span><strong>{{ selectedTicket.date }}</strong></div>
            <div class="detail-item"><span>Time Reported</span><strong>{{ selectedTicket.time }}</strong></div>
            <div class="detail-item"><span>Location</span><strong>{{ selectedTicket.location }}</strong></div>
            <div class="detail-item"><span>District / Barangay</span><strong>{{ selectedTicket.district }} · {{ selectedTicket.barangay }}</strong></div>
            <div class="detail-item"><span>Reported By</span><strong>{{ selectedTicket.reporter }}</strong></div>
          </div>

          <div class="detail-section">
            <span>Incident Description</span>
            <p>{{ selectedTicket.description }}</p>
          </div>

          <div class="detail-section">
            <span>Evidence</span>
            <div v-if="selectedTicket.media" class="evidence-box"><span class="evidence-icon" aria-hidden="true">📷</span><div><strong>Photo evidence attached</strong><small>Media preview will be connected when ticket uploads are available.</small></div></div>
            <div v-else class="no-evidence">No media attached</div>
          </div>

          <div class="dispatch-section">
            <div class="section-heading"><span>DISPATCH &amp; STATUS</span><small>Changes are saved locally for this demo.</small></div>
            <div class="assignment-grid">
              <label>Assign team<select v-model="assignment.team"><option value="">Select a response team</option><option v-for="team in teams" :key="team" :value="team">{{ team }}</option></select></label>
              <label>Assign personnel<select v-model="assignment.personnel" :disabled="!assignment.team"><option value="">Select personnel</option><option v-for="person in availablePersonnel" :key="person" :value="person">{{ person }}</option></select></label>
              <label class="status-field">Status<select v-model="assignment.status"><option v-for="item in statuses" :key="item" :value="item">{{ item }}</option></select></label>
            </div>
            <label class="notes-field">Admin notes<textarea v-model="assignment.notes" rows="3" placeholder="Add dispatch instructions, follow-up notes, or resolution details..."></textarea></label>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="secondary-btn" @click="closeTicket">Close</button>
          <button type="button" class="primary-btn" :disabled="!canSaveAssignment" @click="saveTicket">{{ saveButtonLabel }}</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from "vue"

const pageSize = 4
const search = ref("")
const district = ref("")
const barangay = ref("")
const category = ref("")
const status = ref("")
const currentPage = ref(1)
const selectedTicket = ref(null)
const assignment = ref({ team: "", personnel: "", status: "Pending", notes: "" })

const categories = ["Waste Collection", "Road Repair", "Fallen Tree", "Street Lighting"]
const statuses = ["Pending", "Assigned", "In Progress", "Resolved"]
const teams = ["Sanitation Response Team", "Roads & Infrastructure Team", "Parks & Emergency Team", "Electrical Maintenance Team"]
const personnelByTeam = {
  "Sanitation Response Team": ["R. Dela Cruz", "M. Villanueva", "J. Santos"],
  "Roads & Infrastructure Team": ["A. Ramos", "C. Garcia", "P. Flores"],
  "Parks & Emergency Team": ["L. Navarro", "D. Reyes", "K. Lim"],
  "Electrical Maintenance Team": ["T. Aquino", "N. Bautista", "S. Cruz"],
}

const tickets = ref([
  { id: "#TK-2023-8901", date: "Oct 24, 2023", time: "08:15 AM", location: "Brgy. Carmen, Purok 4", district: "District 1", barangay: "Carmen", category: "Waste Collection", media: true, status: "Pending", reporter: "Maria Santos", description: "Uncollected household waste has accumulated along the roadside for two collection cycles.", team: "", personnel: "", notes: "" },
  { id: "#TK-2023-8895", date: "Oct 23, 2023", time: "02:40 PM", location: "Brgy. Balulang, Macanhan Road", district: "District 2", barangay: "Balulang", category: "Road Repair", media: true, status: "Assigned", reporter: "Carlos Reyes", description: "A growing pothole is affecting vehicles near the intersection and needs inspection.", team: "Roads & Infrastructure Team", personnel: "A. Ramos", notes: "Inspect the road surface and prepare a repair estimate." },
  { id: "#TK-2023-8891", date: "Oct 21, 2023", time: "10:20 AM", location: "Brgy. Lumbia, Masterson Ave", district: "District 3", barangay: "Lumbia", category: "Fallen Tree", media: true, status: "Resolved", reporter: "Ana Cruz", description: "A fallen tree was blocking one lane after heavy rain.", team: "Parks & Emergency Team", personnel: "L. Navarro", notes: "Tree cleared and roadway reopened." },
  { id: "#TK-2023-8905", date: "Oct 24, 2023", time: "09:30 AM", location: "Brgy. Macasandig, Tomas Saco St.", district: "District 1", barangay: "Macasandig", category: "Street Lighting", media: false, status: "Pending", reporter: "Joel Mendoza", description: "The street light near the pedestrian crossing has been out for several nights.", team: "", personnel: "", notes: "" },
  { id: "#TK-2023-8887", date: "Oct 20, 2023", time: "04:05 PM", location: "Brgy. Lapasan, Corrales Extension", district: "District 1", barangay: "Lapasan", category: "Street Lighting", media: false, status: "In Progress", reporter: "Grace Tan", description: "Two consecutive street lamps are not functioning beside the public market.", team: "Electrical Maintenance Team", personnel: "T. Aquino", notes: "Replacement bulbs requested from the depot." },
  { id: "#TK-2023-8882", date: "Oct 19, 2023", time: "11:10 AM", location: "Brgy. Carmen, Velez Street", district: "District 1", barangay: "Carmen", category: "Waste Collection", media: true, status: "Assigned", reporter: "Ben Flores", description: "Public bin is overflowing after the weekend market.", team: "Sanitation Response Team", personnel: "M. Villanueva", notes: "Schedule an additional collection run." },
  { id: "#TK-2023-8876", date: "Oct 18, 2023", time: "01:25 PM", location: "Brgy. Balulang, Riverside Road", district: "District 2", barangay: "Balulang", category: "Fallen Tree", media: true, status: "Pending", reporter: "Ivy Lopez", description: "A leaning tree branch may fall onto the sidewalk and utility line.", team: "", personnel: "", notes: "" },
  { id: "#TK-2023-8869", date: "Oct 17, 2023", time: "07:50 AM", location: "Brgy. Lumbia, Xavier Heights", district: "District 3", barangay: "Lumbia", category: "Road Repair", media: false, status: "Resolved", reporter: "Paolo Diaz", description: "Cracked pavement was creating a hazard near the school gate.", team: "Roads & Infrastructure Team", personnel: "C. Garcia", notes: "Temporary patch completed; scheduled for permanent resurfacing." },
])

const filteredTickets = computed(() => {
  const keyword = search.value.toLowerCase().trim()
  return tickets.value.filter((ticket) => {
    const searchable = [ticket.id, ticket.location, ticket.category, ticket.reporter, ticket.description].join(" ").toLowerCase()
    return (!keyword || searchable.includes(keyword)) && (!district.value || ticket.district === district.value) && (!barangay.value || ticket.barangay === barangay.value) && (!category.value || ticket.category === category.value) && (!status.value || ticket.status === status.value)
  })
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredTickets.value.length / pageSize)))
const paginatedTickets = computed(() => filteredTickets.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize))
const showingFrom = computed(() => filteredTickets.value.length ? (currentPage.value - 1) * pageSize + 1 : 0)
const showingTo = computed(() => Math.min(currentPage.value * pageSize, filteredTickets.value.length))
const availablePersonnel = computed(() => personnelByTeam[assignment.value.team] || [])
const canSaveAssignment = computed(() => assignment.value.status === "Pending" || (assignment.value.team && assignment.value.personnel))
const saveButtonLabel = computed(() => assignment.value.team ? "Save Assignment" : "Update Status")

watch([search, district, barangay, category, status], () => { currentPage.value = 1 })
watch(() => assignment.value.team, () => { assignment.value.personnel = "" })
watch(totalPages, (pages) => { if (currentPage.value > pages) currentPage.value = pages })

function categoryIcon(value) { return { "Waste Collection": "♻", "Road Repair": "▰", "Fallen Tree": "♣", "Street Lighting": "☀" }[value] || "•" }
function statusClass(value) { return value.toLowerCase().replace(/\s+/g, "-") }
function goToPage(page) { currentPage.value = Math.min(Math.max(page, 1), totalPages.value) }
function clearFilters() { search.value = ""; district.value = ""; barangay.value = ""; category.value = ""; status.value = "" }
function viewTicket(ticket) { selectedTicket.value = ticket; assignment.value = { team: ticket.team, personnel: ticket.personnel, status: ticket.status, notes: ticket.notes } }
function closeTicket() { selectedTicket.value = null }
function saveTicket() {
  if (!selectedTicket.value) return
  selectedTicket.value.team = assignment.value.team
  selectedTicket.value.personnel = assignment.value.personnel
  selectedTicket.value.status = assignment.value.status === "Pending" && assignment.value.team ? "Assigned" : assignment.value.status
  selectedTicket.value.notes = assignment.value.notes.trim()
  assignment.value.status = selectedTicket.value.status
}
</script>

<style scoped>
.tickets-page { padding: 32px; color: #17233b; }
.page-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
.page-header h1 { margin: 0; font-size: 28px; }
.page-header p { margin-top: 6px; color: #7b8798; }
.header-actions { display: flex; gap: 10px; }
.export-btn { border: 1px solid #d8dee8; background: white; padding: 10px 14px; border-radius: 7px; cursor: pointer; color: #39465c; }
.export-btn:hover, .secondary-btn:hover, .pagination button:hover:not(:disabled) { background: #f8fafc; }
.filter-card { display: grid; grid-template-columns: 2fr repeat(4, 1fr) auto; gap: 10px; padding: 16px; background: white; border: 1px solid #e1e6ed; border-radius: 10px; margin-bottom: 20px; }
.search-box { display: flex; align-items: center; gap: 8px; border: 1px solid #d8dee8; border-radius: 7px; padding: 0 12px; }
.search-box span { color: hsl(221, 13%, 46%); }
.search-box input { width: 100%; border: 0; outline: 0; padding: 11px 0;  background: #ffffff; color: #17233b}
select, textarea { width: 100%; box-sizing: border-box; border: 1px solid #d8dee8; border-radius: 7px; padding: 10px; background: white; color: #39465c; font: inherit; }
select:focus, textarea:focus, input:focus, button:focus-visible { outline: 2px solid #73b980; outline-offset: 2px; }
textarea { resize: vertical; line-height: 1.45; }
.clear-btn { border: 0; background: transparent; color: #248c3b; cursor: pointer; }
.clear-btn:hover { text-decoration: underline; }
.table-card { background: white; border: 1px solid #e1e6ed; border-radius: 10px; overflow: hidden; }
table { width: 100%; border-collapse: collapse; }
th { background: #f8fafc; color: #68758a; font-size: 12px; text-align: left; padding: 14px 16px; }
td { padding: 16px; border-top: 1px solid #edf0f4; font-size: 14px; }
.ticket-id { font-weight: 600; color: #236b35; }
td small, .evidence-box small { color: #8a94a5; }
.media-thumb { font-size: 18px; }
.category { color: #39465c; white-space: nowrap; }
.status { display: inline-block; padding: 6px 10px; border-radius: 20px; font-size: 12px; font-weight: 600; white-space: nowrap; }
.status.pending { background: #fff2c9; color: #9b7200; }
.status.assigned { background: #e5f0ff; color: #2870bd; }
.status.in-progress { background: #efe7ff; color: #6b46c1; }
.status.resolved { background: #dff4e3; color: #27843b; }
.view-btn { border: 1px solid #2d943d; background: white; color: #248638; padding: 7px 14px; border-radius: 6px; cursor: pointer; }
.view-btn:hover { background: #f0f9f2; }
.empty-state { text-align: center; padding: 40px; color: #d8dbdf; }
.table-footer { display: flex; justify-content: space-between; align-items: center; padding: 14px 16px; color: #7c8798; font-size: 13px; }
.pagination { display: flex; gap: 5px; }
.pagination button { border: 1px solid #dce2e9; background: white; width: 30px; height: 30px; border-radius: 5px; cursor: pointer; }
.pagination button:disabled { cursor: not-allowed; opacity: .45; }
.pagination .active { background: #278c39; color: white; border-color: #278c39; }
.modal-overlay { position: fixed; inset: 0; background: rgba(15, 23, 42, .45); display: flex; align-items: center; justify-content: center; z-index: 1000; padding: 20px; }
.ticket-modal { width: 100%; max-width: 680px; max-height: calc(100vh - 40px); background: white; border-radius: 12px; box-shadow: 0 20px 50px rgba(15, 23, 42, .2); overflow: auto; }
.modal-header { display: flex; justify-content: space-between; align-items: flex-start; padding: 22px 24px; border-bottom: 1px solid #edf0f4; }
.modal-label { display: block; margin-bottom: 5px; font-size: 11px; font-weight: 700; letter-spacing: .08em; color: #7c8798; }
.modal-header h2 { margin: 0; font-size: 22px; color: #17233b; }
.close-btn { border: 0; background: transparent; font-size: 28px; line-height: 1; color: #7c8798; cursor: pointer; }
.close-btn:hover { color: #17233b; }
.modal-body { padding: 24px; }
.modal-status-row { display: flex; justify-content: space-between; align-items: center; gap: 12px; margin-bottom: 22px; }
.assignment-summary { color: #526078; font-size: 13px; text-align: right; }
.detail-grid, .assignment-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 18px; }
.detail-grid { margin-bottom: 24px; }
.detail-item, .assignment-grid label, .notes-field { display: flex; flex-direction: column; gap: 5px; }
.detail-item span, .detail-section > span, .assignment-grid label, .notes-field { font-size: 12px; color: #7c8798; }
.detail-item strong { font-size: 14px; color: #17233b; }
.detail-section { margin-top: 20px; }
.detail-section p { margin: 8px 0 0; color: #526078; font-size: 14px; line-height: 1.6; }
.evidence-box, .no-evidence { margin-top: 8px; padding: 14px; background: #f8fafc; border: 1px solid #e1e6ed; border-radius: 8px; color: #526078; font-size: 14px; }
.evidence-box { display: flex; align-items: center; gap: 10px; }
.evidence-box strong, .evidence-box small { display: block; }
.evidence-box small { margin-top: 3px; font-size: 12px; }
.evidence-icon { font-size: 18px; }
.no-evidence { color: #8a94a5; }
.dispatch-section { margin-top: 26px; padding: 18px; border: 1px solid #e1e6ed; border-radius: 8px; background: #fbfcfd; }
.section-heading { display: flex; justify-content: space-between; gap: 12px; margin-bottom: 14px; }
.section-heading span { color: #526078; font-size: 11px; font-weight: 700; letter-spacing: .08em; }
.section-heading small { color: #8a94a5; }
.status-field { grid-column: 1 / -1; }
.notes-field { margin-top: 16px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 16px 24px; background: #f8fafc; border-top: 1px solid #edf0f4; }
.secondary-btn, .primary-btn { padding: 9px 16px; border-radius: 6px; font-size: 13px; font-weight: 600; cursor: pointer; }
.secondary-btn { background: white; border: 1px solid #d8dee8; color: #526078; }
.primary-btn { background: #278c39; border: 1px solid #278c39; color: white; }
.primary-btn:hover:not(:disabled) { background: #217831; }
.primary-btn:disabled { cursor: not-allowed; opacity: .55; }
.sr-only { position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0, 0, 0, 0); white-space: nowrap; border: 0; }
@media (max-width: 1100px) { .filter-card { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 800px) { .tickets-page { padding: 20px; } .page-header { flex-direction: column; gap: 15px; } .filter-card { grid-template-columns: 1fr; } .table-card { overflow-x: auto; } table { min-width: 900px; } }
@media (max-width: 600px) { .header-actions, .table-footer, .modal-status-row, .section-heading { align-items: flex-start; flex-direction: column; } .detail-grid, .assignment-grid { grid-template-columns: 1fr; } .status-field { grid-column: auto; } .assignment-summary { text-align: left; } .modal-footer { flex-direction: column; } .secondary-btn, .primary-btn { width: 100%; } }
</style>
