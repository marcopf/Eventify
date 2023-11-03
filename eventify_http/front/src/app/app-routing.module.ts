import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { SignupComponent } from './signup/signup.component';
import { HomeComponent } from './home/home.component';
import { CardViewComponent } from './card-view/card-view.component';
import { AdminUserComponent } from './admin-user/admin-user.component';
import { EventDisplayComponent } from './event-display/event-display.component';
import { NewEventComponent } from './new-event/new-event.component';
import { ModifyEventComponent } from './modify-event/modify-event.component';
import { UserInfoComponent } from './user-info/user-info.component';
import { UserRegisteredEventsComponent } from './user-registered-events/user-registered-events.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { EmailAlertComponent } from './email-alert/email-alert.component';
import { NotificationViewComponent } from './notification-view/notification-view.component';




const routes: Routes = [
  {path: "signin", component: SignupComponent},
  {path: "emailAlert", component: EmailAlertComponent},
  {path: "notification", component: NotificationViewComponent},
  {path: "login", component: LoginComponent},
  {path: "cardView/:id", component: CardViewComponent},
  {path: "cardView", component: EventDisplayComponent},
  {path: "users", component: AdminUserComponent},
  {path: "events", component: EventDisplayComponent},
  {path: "create-event", component: NewEventComponent},
  {path: "modify-event/:id", component: ModifyEventComponent},
  {path: "userInfo", component: UserInfoComponent},
  {path: "registered-events", component: UserRegisteredEventsComponent},
  {path: "", component: HomeComponent},
  {path: "page/error/not-found", component: ErrorPageComponent},
  {path: '**', redirectTo: '/page/error/not-found', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
