import { Injectable } from '@angular/core';
import { ApiCallService } from './api-call.service';
import { Router } from '@angular/router';
import { sha256 } from 'js-sha256';

//once small pre-check is done that api for login is called and username and password
//are passed inside the body
function prepareAndPerformLogin(login: login_interface, api: ApiCallService, route: Router, dup_this: LoginService)
{
	const formEl = document.querySelector("#loginForm") as HTMLFormElement;
	const formData = new FormData(formEl);
	const data: { [key: string]: any } = {};

	formData.delete('password');
	formData.set('password', sha256(login.password));
	formData.forEach((value, key) => {
		data[key] = value;
	});
	api.login(data).then((data)=>{
		if (data.ok)
		{
			login.password = "";
			login.username = "";
			route.navigate(['/events']).then(()=>{
				window.location.reload();
			});
			dup_this.loginError = false;
		}
		else
		{
			dup_this.loginError = true;
			formEl.classList.toggle("shake");
			setTimeout(() => {formEl.classList.toggle("shake");}, 500);
		}
	})
	.catch((err)=>{
		console.log("LOGIN ERROR: ", err);
	})
}

interface login_interface {
	username: string,
	password: string,
}

@Injectable({
	providedIn: 'root'
})
export class LoginService{
	error:		boolean = false;
	error_text:	Array<string> = [];
	loginError:	boolean = false;
	login:		login_interface={
		username: "",
		password: "",
	}

	//PERFOM ALL THE CHECK ON INPUT AND MAKE THE CALL TO RELATIVE API IF ALL IS OK
	loginSubmit($event: Event)
	{
		const formEl = document.querySelector("#loginForm") as HTMLFormElement;
		$event.preventDefault();

		if (this.login.password != "" && this.login.username != "")
			prepareAndPerformLogin(this.login, this.api, this.route, this)
		else
		{
			formEl.classList.toggle("shake");
			setTimeout(() => {
				formEl.classList.toggle("shake");
			}, 500);
			this.loginError = true;
		}
	}
	constructor(protected api: ApiCallService, protected route: Router) { }
}