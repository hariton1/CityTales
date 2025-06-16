import { Routes } from '@angular/router';
import {UserListComponent} from './user-management/pages/user-list/user-list.component';
import {ExploreLayoutComponent} from './layout/explore-layout/explore-layout.component';
import {LoginComponent} from './user-management/pages/login/login.component';
import {SignUpComponent} from './user-management/pages/sign-up/sign-up.component';
import {EditUserComponent} from './user-management/pages/edit-user/edit-user.component';
import {OnboardingLoginComponent} from './onboarding/pages/onboarding-login/onboarding-login.component';
import {EditInterestsComponent} from './user-interests/edit-interests/edit-interests.component';
import {FeedbackComponent} from './user-feedback/pages/feedback/feedback.component';
import {TourLayoutComponent} from './layout/tours/tour-layout-component/tour-layout-component';
import {TourDetailComponent} from './layout/tours/tour-detail/tour-detail.component';
import {UserGamesComponent} from './user-games/pages/landing-page/user-games.component';
import {LandingPageComponent} from './user-friends/landing-page/landing-page.component';
import {NotificationInboxComponent} from './core/notification-inbox/notification-inbox.component';
import {FeedbackListComponent} from './user-feedback/pages/feedback-list/feedback-list.component';
import {UserProfileComponent} from './user-management/pages/user-profile/user-profile.component';
import {ResetPasswordComponent} from './user-management/pages/reset-password/reset-password.component';
import {HistoryOneUserComponent} from './user-history/pages/history-one-user/history-one-user.component';
import {GameQuizComponent} from './layout/game-quiz/game-quiz/game-quiz.component';
import {AdminDashboardComponent} from './layout/admin-dashboard/admin-dashboard.component';
import {HistoryAllUsersComponent} from './user-history/pages/history-all-users/history-all-users.component';
import {AboutComponent} from './core/about/about.component';
import {FunFactListComponent} from './saved-fun-facts/pages/fun-fact-list/fun-fact-list.component';
import {OnboardingSignUpComponent} from './onboarding/pages/onboarding-sign-up/onboarding-sign-up.component';

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
  { path: 'tours/:id', component: TourDetailComponent},
  { path: 'login', component: LoginComponent },
  { path: 'register', component: SignUpComponent },
  { path: 'onboarding', component: OnboardingLoginComponent },
  { path: 'first-steps', component: OnboardingSignUpComponent },
  { path: 'feedback', component: FeedbackComponent },
  { path: 'feedback-list', component: FeedbackListComponent },
  { path: 'edit-interests', component: EditInterestsComponent },
  { path: 'scoreboard', component: UserGamesComponent },
  { path: 'inbox', component: NotificationInboxComponent },
  { path: 'friends', component: LandingPageComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: 'reset-pass', component: ResetPasswordComponent },
  { path: 'my-history', component: HistoryOneUserComponent },
  { path: 'fun-fact-list', component: FunFactListComponent },
  { path: 'history', component: HistoryAllUsersComponent },
  { path: 'quizzes', component: GameQuizComponent },
  { path: 'admin-dashboard', component: AdminDashboardComponent },
  { path: 'about-us',
    loadComponent: () => import("./core/about/about.component").then(about => about.AboutComponent)},
  { path: 'home',
    loadComponent: () => import("./core/home/home.component").then(home => home.HomeComponent)},
  { path: '', redirectTo: '/home', pathMatch: 'full' } //default page
];
