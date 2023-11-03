import { Component } from '@angular/core';
import { ApiCallService } from '../services/api-call.service';
import { sha256 } from 'js-sha256';
import { SignupService } from '../services/signup.service';
import { GeneralFuncService } from '../services/general-func.service';

interface userinfo{
	username: string,
	firstName: string,
	lastName: string,
	email: string,
	date: string
}

interface errors{
	emailErr: boolean,
	dateErr: boolean,
	firstNameErr: boolean,
	lastNameErr: boolean,
	passErr: boolean
}

function inputCheck(signup:SignupService, data: any, errors: errors)
{
	!data['email'].match(signup.emailReg) ? errors.emailErr = true : errors.emailErr = false;
	data['firstName'] == "" ? errors.firstNameErr = true : errors.firstNameErr = false;
	data['lastName'] == "" ? errors.lastNameErr = true : errors.lastNameErr = false;
	return (!errors.dateErr && !errors.emailErr && !errors.firstNameErr && !errors.lastNameErr)
}

function resetErr(err: errors)
{
	err.emailErr = false;
	err.dateErr = false;
	err.firstNameErr = false;
	err.lastNameErr = false;
	err.passErr = false;
}

@Component({
	selector: 'app-user-info',
	templateUrl: './user-info.component.html',
	styleUrls: ['./user-info.component.css']
})
export class UserInfoComponent {
	toggleInfo: boolean = false;
	togglePass: boolean = false;
	isLoaded: 	boolean = false; 
	imageUrl: 	Array<string | null> = [null];
	errors: 	errors ={
		emailErr: false,
		dateErr: false,
		firstNameErr: false,
		lastNameErr: false,
		passErr: false
	}
	userInfo: 	userinfo = {
		username: "",
		firstName: "",
		lastName: "",
		email: "",
		date: ""
	}

	//function that make the change user info form visible when in desktop view
	openInfo()
	{
		if (this.togglePass)
			this.openPassword()
		const infoEl = document.querySelector(".left") as HTMLDivElement;
		const leftEl = document.querySelector(".openLeft") as HTMLDivElement;
		const optEl = document.querySelector(".options") as HTMLDivElement;

		if (!this.toggleInfo)
		{
			infoEl.style.transform = "translateX(50svw) translateY(-50%)"
			leftEl.style.transform = "translateX(50svw) translateY(-50%)"
			optEl.style.transform = "translateX(20svw)"
			this.toggleInfo = true
		}
		else
		{
			infoEl.style.transform = "translateX(0) translateY(-50%)"
			leftEl.style.transform = "translateX(0) translateY(-50%)"
			optEl.style.transform = "translateX(0)"
			this.toggleInfo = false
		}
	}

	//function that make the change password form visible when in desktop view
	openPassword()
	{
		const infoEl = document.querySelector(".right") as HTMLDivElement;
		const rightEl = document.querySelector(".openRight") as HTMLDivElement;
		const optEl = document.querySelector(".options") as HTMLDivElement;

		if (this.toggleInfo)
			this.openInfo()
		if (!this.togglePass)
		{
			infoEl.style.transform = "translateX(-80svw) translateY(-50%)"
			rightEl.style.transform = "translateX(-80svw) translateY(-50%)"
			optEl.style.transform = "translateX(20svw)"
			this.togglePass = true
		}
		else
		{
			infoEl.style.transform = "translateX(0svw) translateY(-50%)"
			rightEl.style.transform = "translateX(0) translateY(-50%)"
			optEl.style.transform = "translateX(0)"
			this.togglePass = false
		}
	}

	//call the api to change passoword for current user
	submitPassword()
	{
		const formEl = document.querySelector("#passwordForm") as HTMLFormElement;
		const formData = new FormData(formEl);
		const rightEl = document.querySelector('.right') as HTMLDivElement;
		const data: { [key: string]: any } = {};
		
		formData.set("username", this.userInfo.username);
		formData.set("firstName", this.userInfo.firstName);
		formData.set("lastName", this.userInfo.lastName);
		formData.set("email", this.userInfo.email);
		formData.forEach((value, key) => {
			if ((key == "password" || key == "newPassword") && !this.signup.validate(String(value)))
				this.errors.passErr = true
			data[key] = value;
		});
		if (this.errors.passErr || data['newPassword'] != data['rNewPassword'])
		{
			rightEl.style.backgroundColor = "var(--err-col)"
			this.errors.passErr = true
		}
		else
		{
			data['newPassword'] = sha256(data['newPassword']);
			data['password'] = sha256(data['password']);
			delete data['rNewPassword']
			resetErr(this.errors);
			this.api.updatePassword(data)
			.then((data)=>{

			}).catch((err)=>{
				console.log("ERROR WHILE UPDATING PASSWORD ", err);
			});
		}
	}

	//call the api to change user info like name or email
	submitInfo()
	{
		const 	formEl = document.querySelector("#infoForm") as HTMLFormElement;
		const 	leftEl = document.querySelector(".left") as HTMLFormElement;
		const 	formData = new FormData(formEl);
		const 	data: { [key: string]: any } = {};

		this.errors.emailErr = false;
		this.errors.dateErr = false;
		formData.set("username", this.userInfo.username);
		formData.forEach((value, key) => {
			data[key] = value;
		});
		if (inputCheck(this.signup, data, this.errors))
		{
			this.api.updateUserInfo(data).then((data)=>{

			}).catch((err)=>{
				console.log("ERROR UPDATING USER INFO ", err);
			});
			leftEl.style.backgroundColor = "transparent";
		}
		else
			leftEl.style.backgroundColor = "var(--err-col)";
	}

	//callthe api to submit new photo for current user
	submitPhoto()
	{
		const rawPictForm = document.querySelector("#pict") as HTMLInputElement;
		const file = rawPictForm.files?.item(0);
		const pictFormData = new FormData();

		this.api.updateProfilePicture(file, this.signup.signin.surname).then((data)=>{
		}).catch((err)=>{
			console.log("ERROR WHILE UPDATING USER PHOTO", err);
		});
	}

	constructor(protected signup: SignupService, protected api: ApiCallService, protected gFunc: GeneralFuncService){
		this.api.getUserInfo().then((data)=>{
			this.userInfo = data as unknown as userinfo;
			this.isLoaded = true;
			this.api.getUserImage(this.gFunc.getUserName(), this.imageUrl).then((data)=>{
			})
		}).catch((err)=>{
			console.log("ERROR GETTING USER INFO ", err);
		})
	}
}
