<template>
  <div class="settings-page">

    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1>Settings</h1>
        <p>Manage your admin account, preferences, and security.</p>
      </div>
    </div>


    <div class="settings-layout">

      <!-- Left Navigation -->
      <aside class="settings-nav">

        <button
          type="button"
          :class="{ active: activeSection === 'account' }"
          @click="activeSection = 'account'"
        >
          <span class="nav-icon">◉</span>
          Account
        </button>

        <button
          type="button"
          :class="{ active: activeSection === 'notifications' }"
          @click="activeSection = 'notifications'"
        >
          <span class="nav-icon">♧</span>
          Notifications
        </button>

        <button
          type="button"
          :class="{ active: activeSection === 'security' }"
          @click="activeSection = 'security'"
        >
          <span class="nav-icon">▣</span>
          Security
        </button>

        <button
          type="button"
          :class="{ active: activeSection === 'system' }"
          @click="activeSection = 'system'"
        >
          <span class="nav-icon">⚙</span>
          System Preferences
        </button>

      </aside>


      <!-- Main Settings Content -->
      <main class="settings-content">


        <!-- ========================= -->
        <!-- ACCOUNT -->
        <!-- ========================= -->

        <section
          v-if="activeSection === 'account'"
          class="settings-card"
        >

          <div class="section-header">
            <div>
              <h2>Admin Account</h2>
              <p>Update your administrator profile information.</p>
            </div>
          </div>


          <div class="profile-area">

            <div class="avatar">
              AD
            </div>

            <div>
              <strong>CLENRO Central Admin</strong>
              <span>System Administrator</span>
            </div>

          </div>


          <div class="form-grid">

            <label>
              Full Name

              <input
                v-model="account.name"
                type="text"
              />
            </label>


            <label>
              Official Email

              <input
                v-model="account.email"
                type="email"
              />
            </label>


            <label>
              Role

              <input
                value="CLENRO Central Admin"
                type="text"
                disabled
              />
            </label>


            <label>
              Phone Number

              <input
                v-model="account.phone"
                type="tel"
              />
            </label>

          </div>


          <div class="action-row">

            <button
              type="button"
              class="primary-btn"
              @click="saveAccount"
            >
              Save Changes
            </button>

          </div>

        </section>


        <!-- ========================= -->
        <!-- NOTIFICATIONS -->
        <!-- ========================= -->

        <section
          v-if="activeSection === 'notifications'"
          class="settings-card"
        >

          <div class="section-header">
            <div>
              <h2>Notification Preferences</h2>
              <p>Choose which operational alerts you receive.</p>
            </div>
          </div>


          <div class="preference-list">

            <div class="preference-item">

              <div>
                <strong>New Incident Reports</strong>
                <span>Notify me when citizens submit a new report.</span>
              </div>

              <label class="switch">
                <input
                  v-model="notifications.newReports"
                  type="checkbox"
                />
                <span></span>
              </label>

            </div>


            <div class="preference-item">

              <div>
                <strong>Dispatch Updates</strong>
                <span>Notify me when a ticket is assigned or dispatched.</span>
              </div>

              <label class="switch">
                <input
                  v-model="notifications.dispatch"
                  type="checkbox"
                />
                <span></span>
              </label>

            </div>


            <div class="preference-item">

              <div>
                <strong>Resolution Updates</strong>
                <span>Notify me when an incident is marked resolved.</span>
              </div>

              <label class="switch">
                <input
                  v-model="notifications.resolution"
                  type="checkbox"
                />
                <span></span>
              </label>

            </div>


            <div class="preference-item">

              <div>
                <strong>System Alerts</strong>
                <span>Receive important system and security notifications.</span>
              </div>

              <label class="switch">
                <input
                  v-model="notifications.system"
                  type="checkbox"
                />
                <span></span>
              </label>

            </div>

          </div>


          <div class="action-row">

            <button
              type="button"
              class="primary-btn"
              @click="saveNotifications"
            >
              Save Preferences
            </button>

          </div>

        </section>


        <!-- ========================= -->
        <!-- SECURITY -->
        <!-- ========================= -->

        <section
          v-if="activeSection === 'security'"
          class="settings-card"
        >

          <div class="section-header">
            <div>
              <h2>Security</h2>
              <p>Manage your password and administrator security settings.</p>
            </div>
          </div>


          <div class="security-status">

            <div class="security-icon">
              ✓
            </div>

            <div>
              <strong>Admin Security</strong>
              <span>Your administrator account is protected by password and security PIN.</span>
            </div>

          </div>


          <div class="form-grid one-column">

            <label>
              Current Password

              <input
                v-model="passwordForm.current"
                type="password"
                placeholder="Enter current password"
              />
            </label>


            <label>
              New Password

              <input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="Enter new password"
              />
            </label>


            <label>
              Confirm New Password

              <input
                v-model="passwordForm.confirm"
                type="password"
                placeholder="Confirm new password"
              />
            </label>


            <label>
              Security PIN

              <input
                v-model="passwordForm.pin"
                type="password"
                maxlength="6"
                placeholder="6-digit security PIN"
              />
            </label>

          </div>


          <div class="action-row">

            <button
              type="button"
              class="primary-btn"
              @click="changePassword"
            >
              Update Security
            </button>

          </div>

        </section>


        <!-- ========================= -->
        <!-- SYSTEM -->
        <!-- ========================= -->

        <section
          v-if="activeSection === 'system'"
          class="settings-card"
        >

          <div class="section-header">
            <div>
              <h2>System Preferences</h2>
              <p>Configure how the CityCare Admin portal behaves.</p>
            </div>
          </div>


          <div class="form-grid one-column">

            <label>
              Default Dashboard View

              <select v-model="system.dashboardView">
                <option>Executive Dashboard</option>
                <option>Incident Queue</option>
                <option>Fleet Overview</option>
              </select>
            </label>


            <label>
              Default Ticket Status Filter

              <select v-model="system.ticketStatus">
                <option>All Statuses</option>
                <option>Pending</option>
                <option>Assigned</option>
                <option>In Progress</option>
                <option>Resolved</option>
              </select>
            </label>


            <label>
              Report Period

              <select v-model="system.reportPeriod">
                <option>Current Month</option>
                <option>Last Month</option>
                <option>Last 3 Months</option>
                <option>Current Year</option>
              </select>
            </label>

          </div>


          <div class="preference-item single">

            <div>
              <strong>Auto-refresh Dashboard</strong>
              <span>Automatically refresh operational information.</span>
            </div>

            <label class="switch">
              <input
                v-model="system.autoRefresh"
                type="checkbox"
              />
              <span></span>
            </label>

          </div>


          <div class="action-row">

            <button
              type="button"
              class="primary-btn"
              @click="saveSystem"
            >
              Save Preferences
            </button>

          </div>

        </section>


        <!-- Logout -->
        <div class="logout-area">

          <button
            type="button"
            class="logout-btn"
            @click="logout"
          >
            ↪ Log Out
          </button>

        </div>

      </main>

    </div>


    <!-- Saved message -->
    <div
      v-if="savedMessage"
      class="toast"
    >
      {{ savedMessage }}
    </div>

  </div>
