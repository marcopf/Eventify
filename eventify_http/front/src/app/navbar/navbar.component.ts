import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { GeneralFuncService } from '../services/general-func.service';
import { SignupService } from '../services/signup.service';
import { response } from 'express';
import { ApiCallService } from '../services/api-call.service';
import { EventsService } from '../services/events.service';
import { NotificationService } from '../services/notification.service';

async function getImage() {
	fetch("http://localhost:3000/image").then((imageBlob)=>{
		return imageBlob.blob()
	}).then((imageBlob)=>{
		const imageURL = URL.createObjectURL(imageBlob);
		const imgElement = document.getElementById('test') as HTMLImageElement;
		imgElement.src = imageURL;
	})
	
}

@Component({
	selector: 'app-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit{
	open:				boolean = false;
	openC:				boolean = false;
	userWantToSearch:	boolean = false;
	imageUrl:			Array<string | null> = [];
	isKnobRight:		boolean = false;
	
	//function that handle the opening and closing of the sidebard 
	//both in desktop mode and mobile
	openSide()
	{
		let side = document.querySelector(".side") as HTMLDivElement;
		let chat = document.querySelector(".sideChat") as HTMLDivElement;
		let handle = document.querySelector(".sidebar") as HTMLDivElement;

		if (this.gFunc.getUserName() == null)
				this.api.refreshToken();
		side.style.zIndex = "1000000";
		chat.style.zIndex = "10000";
		if (this.open == true)
		{
			side.style.transform = "translateX(0vw)"
			handle.style.transform = "translateX(0)"
			if (window.innerWidth < 1000)
				handle.style.width = "20svw"
			this.open = false
			if (this.openC)
			{
				chat.style.transform = "translateX(0)"
				this.openC = false;
			}
		}
		else if (window.innerWidth < 1000)
		{
			side.style.transform = "translateY(-90svh)"
			handle.style.transform = "translateY(-90svh)"
			handle.style.width = "100svw"
			this.open = true;
		}
		else
		{
			side.style.transform = "translateX(-30vw)"
			handle.style.transform = "translateX(-30vw)"
			this.open = true;      
		}
	}
	goHome()
	{
		this.route.navigate(['']);
	}
	getWindow()
	{
		return window.innerWidth;
	}
	logout()
	{
		this.api.logout();
	}
	isLogged()
	{
		return (this.gFunc.isLogged());
	}
	getUsername()
	{
		return (this.gFunc.getUserName())
	}
	isAdmin()
	{
		return (this.gFunc.isAdmin())
	}
	
	ngOnInit(): void {
		document.querySelector(".side")?.addEventListener('click', ($event: any)=>{
			if (this.open && $event.target.tagName != "SELECT")
				this.openSide();
		})
		const selEl = document.querySelector("#lan") as HTMLSelectElement;
		if (sessionStorage.getItem("language") != null)
			selEl.value = sessionStorage.getItem("language") as string;
		else
		selEl.value = "en";
		this.gFunc.switchLanguage();
	}

	constructor(protected route: Router, private signup: SignupService, protected gFunc: GeneralFuncService, protected api: ApiCallService, protected event: EventsService, protected notification: NotificationService) {
		this.api.getUserImage(this.gFunc.getUserName(), this.imageUrl).then((data)=>{
		})
	}
}
