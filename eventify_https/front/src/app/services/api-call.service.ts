import { Injectable }			from '@angular/core';
import { Router }				from '@angular/router';
import { LoginService }			from './login.service';
import { SignupService }		from './signup.service';
import { NewEventService }		from './new-event.service';
import { EventsService }		from './events.service';
import {API}					from './ApiPath'
import { GeneralFuncService }	from './general-func.service';
import { resolve } from 'path';

@Injectable({
	providedIn: 'root'
})
export class ApiCallService {

	/*
		send GET request that refresh the JWT token trough the validation
		of the http only refresh token
	*/
	async refreshToken() : Promise<Response>
	{
		const res = await fetch(API.REFRESH_TOKEN, {
			credentials: 'include',
			method: "GET"
		})
		return (res)
	}

	/*
		FILE:			contain the profile picture selcted by the user,
		USERNAME:		username of related to the picture,
		INFORESPONSE	response object coming from API request that post new user info,
		DEEP			number that is used as flag to make the call to refresh token when needed

		send POST with the profile picture of the new created user
	*/
	async signupImage(file: File, username: string, infoResponse: Response,  deep?: number): Promise<Response>
	{
		const	pict = new FormData();
		const	responseJson = await infoResponse.json();

		pict.set("image", file);

		const pictRes = await fetch(API.POST_SIGNUP_IMAGE + `?token=${responseJson.uploadCode}`, {
			method: "POST",
			body: pict,
		})
		if (pictRes.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.signupImage(file, username, infoResponse, 1);
				else
					this.route.navigate(['/login']);
			})
		}
		return (pictRes);
	}

	/*
		NAME		the name of the tag to be removed
		DEEP		number that is used as flag to make the call to refresh token when needed

		send DELETE request that delete the tag idefied by NAME from db
	*/
	async removeTag(name: string, deep?:number) : Promise<boolean>
	{
		let trimmed: string = name.trim();
		const res = await fetch(API.DELETE_TAG + `?categoryName=${trimmed}`, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			method: "DELETE"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.removeTag(name, 1);
				else
					this.route.navigate(['/login'])
			})
		}
		if (res.ok)
			return (true);
		return false;
	}

	/*
		NAME			name of the tage to be modified
		MODIFIEDTAG		value of the modified tag
		DEEP			number that is used as flag to make the call to refresh token when needed

		send PUT request that modify the tage identified by NAME replacing it with
		MODIFIEDTAG value
	*/
	async modifyTag(name: string, modifiedTag: string, deep?: number) : Promise<boolean>
	{
		const res = await fetch(API.PUT_MODIFY_TAG + "?categoryName=" + name, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString(),
				"Content-Type": "application/json",
			},
			body: JSON.stringify({
				"categoryName": modifiedTag,
			}),
			method: "PUT"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.modifyTag(name, modifiedTag, 1);
				else
					this.route.navigate(['/login'])
			})
		}
		if (res.ok)
			return (true);
		return false;
	}

	/*
		NEWTAGS		JSON like value that contain all the new tag to be added,
		DEEP		number that is used as flag to make the call to refresh token when needed

		send POST request with NEWTAGS inside the body that add the new tag to db
	*/
	async addTags(newTags: {'categories': Array<string>}, deep?: number) : Promise<boolean>
	{
		const res = await fetch(API.POST_ADD_TAGS, {
			credentials: 'include',
			method: "POST",
			headers:{
				"authorization": "Bearer " + this.gFunc.getJwtString(),
				"Content-Type": "application/json"
			},
			body: JSON.stringify(newTags)
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.addTags(newTags, 1);
				else
					this.route.navigate(['/login'])
			})
		}
		if (res.ok)
			return (true);
		return false;
	}

	/*
		DATA	all info entered by the user from signup form
		DEEP	number that is used as flag to make the call to refresh token when needed

		send POST request with all new user info inside the body
		in JSON format and call another func to send profile pict to server
	*/
	async signup(data: any, deep?: number) : Promise<Response>
	{
		const dataRes = await fetch(API.POST_SIGNUP_INFO, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(data),
			credentials: 'include'
		})
		if (dataRes.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.signup(data, 1);
			})
		}
		return (dataRes);
	}

	/*
		DATA	contain username and password to be sent and validate
		DEEP	number that is used as flag to make the call to refresh token when needed

		send POST request with input username and crypted password
		(SHA256) inside the body in JSON format
	*/
	async login(data: any, deep?: number) : Promise<Response>
	{
		const res = await fetch(API.POST_LOGIN, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				"Content-Length": String(JSON.stringify(data).length),
			},
			credentials: 'include',
			body: JSON.stringify(data),
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.login(data, 1);
			})
		}
		return (res);
	}

	/*
		LIST	list of string that store the URL of the images once has been retrieved from the served
		INDEX	number that is use d as index to access LIST var
		DEEP	number that is used as flag to make the call to refresh token when needed

		send GET request asking for an initial set of event card
		to display in the home-event view
	*/
	async getEventImage(list: Array<string | null>, index: number, id: string) {
		fetch(API.GET_EVENT_IMAGE + `?eventId=${id}&imageNum=${index}`, {
			method: "GET",
		})
		.then((imageBlob)=>{
			if (!imageBlob.ok)
				return undefined
		  return imageBlob.blob()
		})
		.then((imageBlob: Blob | undefined)=>{
			if (imageBlob == undefined)
				list[index] = "/assets/imageBackup.jpg";
			else
				list[index]  = URL.createObjectURL(imageBlob);
		})
		.catch((err)=>{
			console.log("ERROR WHILE GETTING IMAGE ", err);
			list[index] = "/assets/imageBackup.jpg";
		})
	}

	/*
		USERNAME		variable that store the username that will be used to search his photo from db
		USERIMAGE		list of string that store the URL of the images once has been retrieved from the served
		INDEX			number that is use d as index to access USERIMAGE var

		send GET request that retrive image of specified user passing its username
	*/
	async getUserImage(username: string, userImage: Array<string | null>, index?: number) {
		fetch(API.GET_USER_IMAGE + `?username=${username}`, {
			method: "GET",

			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
		})
		.then((imageBlob)=>{
			if (!imageBlob.ok)
			{
				userImage[0] = "/assets/backUpProfilePict.png";
				return undefined
			}
		  	return imageBlob.blob()
		})
		.then((imageBlob: Blob | undefined)=>{
			if (index != undefined)
			{
				if (imageBlob == undefined)
					userImage[index] = "/assets/backUpProfilePict.png";
				else
					userImage[index] = URL.createObjectURL(imageBlob);
			}
			else if (index == undefined && imageBlob == undefined)
				userImage[0] = "/assets/backUpProfilePict.png";
			else if (imageBlob != undefined)
				userImage[0] = URL.createObjectURL(imageBlob);
		})
		.catch((err)=>{
			console.log("ERROR WHILE GETTING IMAGE ", err);
			userImage[0] = "/assets/backUpProfilePict.png";
		})
	}

	/*
		PAGE	somethong like a counter that is incremented everytime the "infinite scroll" is called
		DEEP	number that is used as flag to make the call to refresh token when needed

		send GET request that retrive a first set of events from database and is called again
		everytime you need more events
	*/
	async getBaseEvents(page: string, deep?: number): Promise<typeof this.events.events | []>
	{
		const res = await fetch(API.GET_INITIAL_EVENTS + "?page=" + page + "&size=10", {
			credentials: 'include',
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getBaseEvents(page, 1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (res.ok)
		{
			let data = await res.json();
			return (data);
		}
		return ([])
	}

	/*
		DEEP	number that is used as flag to make the call to refresh token when needed

		send GET request asking for all the event where the current
		user is registered
	*/
	async getRegisteredEvents(deep?: number): Promise<JSON[] | []>
	{
		const res = await fetch(API.GET_REGISTERED_EVENTS, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getRegisteredEvents(1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (res.ok)
			return (await res.json())
		return ([])
	}

	async getOwnCreatedEvents(deep?: number): Promise<JSON[] | []>
	{
		const res = await fetch(API.GET_OWN_CREATED_EVENTS, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getRegisteredEvents(1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (res.ok)
			return (await res.json())
		return ([])
	}

	/*
		DEEP	number that is used as flag to make the call to refresh token when needed

		send GET request asking for a singlue user full info
		used in userinfo page
	*/
	async getUserInfo(deep?: number): Promise<JSON | null>
	{
		const res = await fetch(API.GET_USER_INFO + "?username=" + this.gFunc.getUserName(), {
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getUserInfo(1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (res.ok)
			return (await res.json())
		return (null)
	}

	/*
		DEEP	number that is used as flag to make the call to refresh token when needed

		send a GET request that retrive all the tags contained inside the db
	*/
	async getTags(deep?: number): Promise<JSON | null>
	{
		const res = await fetch(API.GET_TAGS, {
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getTags(1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (res.ok)
			return (await res.json())
		return (null)
	}

	/*
		DEEP	number that is used as flag to make the call to refresh token when needed
		ID		id value used to access the info related to a single event

		send POST asking for full details of (ID) event-card
		the body will contain the requested event-card's id
	*/
	async getSingleEvent(id: string, deep?: number): Promise<any>
	{
		const res = await fetch(API.GET_SINGLE_EVENT + "?id=" + id, {
			method: "GET",
			headers:{
				"authorization": "Bearer " + this.gFunc.getJwtString()
			}
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getSingleEvent(id, 1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (res.ok)
			return (await res.json())
		return ({})
	}

	/*
		IMAGES		variable that contain all the image coming from new event form
		ID			id that is used to link the IMAGES  to the right event
		DEEP		number that is used as flag to make the call to refresh token when needed

		send POST request with event-card related images
	*/
	async postImage(images: FormData, id: string, deep?: number): Promise<Response>
	{
		const imagePost = await fetch(API.POST_NEW_EVENT_IMAGES + `?eventId=${id}`, {
			method: "POST",
			headers:{
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			body: images,
		})
		if (imagePost.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.postImage(images, id, 1);
				else
					this.route.navigate(['/login'])
			})	
		}
		return (imagePost);
	}

	/*
		DATA		variable that contain all the info of the new event to be created
		IMAGES		variable that contain all the image coming from new event form
		DEEP		number that is used as flag to make the call to refresh token when needed

		send POST request for new event-card text data (JSON)
		related to the event-card and call the func that post
		card related images
	*/
	async createEvent(data: any, images: FormData, deep?: number): Promise<number>
	{
		const dataPost = await fetch(API.POST_NEW_EVENT_INFO, {
			method: "POST",
			headers: {
				"Content-Type": "application/json",
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			body: JSON.stringify(data),
		})
		if (dataPost.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.createEvent(data, images, 1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (dataPost.ok)
		{
			const resJson = await dataPost.json();
			const postImage = await this.postImage(images, resJson.id);

			if (postImage.ok)
				this.route.navigate(['/events']).then(()=>{window.location.reload()});
			else
			{
				this.route.navigate(['/events']);
				return (2);
			}
		}
		else
		{
			this.route.navigate(['/create-event']);
			return (1);
		}
		return (0)
	}

	/*
		IMAGES		variable that contain all the image coming from new event form
		DEEP		number that is used as flag to make the call to refresh token when needed
		ID			id that is used to link the IMAGES  to the right event

		send POST request with new photo that will be used to update IMAGES
		for the selected event (ID)
	*/
	async modifyEventImage(images: FormData, id: string,  deep?: number): Promise<Response>
	{
		const imagePost = await fetch(API.POST_NEW_EVENT_IMAGES + `?eventId=${id}`, {
			method: "POST",
			headers:{
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			body: images,
		})
		if (imagePost.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.modifyEventImage(images, id, 1);
				else
					this.route.navigate(['/login'])
			})	
		}
		return (imagePost);
	}

	/*
		DATA		variable that contain all the info of the new event to be created
		IMAGES		variable that contain all the image coming from new event form
		ID			id that is used to link the IMAGES  to the right event
		DEEP		number that is used as flag to make the call to refresh token when needed


		send PUT request with new photo and data that will be used to update IMAGES and info
		for the selected event (ID)
	*/
	async modifyEvent(data: any, images: FormData, id: string, deep?: number): Promise<number>
	{
		const dataPost = await fetch(API.POST_MODIFIED_EVENT_INFO + `?id=${id}`, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json",
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			body: JSON.stringify(data),
		})
		if (dataPost.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.modifyEvent(data, images, id, 1);
				else
					this.route.navigate(['/login'])
			})	
		}
		if (dataPost.ok)
		{
			if (!images.has("image"))
			{
				this.route.navigate(['/events']);
				return (0);
			}
			const postImage = await this.modifyEventImage(images, id);
			if (postImage.ok)
				this.route.navigate(['']);
			else
			{
				this.route.navigate(['/events']);
				return (1);
			}
		}
		else
		{
			this.route.navigate([`/modify-event/${id}`]);
			return (1);
		}
		return (0)
	}

	/*
		DATA		variables that store the modified value that will be used to update user info
		DEEP		number that is used as flag to make the call to refresh token when needed

		send PUT request with all new info of the user used from the backend to update the value
	*/
	async updateUserInfo(data: any, deep?: number): Promise<Response>
	{
		const userInfoRes = await fetch(API.PUT_NEW_USER_INFO, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json",
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			body: JSON.stringify(data),
		})
		if (userInfoRes.ok)
			this.route.navigate([''])
		if (userInfoRes.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.updateUserInfo(data, 1);
				else
					this.route.navigate(['/login']);
			})	
		}
		return (userInfoRes);
	}

	/*
		DATA		variables that store the old and the modified password
		DEEP		number that is used as flag to make the call to refresh token when needed

		send PUT request with the old and new password of the user used from the backend to update the value
	*/
	async updatePassword(data: any, deep?: number): Promise<Response>
	{
		const passwordRes = await fetch(API.PUT_NEW_USER_PASSWORD, {
			method: "PUT",
			headers: {
				"Content-Type": "application/json",
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			body: JSON.stringify(data),
		})
		if (passwordRes.ok)
			this.route.navigate([''])
		if (passwordRes.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.updatePassword(data, 1);
				else
					this.route.navigate(['/login']);
			})	
		}
		return (passwordRes);
	}

	/*
		FILE		variable that store the new profile picture
		USERNAME	variable that store the current user's username
		DEEP		number that is used as flag to make the call to refresh token when needed

		send a POST request with new picture that will be used from server to update the profile picture
	*/
	async updateProfilePicture(file: File | null | undefined, username: string, deep?: number): Promise<Response>
	{
		const pictFormData = new FormData();

		pictFormData.set("image", file!)
		const imagePost = await fetch(API.POST_NEW_USER_PICTURE, {
			method: "POST",
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			body: pictFormData,
		})
		if (imagePost.ok)
			window.location.reload();
		if (imagePost.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.updateProfilePicture(file, username, 1);
				else
					this.route.navigate(['/login']);
			})
		}
		return (imagePost);
	}

	/*
		DEEP		number that is used as flag to make the call to refresh token when needed

		send GET request that retrieve all registered user from db
	*/
	async getUser (deep?: number): Promise<JSON>
	{
		const userRes = await fetch(API.GET_USER_LIST_ADMIN, {
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			method: "GET"
		})
		if (userRes.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getUser(1);
				else
					this.route.navigate(['/login']);
			})	
		}
		if (userRes.ok)
			return (await userRes.json());
		else
			return ({} as JSON);
	}

	/*
		USERNAMETODELETE	username that will be used from server to know what user to delete

		send DELETE request with a username that need to be deleted
	*/
	async deleteUser(usernameToDelete: string)
	{
		if (!confirm(`Do you really want to delete this user (${usernameToDelete})`))
			return ;
		fetch(API.DELETE_USER + `?username=${usernameToDelete}`, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			method: "DELETE"
		})
		.then(()=>{
			window.location.reload();
		})
		.catch((err)=>{
			alert("something went wrong...");
			console.log("ERROR WHILE REMOVING USER ", err);
		})
	}

	/*
		EVENTIDTODELETE		event-id that will be used from server to know which event to delete
		EVENTTITLE			title of the event that will be deleted

		send DELETE request with a event id that will be used to deleted the requested event
	*/
	async deleteEvent(eventIdToDelete: string, eventTitle: string)
	{
		if (!confirm(`Do you really want to delete this user (${eventTitle})`))
			return ;
		fetch(API.DELETE_EVENT + `?id=${eventIdToDelete}`, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			method: "DELETE"
		})
		.then((data)=>{
			if (data.ok)
			{
				this.route.navigate(['']).then(()=>{
					window.location.reload();
				})
			}
		})
		.catch((err)=>{
			alert("something went wrong...");
			console.log("ERROR WHILE REMOVING EVENT ", err);
		})
	}

	/*
		send POST request that simply ask to backend to marke the current user as logged out
		and then delete the current JWT stored in browser
	*/
	async logout()
	{
		fetch(API.POST_LOGOUT, {
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			method: "POST"
		}).then((res)=>{
			if (res.ok)
			{
				document.cookie = ' authorization=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
				this.route.navigate(['']).then(()=>{
				  window.location.reload()
				});
			}
			else
			{
				alert("something went wrong... retry");
				this.route.navigate(['']).then(()=>{
					window.location.reload()
				  });
			}
		}).catch((err)=>{
			console.log("ERROR WHILE LOGGING OUT: ", err)
		})
	}

	async search(type: string, title?: string, place?: string, timeFrom?: string, timeTo?: string, categories?: string[]) : Promise<typeof this.events.events | []>
	{
		let categoryList: string = "";
		let res;

		if (categories != undefined)
		{
			for (let el of categories)
				categoryList += (el + ",");
			categoryList = categoryList.substring(0, categoryList.length - 1);
		}
		res = await fetch(API.GET_SEARCH_EVENT + `?type=${type}&${title != undefined ? "search=" + title : ""}
		${place != undefined ? "search=" + place : ""}
		${categories != undefined ? "categories=" + categoryList : ""}
		${timeFrom != undefined ? "from=" + timeFrom : ""}
		${timeTo != undefined && timeFrom == undefined ? "to=" + timeTo : ""}
		${timeTo != undefined && timeFrom != undefined ? "&to=" + timeTo : ""}`,{
			method: "GET"
		})
		if (res.ok)
		{
			let js = await res.json();
			return (js)
		}
		else
		{
			console.log("ERROR IN SEARCH...");
			return ([]);
		}
	}
	async subscribeEvent(id: string, deep?: number)
	{
		fetch(API.PUT_REGISTER_TO_EVENT + `?eventId=${id}`, {
			method: "PUT",
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
		}).then(data=>{
			if (data.status == 401 && deep == undefined)
			{
				this.refreshToken().then((res)=>{
					if (res.ok)
						this.subscribeEvent(id, 1);
					else
						this.route.navigate(['/login']);
				})	
			}
			if (data.ok)
				window.location.reload()
		}).catch(err=>{
			console.log("ERROR WHILE UNSUBSCRIBING...", err)
		})
	}

	async unsubscribeEvent(id: string, deep?: number)
	{
		fetch(API.PUT_UNREGISTER_TO_EVENT + `?eventId=${id}`, {
			method: "PUT",
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
		}).then(data=>{
			if (data.status == 401 && deep == undefined)
			{
				this.refreshToken().then((res)=>{
					if (res.ok)
						this.subscribeEvent(id, 1);
					else
						this.route.navigate(['/login']);
				})	
			}
			if (data.ok)
				window.location.reload()
		}).catch(err=>{
			console.log("ERROR WHILE UNSUBSCRIBING...", err)
		})
	}

	async blockUser(usernameToBlock: string)
	{
		fetch(API.PUT_BLOCK_USER + `?username=${usernameToBlock}`, {
			method: "PUT",
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
		}).then(data=>{
			if (data.ok)
				window.location.reload()
		}).catch(err=>{
			console.log("ERROR WHILE BLOCKING USER...", err)
		})
	}

	async unblockUser(usernameToUnblock: string)
	{
		fetch(API.PUT_UNBLOCK_USER + `?username=${usernameToUnblock}`, {
			method: "PUT",
			credentials: 'include',
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
		}).then(data=>{
			if (data.ok)
				window.location.reload()
		}).catch(err=>{
			console.log("ERROR WHILE BLOCKING USER...", err)
		})
	}
	
	async getNotification(deleteNotification: string, deep?: number) : Promise<JSON[]>
	{
		const res = await fetch(API.GET_NOTIFICATION + `?delete=${deleteNotification}`, {
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getNotification(deleteNotification, 1);
			})	
		}
		if (res.ok)
		{
			if (deleteNotification == "true")
				window.location.reload()
			let tempJson = await res.json()
			return tempJson;
		}
		return (JSON.parse("[]"))
	}

	async getEventImminent(deep?: number) : Promise<JSON[]>
	{
		const res = await fetch(API.GET_IMMINENT_EVENT, {
			headers: {
				"authorization": "Bearer " + this.gFunc.getJwtString()
			},
			credentials: 'include',
			method: "GET"
		})
		if (res.status == 401 && deep == undefined)
		{
			this.refreshToken().then((res)=>{
				if (res.ok)
					this.getEventImminent(1);
			})	
		}
		if (res.ok)
		{
			let tempJson = await res.json()
			return tempJson;
		}
		return (JSON.parse("[]"))
	}
	constructor(public route: Router, public newEvent: NewEventService, public events: EventsService, protected gFunc: GeneralFuncService) { }
}
