import { Injectable } from '@angular/core';
import { sha256 } from 'js-sha256';
import { ApiCallService } from './api-call.service';
import { Route, Router } from '@angular/router';

type signin_interface = {
	name:             string,
	surname:          string,
	username:         string,
	email:            string,
	password:         string,
	confirm_password: string,
	date:             string;
	file:             File | null | undefined;
}

//do thing depending on the response from sign api call
//like routing to email confirm if call was successfull or trigger error when needed 
function handleSignupRequest(data: Response, dup_this: SignupService, formEl: HTMLFormElement, api: ApiCallService, route: Router)
{
	if (data.ok)
	{
		api.signupImage(dup_this.signin.file as File, dup_this.signin.username, data, 0).then((data)=>{
			dup_this.signUpError = false;
			route.navigate(['/emailAlert']).then(()=>{
			});
		})
	}
	else
	{
		formEl.classList.toggle("shake");
		setTimeout(() => {formEl.classList.toggle("shake")}, 500);
		dup_this.signUpError = true;
	}
}

@Injectable({
	providedIn: 'root'
})
export class SignupService {
	emailReg:     RegExp  =/(?:[a-z0-9!#$%&'*+/=?^_{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/;
	error:        boolean = false;
	errors:       Array<boolean> = [false, false, false, false]
	error_text:   Array<string> = [];
	signUpError:  boolean = false;
	signin:       signin_interface = {
		name: "",
		surname: "",
		username: "",
		email: "",
		password: "",
		confirm_password: "",
		date: "",
		file: null,
	}

	checkForError()
	{
		for (let el of this.errors)
		{
			if (el)
				return (true);
		}
		return (false);
	}

	performCheck()
	{
		let flag: boolean = true;

		if (!this.checkEmail())
			flag = false;
		if (!this.checkDate())
			flag = false;
		return flag;
	}

	public rcheckMsg(str:string)
	{
		let flag: number = 0;
		for (let el of this.error_text)
		if (el == str)
			flag = 1;
		if (!flag)
			this.error_text.push(str);
		return (true);
	}

	//REMOVE THE MESAGE FROM LIST IF IT'S FOUND (FOR REPEATED PASSWORD)
	public rremoveMsg(str: string)
	{
		let toDelete: number = -1;

		for (let i = 0; i < this.error_text.length; i++)
		{
			if (this.error_text[i] == str)
				toDelete = i
		}
		if (toDelete != -1)
			delete this.error_text[toDelete];
	}

	//simpli apply all the check returning true or false depending from check result
	public validate(str: string) : boolean
	{
		if ((str.length > 8 && str.length < 256)
			&& str.match(/[0123456789]/)
			&& str.match(/[!@#$%^&*()_+\-=ˆ\[\]{};:'",.<>?~]/)
			&& str.match(/[QWERTYUIOPASDFGHJKLZXCVBNM]/))
				return (true);
		return (false);
	}

	//function that check the presence of file and modify the selector
	//showing the selected file
	public checkFile(event: Event)
	{
		const inp = event?.currentTarget as HTMLInputElement;
		const lab = document.querySelector(".file") as HTMLLabelElement
		const icon = document.createElement("i");
		
		icon.classList.add("fa-solid");
		icon.classList.add("fa-circle-check");
		this.signin.file = inp.files!.item(0);
		lab.textContent = inp.files!.item(0)!.name + "\xa0\xa0";
		lab.appendChild(icon);
	}

	//function that check the input date in particular che if the user 
	//is at least 18 years old
	public checkDate() : boolean
	{
		const inp = document.querySelector("#birthDate") as HTMLInputElement;
		const dateLabel = document.querySelector(".dateLabel") as HTMLLabelElement;
		const splitted = this.signin.date.split("-");

		if (!(new Date(Number(splitted[0])+18, Number(splitted[1])-1, Number(splitted[2])) <= new Date()))
		{
			inp.style.backgroundColor = "var(--darkErr-col)"
			this.errors[1] = true;
			if (!dateLabel.innerText.includes("(you must be adult!)"))
				dateLabel.innerText = dateLabel.innerText + "(you must be adult!)";
			return (false);
		}
		dateLabel.innerText = "Birth Date: "
		inp.style.backgroundColor = "white"
		this.errors[1] = false;
		return (true);
	}

	//function that ched the email and set the ui error if needed
	public checkEmail() : boolean
	{
		const email = document.querySelector("#email") as HTMLInputElement;
		const emailLab = document.querySelector(".emailLabel") as HTMLLabelElement;

		if (!this.signin.email.match(this.emailReg) || this.signin.email == "")
		{
			email.style.backgroundColor = "var(--darkErr-col)"
			if (!emailLab.innerText.includes("(email not valid)"))
				emailLab.innerText = emailLab.innerText + "(email not valid)"
			this.errors[0] = true;
			return (false);
		}
		email.style.backgroundColor = "white"
		emailLab.innerText = "Email: "
		this.errors[0] = false;
		return (true);
	}

	//function that check the signup password with regex expression for
	//uppercase letter number special character and min len
	public rPassCheck()
	{
		//check password length
		!(this.signin.password.length > 8 && this.signin.password.length < 256) ? this.errors[2] = this.rcheckMsg("password length range (8-256)") : this.rremoveMsg("password length range (8-256)");
		//check the presence of at least one number
		!this.signin.password.match(/[0123456789]/) ? this.errors[2] = this.rcheckMsg("password must contain at least one number") : this.rremoveMsg("password must contain at least one number");
		//check the presence of at least one special character
		!this.signin.password.match(/[!@#$%^&*()_+\-=ˆ\[\]{};:'",.<>?~]/) ? this.errors[2] = this.rcheckMsg("password must contain at least one special character") : this.rremoveMsg("password must contain at least one special character");
		//check the presence of at least one uppercase letter
		!this.signin.password.match(/[QWERTYUIOPASDFGHJKLZXCVBNM]/) ? this.errors[2] = this.rcheckMsg("password must contain at least one uppercase letter") : this.rremoveMsg("password must contain at least one uppercase letter");
		//check if the repeated password match the first one
		this.signin.password === this.signin.confirm_password ? this.rremoveMsg("password does not match!"): this.errors[2] = this.rcheckMsg("password does not match!")
		if (this.validate(this.signin.password) && this.validate(this.signin.confirm_password) && this.signin.password == this.signin.confirm_password)
		{
			this.errors[2] = false;
			this.error_text = [];
		}
	}

	//handle data when signup button is clicked
	public signupSubmit()
	{
		const formEl = document.querySelector("#signUp") as HTMLFormElement;
		const h1El = document.querySelector("#header") as HTMLHeadingElement;
		const formData = new FormData(formEl);
		const data: { [key: string]: any } = {};

		if (this.performCheck() && this.signin.username != "" && this.signin.name != "" && this.signin.surname != "" && this.validate(this.signin.password) && this.validate(this.signin.confirm_password) && this.signin.confirm_password == this.signin.password && this.signin.file != null)
		{
			h1El.innerText = "Sign up";
			formEl.style.border = 'unset'; 
			h1El.style.color = "var(--text-col)";
			formData.delete("profilePicture");
			formData.delete('rPassword');
			formData.set('password', sha256(this.signin.password));
			formData.forEach((value, key) => {
				if (key == "username")
				{
					let str = value as string;
					
					data[key] = str.trim()
				}
				else
					data[key] = value;
			});
			this.api.signup(data).then((data)=>{
				handleSignupRequest(data, this, formEl, this.api, this.route
			)})
			.catch((err)=>{
				console.log("SIGNUPGIN ERROR: ", err);
			})
		}
		else
		{
			if (!h1El.innerText.includes("(check your input!)"))
				h1El.innerText = h1El.innerText + ("\xa0(check your input!)");
			formEl.style.border = '3px solid var(--darkErr-col)'; 
			h1El.style.color = "var(--darkErr-col)";
			formEl.classList.toggle("shake");
			setTimeout(() => {
				formEl.classList.toggle("shake");
			}, 500);
			if (this.signin.password == "" || this.signin.confirm_password == "")
				this.errors[2] = true
		}
	}
	
	constructor(protected api: ApiCallService, protected route: Router) { }
}
