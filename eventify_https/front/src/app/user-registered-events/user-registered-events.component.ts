import { Component } from '@angular/core';
import { EventsService } from '../services/events.service';
import { ApiCallService } from '../services/api-call.service';
import { GeneralFuncService } from '../services/general-func.service';

@Component({
  selector: 'app-user-registered-events',
  templateUrl: './user-registered-events.component.html',
  styleUrls: ['./user-registered-events.component.css']
})
export class UserRegisteredEventsComponent {
	flag:				boolean = false;
	isLoading:			boolean = true;
	pageCounter:		number = 1;
	componentLoaded:	boolean = false;
	isKnobRight:		boolean = false;
	parent_objs:		JSON[] | [] = [];
	
	setListenerForInfiniteScroll()
	{
		// if (this.componentLoaded)
		// 	return;
		// document.querySelector(".cardContainer")?.addEventListener('scroll', (event)=>{
		// 	const lastEl:NodeListOf<HTMLDivElement> = document.querySelectorAll(".card");
		// 	const targetElement = event.target as HTMLElement;
		// 	const scrollTop = targetElement.scrollTop;

		// 	if ((lastEl[lastEl.length - 1].offsetTop +  lastEl[lastEl.length - 1].offsetHeight>= (scrollTop + window.innerHeight)) || this.flag)
		//  		return ;
		// 	this.flag = true;
			// this.api.getBaseEvents()
			// .then((data)=>{
			// 	for (let el of data)
			// 	{
			// 		this.events.events.push(el);
			// 		this.flag = false;
			// 	}
			// })
		// })
		// this.componentLoaded = true;
	}

	//simply check if the number of events is even
	//if not place a empti event to align the loader component
	needFiller(len: number)
	{
		if (len % 2 == 1 && window.innerWidth > 800)
			return (true)
		return (false)
	}

	//just return the width of the viewport
	getWidth()
	{
		return (window.innerWidth);
	}

	//move the slider's knob and make the api call to the 
	//relative selector registered or owned events
	swing($event: any)
	{
		if (!this.isKnobRight)
		{
			$event.target.style.transform = "translateX(80%)";
			this.isKnobRight = true;
			this.api.getOwnCreatedEvents()
			.then((data)=>{
				this.parent_objs = data;
			}).catch((err)=>{
				console.log("REGISTERED EVENTS: ", err);
			})
		}
		else
		{
			$event.target.style.transform = "translateX(-80%)";
			this.isKnobRight = false;
			this.api.getRegisteredEvents()
			.then((data)=>{
				this.parent_objs = data;
			}).catch((err)=>{
				console.log("REGISTERED EVENTS: ", err);
			})
		}
	}

	
	ngOnInit(): void {
		this.events.value? this.events.value = '': '';
		if (document.querySelector(".sideBar") != null)
		{
			const homeEl = document.querySelector(".home") as HTMLDivElement;

			homeEl.style.width = "90svw";
			if (this.getWidth() < 1000)
				homeEl.style.width = "100svw";
		}
	}

	constructor (protected events: EventsService, private api: ApiCallService, protected gFunc: GeneralFuncService) {
		this.api.getRegisteredEvents()
		.then((data)=>{
      		this.parent_objs = data;
      		this.isLoading = false;
		}).catch((err)=>{
			console.log("REGISTERED EVENTS: ", err);
		})
	}
}
