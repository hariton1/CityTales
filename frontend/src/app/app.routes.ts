import { Routes } from '@angular/router';
import {UserListComponent} from './user-management/pages/user-list/user-list.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';
import {LoginComponent} from './user-management/pages/login/login.component';
import {SignUpComponent} from './user-management/pages/sign-up/sign-up.component';
import {EditUserComponent} from './user-management/pages/edit-user/edit-user.component';
import {OnboardingComponent} from './user-interests/onboarding/onboarding.component';
import {EditInterestsComponent} from './user-interests/edit-interests/edit-interests.component';
import {FeedbackComponent} from './user-feedback/pages/feedback/feedback.component';
import {TourLayoutComponent} from './layout/tours/tour-layout-component/tour-layout-component';

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
  { path: 'tours', component: TourLayoutComponent},
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignUpComponent },
  { path: 'onboarding', component: OnboardingComponent },
  { path: 'feedback', component: FeedbackComponent },
  { path: 'edit-interests', component: EditInterestsComponent },
  { path: '', redirectTo: '/explore', pathMatch: 'full' } //default page, to be changed
];
