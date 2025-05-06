import { Routes } from '@angular/router';
import {UserListComponent} from './user-management/pages/user-list/user-list.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';

export const routes: Routes = [
  { path: 'admin/user-list', component: UserListComponent },
  { path: 'explore', component: ExploreLayoutComponent },
  { path: '', redirectTo: '/explore', pathMatch: 'full' } //default page, to be changed
];
