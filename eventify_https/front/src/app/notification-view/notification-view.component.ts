import { Component } from '@angular/core';
import { ApiCallService } from '../services/api-call.service';
import { NotificationService } from '../services/notification.service';
import { GeneralFuncService } from '../services/general-func.service';

@Component({
  selector: 'app-notification-view',
  templateUrl: './notification-view.component.html',
  styleUrls: ['./notification-view.component.css']
})
export class NotificationViewComponent {
  constructor(protected api: ApiCallService, protected notification: NotificationService, protected gFunc: GeneralFuncService){
    this.notification.getNotification()
  }
}
