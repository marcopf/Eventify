import { Component, OnInit } from '@angular/core';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';
import { GeneralFuncService } from '../services/general-func.service';
import { NotificationService } from '../services/notification.service';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
	styleUrls: ['./login.component.css']
})
export class LoginComponent{
	test:				string = "";
	width:				number = window.innerWidth;
	user:				any;
	logged:				any;
	passWordIsVisible:	boolean = false;

	//show/hide the password on eye icon click
	togglePasswordVisibility()
	{
		const inpEl = document.querySelector('#password') as HTMLInputElement;
		const passwordContainerEl = document.querySelector('.passwordCont') as HTMLInputElement;

		if (inpEl.type == "password")
		{
			inpEl.type = "text";
			passwordContainerEl.style.backgroundColor = "var(--bg-col)"
			inpEl.style.color = "white"
			this.passWordIsVisible = true;
		}
		else
		{
			inpEl.type = "password";
			passwordContainerEl.style.backgroundColor = "white"
			inpEl.style.color = "black"
			this.passWordIsVisible = false;
		}
	}

	constructor(protected login_group: LoginService, private route: Router, protected gFunc: GeneralFuncService, protected notification:NotificationService){
		if (this.gFunc.isLogged())
			this.route.navigate(['/events'])
		this.notification.getNotification();
		setInterval(()=>{
			this.notification.getNotification();
		}, (10 * (60 * 1000)))
	}
}