</template>


<script setup>

import { ref } from "vue"
import { useRouter } from "vue-router"


const router = useRouter()


/* ================================
   ACTIVE SECTION
================================ */

const activeSection = ref("account")


/* ================================
   ACCOUNT
================================ */

const account = ref({
  name: "CityCare Administrator",
  email: "admin@clenro-cdo.gov.ph",
  phone: "0917 123 4567"
})


/* ================================
   NOTIFICATIONS
================================ */

const notifications = ref({
  newReports: true,
  dispatch: true,
  resolution: true,
  system: true
})


/* ================================
   PASSWORD
================================ */

const passwordForm = ref({
  current: "",
  newPassword: "",
  confirm: "",
  pin: ""
})


/* ================================
   SYSTEM
================================ */

const system = ref({
  dashboardView: "Executive Dashboard",
  ticketStatus: "All Statuses",
  reportPeriod: "Current Month",
  autoRefresh: true
})


/* ================================
   TOAST
================================ */

const savedMessage = ref("")


function showSavedMessage(message) {

  savedMessage.value = message

  setTimeout(() => {
    savedMessage.value = ""
  }, 2500)

}


/* ================================
   SAVE ACCOUNT
================================ */

function saveAccount() {

  showSavedMessage(
    "Account changes saved."
  )

}


