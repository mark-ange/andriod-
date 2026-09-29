import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '../views/AdminLogin.vue'
import Dashboard from '../views/Dashboard.vue'
import Tickets from '../views/Tickets.vue'
import Fleet from '../views/Fleet.vue'
import Barangays from '../views/Barangays.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: AdminLogin
  },
  {
    path: '/dashboard',
    component: Dashboard
  },
  {
    path: '/tickets',
    name: 'Tickets',
    component: Tickets
  },
  {
  path: '/fleet',
  name: 'Fleet',
  component: Fleet
},
{
  path: '/barangays',
  name: 'Barangays',
  component: Barangays
}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router