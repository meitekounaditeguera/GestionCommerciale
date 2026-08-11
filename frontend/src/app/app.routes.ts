import { Routes } from '@angular/router';

import { LoginComponent } from './components/login/login';
import { HomeComponent } from './components/home/home';
import { DashboardComponent } from './components/dashboard/dashboard';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ROLE_ADMIN', 'ROLE_GESTIONNAIRE'] }
  },
  { path: '', component: HomeComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' }
];