/* ================================
   SAVE NOTIFICATIONS
================================ */

function saveNotifications() {

  showSavedMessage(
    "Notification preferences saved."
  )

}


/* ================================
   CHANGE PASSWORD
================================ */

function changePassword() {

  if (
    !passwordForm.value.newPassword ||
    !passwordForm.value.confirm
  ) {

    showSavedMessage(
      "Please complete the password fields."
    )

    return

  }


  if (
    passwordForm.value.newPassword !==
    passwordForm.value.confirm
  ) {

    showSavedMessage(
      "New passwords do not match."
    )

    return

  }


  passwordForm.value =
    {
      current: "",
      newPassword: "",
      confirm: "",
      pin: ""
    }


  showSavedMessage(
    "Security settings updated."
  )

}


/* ================================
   SAVE SYSTEM
================================ */

function saveSystem() {

  showSavedMessage(
    "System preferences saved."
  )

}


/* ================================
   LOGOUT
================================ */

function logout() {

  router.push("/login")

}

</script>


<style scoped>

/* =================================
   PAGE
================================= */

.settings-page {

  padding: 32px;

  color: #17233b;

}


/* =================================
   HEADER
================================= */

.page-header {

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


/* =================================
   LAYOUT
================================= */

.settings-layout {

  display: grid;

  grid-template-columns: 220px minmax(0, 1fr);

  gap: 14px;

}


/* =================================
   SETTINGS NAV
================================= */

.settings-nav {

  height: fit-content;

  padding: 10px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

}


.settings-nav button {

  width: 100%;

  display: flex;

  align-items: center;

  gap: 10px;

  padding: 11px 12px;

  border: 0;

  border-radius: 6px;

  background: transparent;

  color: #667085;

  font-size: 12px;

  text-align: left;

  cursor: pointer;

}


.settings-nav button:hover {

  background: #f2f7f3;

  color: #278c39;

}


.settings-nav button.active {

  background: #e8f3ea;

  color: #278c39;

  font-weight: 600;

}


.nav-icon {

  width: 18px;

  text-align: center;

}


/* =================================
   CONTENT CARD
================================= */

.settings-card {

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

  overflow: hidden;

}


.section-header {

  padding: 18px 20px;

  border-bottom: 1px solid #edf0f4;

}


.section-header h2 {

  margin: 0;

  font-size: 17px;

  color: #17233b;

}


.section-header p {

  margin: 4px 0 0;

  font-size: 11px;

  color: #8a94a5;

}


/* =================================
   PROFILE
================================= */

.profile-area {

  display: flex;

  align-items: center;

  gap: 12px;

  margin: 20px;

  padding-bottom: 20px;

  border-bottom: 1px solid #edf0f4;

}


.avatar {

  width: 48px;

  height: 48px;

  display: flex;

  align-items: center;

  justify-content: center;

  border-radius: 50%;

  background: #e0f2e4;

  color: #278c39;

  font-size: 13px;

  font-weight: 700;

}


.profile-area strong {

  display: block;

  color: #17233b;

  font-size: 13px;

}


.profile-area span {

  display: block;

  margin-top: 3px;

  color: #8a94a5;

  font-size: 10px;

}


/* =================================
   FORM
================================= */

.form-grid {

  display: grid;

  grid-template-columns: 1fr 1fr;

  gap: 16px;

  padding: 0 20px 20px;

}


.form-grid.one-column {

  grid-template-columns: 1fr;

}


.form-grid label {

  display: flex;

  flex-direction: column;

  gap: 6px;

  color: #667085;

  font-size: 11px;

  font-weight: 600;

}


input,
select {

  width: 100%;

  height: 40px;

  box-sizing: border-box;

  padding: 0 10px;

  border: 1px solid #d8dee8;

  border-radius: 6px;

  background: white;

  color: #344054;

  outline: none;

  font-size: 12px;

}


input:focus,
select:focus {

  border-color: #73b980;

  box-shadow:
    0 0 0 2px rgba(115, 185, 128, .12);

}


input:disabled {

  background: #f5f7fa;

  color: #8a94a5;

  cursor: not-allowed;

}


/* =================================
   PREFERENCES
================================= */

.preference-list {

  padding: 5px 20px 0;

}


.preference-item {

  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 20px;

  padding: 16px 0;

  border-bottom: 1px solid #edf0f4;

}


.preference-item.single {

  margin: 0 20px;

}


.preference-item strong {

  display: block;

  color: #344054;

  font-size: 12px;

}


.preference-item span {

  display: block;

  margin-top: 3px;

  color: #8a94a5;

  font-size: 10px;

}


/* =================================
   TOGGLE
================================= */

.switch {

  position: relative;

  width: 40px;

  height: 22px;

  flex-shrink: 0;

}


.switch input {

  opacity: 0;

  width: 0;

  height: 0;

}


.switch span {

  position: absolute;

  inset: 0;

  border-radius: 20px;

  background: #d0d5dd;

  cursor: pointer;

  transition: .2s;

}


.switch span::before {

  content: "";

  position: absolute;

  width: 16px;

  height: 16px;

  left: 3px;

  top: 3px;

  border-radius: 50%;

  background: white;

  transition: .2s;

  box-shadow: 0 1px 2px rgba(0,0,0,.15);

}


.switch input:checked + span {

  background: #278c39;

}


.switch input:checked + span::before {

  transform: translateX(18px);

}


/* =================================
   SECURITY
================================= */

.security-status {

  display: flex;

  align-items: center;

  gap: 12px;

  margin: 20px;

  padding: 14px;

  border: 1px solid #dcefe0;

  border-radius: 8px;

  background: #f5fbf6;

}


.security-icon {

  width: 34px;

  height: 34px;

  display: flex;

  justify-content: center;

  align-items: center;

  border-radius: 50%;

  background: #e0f2e4;

  color: #278c39;

  font-weight: 700;

}


.security-status strong {

  display: block;

  font-size: 12px;

  color: #344054;

}


.security-status span {

  display: block;

  margin-top: 3px;

  font-size: 10px;

  color: #7c8798;

}


/* =================================
   ACTIONS
================================= */

.action-row {

  display: flex;

  justify-content: flex-end;

  padding: 15px 20px;

  background: #f8fafc;

  border-top: 1px solid #edf0f4;

}


.primary-btn {

  border: 1px solid #278c39;

  background: #278c39;

  color: white;

  padding: 9px 16px;

  border-radius: 6px;

  font-size: 11px;

  font-weight: 600;

  cursor: pointer;

}


.primary-btn:hover {

  background: #217831;

}


/* =================================
   LOGOUT
================================= */

.logout-area {

  margin-top: 14px;

  padding: 15px;

  background: white;

  border: 1px solid #e1e6ed;

  border-radius: 9px;

}


.logout-btn {

  width: 100%;

  padding: 10px;

  border: 1px solid #e1a6a6;

  border-radius: 6px;

  background: white;

  color: #b42318;

  font-size: 11px;

  font-weight: 600;

  cursor: pointer;

}


.logout-btn:hover {

  background: #fff5f5;

}


/* =================================
   TOAST
================================= */

.toast {

  position: fixed;

  right: 25px;

  bottom: 25px;

  z-index: 2000;

  padding: 12px 16px;

  border-radius: 7px;

  background: #17233b;

  color: white;

  font-size: 11px;

  box-shadow:
    0 8px 25px rgba(15, 23, 42, .2);

}


/* =================================
   RESPONSIVE
================================= */

@media (max-width: 800px) {

  .settings-page {

    padding: 20px;

  }


  .settings-layout {

    grid-template-columns: 1fr;

  }


  .form-grid {

    grid-template-columns: 1fr;

  }

}

</style>