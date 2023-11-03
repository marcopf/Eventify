import { Injectable } from '@angular/core';
import { ApiCallService } from './api-call.service';

@Injectable({
	providedIn: 'root'
})
export class NotificationService {
	isInboxFull:	boolean = false;
	imminentEvent:	any;
	notification:	any;

	//ask for notification about event status e imminent event to backend passing false so 
	//that the notification is not deleted from db 
	getNotification()
	{
		this.api.getEventImminent().then(data=>{
			this.imminentEvent = data;
		})
		this.api.getNotification("false").then(data=>{
			let notification: JSON;
			if (data.length > 0)
				this.isInboxFull = true;
			else
				this.isInboxFull = false;
			this.notification = data;
			for (notification of data)
			{
				let message = JSON.parse(JSON.stringify(notification))
				Notification.requestPermission().then(perm=>{
				if (perm === "granted")
					new Notification(message.message, {body: message.body})
				})
			}
		})
	}
	constructor(protected api: ApiCallService) { }
}
