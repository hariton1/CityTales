import { Routes } from '@angular/router';
import {UserListComponent} from './user-management/pages/user-list/user-list.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';
import {LoginComponent} from './user-management/pages/login/login.component';
import {SignUpComponent} from './user-management/pages/sign-up/sign-up.component';

export const routes: Routes = [
  { path: 'admin/user-list', component: UserListComponent },
  { path: 'explore', component: ExploreLayoutComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignUpComponent },
  { path: '', redirectTo: '/explore', pathMatch: 'full' } //default page, to be changed
];
