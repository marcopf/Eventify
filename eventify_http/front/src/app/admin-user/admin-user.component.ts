import { Component, OnInit } from '@angular/core';
import { UsersService } from '../services/users.service';
import { EventsService } from '../services/events.service';
import { Router } from '@angular/router';
import { GeneralFuncService } from '../services/general-func.service';
import { ApiCallService } from '../services/api-call.service';

interface marked {
	users: boolean,
	tag: boolean,
	info: boolean,
	eventRemove: boolean,
}

function resetMarked(m: marked)
{
	m.users = false;
	m.tag = false;
	m.info = false;
	m.eventRemove = false;
}

@Component({
	selector: 'app-admin-user',
	templateUrl: './admin-user.component.html',
	styleUrls: ['./admin-user.component.css']
})
export class AdminUserComponent implements OnInit{
	inp_len:	number = 1;
	itemMarked:	marked = {
		users: true,
		tag: false,
		info: false,
		eventRemove: false,
	}
	
	//make the sidebar of admin section keep the icon relative to current section highlighted
	ngOnInit(): void {
		let sideEl = document.querySelector(".sideBar") as HTMLDivElement;

		if (!this.gFunc.isAdmin())
			this.router.navigate([''])
		sideEl.addEventListener('click', (event: any)=>{
			if (event.target.classList.contains("sideBar"))
				return ;
			resetMarked(this.itemMarked);
			if (event.target.classList.contains("fa-users"))
				this.itemMarked.users = true;
			if (event.target.classList.contains("fa-tags"))
				this.itemMarked.tag = true;
			if (event.target.classList.contains("fa-circle-info"))
				this.itemMarked.info = true;
			if (event.target.classList.contains("fa-calendar-xmark"))
				this.itemMarked.eventRemove = true;
		})
	}

	constructor (protected users: UsersService, protected events: EventsService, protected router: Router, protected gFunc: GeneralFuncService, protected api: ApiCallService){
	}
}
