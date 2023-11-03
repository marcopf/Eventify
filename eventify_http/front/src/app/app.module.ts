import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SignupComponent } from './signup/signup.component';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import {FormsModule} from '@angular/forms';
import { NavbarComponent } from './navbar/navbar.component';
import { EventCardComponent } from './event-card/event-card.component';
import { CardViewComponent } from './card-view/card-view.component';
import { SearchBarComponent } from './search-bar/search-bar.component';
import { AdminUserComponent } from './admin-user/admin-user.component';
import { EventDisplayComponent } from './event-display/event-display.component';
import { NewEventComponent } from './new-event/new-event.component';
import { ModifyEventComponent } from './modify-event/modify-event.component';
import { LoaderComponent } from './loader/loader.component';
import { ServiceWorkerModule } from '@angular/service-worker'
import { ReactiveFormsModule } from '@angular/forms';
import { UserInfoComponent } from './user-info/user-info.component';
import { AdminTagPanelComponent } from './admin-tag-panel/admin-tag-panel.component';
import { FooterComponent } from './footer/footer.component';
import { UserRegisteredEventsComponent } from './user-registered-events/user-registered-events.component';
import { SmallLoaderComponent } from './small-loader/small-loader.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { EmailAlertComponent } from './email-alert/email-alert.component';
import { NotificationViewComponent } from './notification-view/notification-view.component';


@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    LoginComponent,
    HomeComponent,
    NavbarComponent,
    EventCardComponent,
    CardViewComponent,
    SearchBarComponent,
    AdminUserComponent,
    EventDisplayComponent,
    NewEventComponent,
    ModifyEventComponent,
    LoaderComponent,
    UserInfoComponent,
    AdminTagPanelComponent,
    FooterComponent,
    UserRegisteredEventsComponent,
    SmallLoaderComponent,
    ErrorPageComponent,
    EmailAlertComponent,
    NotificationViewComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    BrowserAnimationsModule
  ],
  bootstrap: [AppComponent],
})
export class AppModule { }
