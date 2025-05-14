import { Routes } from '@angular/router';
import {UserListComponent} from './user-management/pages/user-list/user-list.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';
import {LoginComponent} from './user-management/pages/login/login.component';
import {SignUpComponent} from './user-management/pages/sign-up/sign-up.component';
import {EditUserComponent} from './user-management/pages/edit-user/edit-user.component';

export const routes: Routes = [
  {
    path: 'admin/users',
    children: [
      {
        path: '',
        component: UserListComponent
      },
      {
        path: ':id/edit',
        component: EditUserComponent,
        runGuardsAndResolvers: 'paramsOrQueryParamsChange'
      }
    ]
  }
  ,
  { path: 'explore', component: ExploreLayoutComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignUpComponent },
  { path: '', redirectTo: '/explore', pathMatch: 'full' } //default page, to be changed
];
