import { Component, HostListener, OnInit } from '@angular/core';
import { ApiCallService } from './services/api-call.service';
import { NotificationService } from './services/notification.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  small: boolean = window.innerWidth < 1000;
  notificationNeeded: boolean = true;
  title = 'Eventify';

  @HostListener('window:resize') onresize()
  {
    console.log(window.innerWidth)
    if (!this.small && window.innerWidth < 1000)
    {
      document.location.reload()
      this.small = true;
    }
    if (this.small && window.innerWidth >= 1000)
    {
      document.location.reload()
      this.small = false;
    }
  }
  constructor(protected api: ApiCallService, protected notification: NotificationService){}
}
