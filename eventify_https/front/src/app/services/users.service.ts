import { Injectable } from '@angular/core';
import { ApiCallService } from './api-call.service';

@Injectable({
	providedIn: 'root'
})
export class UsersService {
	users:		any = [];
	isLoaded:	boolean = false;
	imagesUrls:	Array<string | null> = [];

	constructor(public api: ApiCallService) { 
		this.api.getUser()
		.then(data =>{
			this.users = data;
			this.isLoaded = true;
			for (let i = 0; i < this.users.length; i++)
			{
				this.imagesUrls.push(null);
				this.api.getUserImage(this.users[i].username, this.imagesUrls, i)
				.catch((err)=>{
					console.log("ERROR WHILE GETTING USER IMAGES", err);
				})
			}
		})
		.catch((err)=>{
			console.log("ERROR WHILE GETTING USERS LIST", err)
		})
	}
}
