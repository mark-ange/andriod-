<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const role = ref('CLENRO Central Admin')
const email = ref('')
const password = ref('')
const securityPin = ref('')

function login() {
  if (!email.value || !password.value || !securityPin.value) {
    alert('Please complete all fields.')
    return
  }

  router.push('/dashboard')
}
</script>

<template>
  <main class="login-page">
    <section class="login-card">

      <!-- Logo / Brand -->
      <div class="brand">
        <div class="brand-icon">▦</div>
        <h1>CityCare CDO</h1>
      </div>

      <!-- Welcome -->
      <div class="welcome">
        <h2>Welcome Back</h2>
        <p>Please authenticate to access the admin portal.</p>
      </div>

      <!-- Form -->
      <form @submit.prevent="login">

        <label for="role">Select Role</label>

        <select id="role" v-model="role">
          <option>CLENRO Central Admin</option>
          <option>CLENRO Staff</option>
        </select>


        <label for="email">Official Email</label>

        <input
          id="email"
          v-model="email"
          type="email"
          placeholder="admin@clenro-cdo.gov.ph"
        />


        <label for="password">Password</label>

        <input
          id="password"
          v-model="password"
          type="password"
          placeholder="••••••••"
        />


        <label for="pin">Security PIN</label>

        <input
          id="pin"
          v-model="securityPin"
          type="password"
          maxlength="6"
          placeholder="6-digit PIN"
        />


        <button type="submit">
          LOGIN TO ADMIN PORTAL
          <span>→</span>
        </button>

      </form>

      <p class="security-note">
        Secure Access System • CLENRO CDO
      </p>

    </section>
  </main>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  padding: 20px;
  font-family: Arial, Helvetica, sans-serif;
}

.login-card {
  width: 100%;
  max-width: 390px;
  background: #ffffff;
  border: 1px solid #e3e8ee;
  border-radius: 7px;
  padding: 26px 28px 20px;
  box-sizing: border-box;
}

/* Brand */

.brand {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 22px;
}

.brand-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2e8b3c;
  color: #ffffff;
  border-radius: 5px;
  font-size: 16px;
  font-weight: bold;
}

.brand h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #183153;
}

/* Welcome */

.welcome {
  margin-bottom: 20px;
}

.welcome h2 {
  margin: 0 0 5px;
  font-size: 23px;
  color: #182230;
}

.welcome p {
  margin: 0;
  color: #667085;
  font-size: 13px;
}

/* Form */

form {
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 5px;
  font-size: 12px;
  font-weight: 600;
  color: #344054;
}

input,
select {
  width: 100%;
  height: 39px;
  box-sizing: border-box;
  margin-bottom: 13px;
  padding: 0 11px;
  border: 1px solid #d0d5dd;
  border-radius: 5px;
  background: #ffffff;
  color: #344054;
  font-size: 13px;
  outline: none;
}

input:focus,
select:focus {
  border-color: #278c37;
  box-shadow: 0 0 0 2px rgba(39, 140, 55, 0.08);
}

/* Login button */

button {
  width: 100%;
  height: 40px;
  margin-top: 2px;
  border: none;
  border-radius: 5px;
  background: #16852b;
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

button:hover {
  background: #126f24;
}

button span {
  margin-left: 6px;
}

/* Footer */

.security-note {
  margin: 15px 0 0;
  text-align: center;
  font-size: 10px;
  color: #98a2b3;
}
</style>