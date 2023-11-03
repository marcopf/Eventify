import { Component, OnInit } from '@angular/core';
import { SignupService } from '../services/signup.service';
import { GeneralFuncService } from '../services/general-func.service';
import { Router } from '@angular/router';

@Component({
	selector: 'app-signup',
	templateUrl: './signup.component.html',
	styleUrls: ['./signup.component.css']
})
export class SignupComponent {
	width:				number = window.innerWidth;
	passWordIsVisible:	boolean = false;
	rPassWordIsVisible:	boolean = false;

	//show/hide the password on eye icon click
	togglePasswordVisibility($event: any)
	{
		const inpEl = $event.target.parentNode.firstChild;
		if (inpEl.type == "password")
		{
			inpEl.type = "text";
			inpEl.parentNode.style.backgroundColor = "var(--bg-col)"
			inpEl.style.color = "white"
			if (inpEl.id == "password")
				this.passWordIsVisible = true;
			else
				this.rPassWordIsVisible = true;
		}
		else
		{
			inpEl.type = "password";
			inpEl.parentNode.style.backgroundColor = "white"
			inpEl.style.color = "black"
			if (inpEl.id == "password")
				this.passWordIsVisible = false;
			else
				this.rPassWordIsVisible = false;
		}
	}
	
	//set the default min value for date picker to 150 year in the past from today
	ngAfterViewInit()
	{
		const dateEl = document.querySelector("#birthDate") as HTMLInputElement
		const currDate = new Date()
		const year = String(currDate).split(" ")[3]
		
		dateEl.setAttribute("min", String(Number(Number(year) - 150) + "-01-01"));
	}

	constructor(protected signup: SignupService, protected gFunc: GeneralFuncService, private route: Router) {
		if (this.gFunc.isLogged())
			this.route.navigate(['/events'])  
	}
}
