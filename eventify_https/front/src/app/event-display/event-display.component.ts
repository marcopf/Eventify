import { Component, OnInit } from '@angular/core';
import { EventsService } from '../services/events.service';
import { ApiCallService } from '../services/api-call.service';
import { GeneralFuncService } from '../services/general-func.service';
import { NavigationEnd, Router } from '@angular/router';

@Component({
	selector: 'app-event-display',
	templateUrl: './event-display.component.html',
	styleUrls: ['./event-display.component.css']
})
export class EventDisplayComponent implements OnInit{
	flag:					boolean = false;
	isLoading:				boolean = true;
	parent_objs:			typeof this.events.events | [] = [];
	componentLoaded:		boolean = false;
	pageCounter:			number = 0;
	infiniteScrollLoader:	boolean = false;


	//check if the event number is odd if so place an empty evemt to align the loader
	needFiller(len: number)
	{
		if (len % 2 == 1 && window.innerWidth > 800)
			return (true)
		return (false)
	}

	//return the width of the viewport
	getWidth()
	{
		return (window.innerWidth);
	}

	//set the listener that wait until the bottom of page is loaded whe that happen
	//api call is made to add more event to the current
	setListenerForInfiniteScroll()
	{
		if (this.componentLoaded)
			return;
		this.componentLoaded = true;
		document.querySelector(".cardContainer")?.addEventListener('scroll', (event)=>{
			if (this.flag)
				return;
			const lastEl:NodeListOf<HTMLDivElement> = document.querySelectorAll(".card");
			const targetElement = event.target as HTMLElement;
			const scrollTop = targetElement.scrollTop;

			if ((lastEl[lastEl.length - 1].offsetTop +  lastEl[lastEl.length - 1].offsetHeight>= (scrollTop + window.innerHeight)) || this.flag)
		 		return ;
			this.flag = true;
			this.infiniteScrollLoader = true;
			this.api.getBaseEvents(String(this.pageCounter))
			.then((data)=>{
				for (let el of data)
					this.events.events.push(el)
				if (data.length > 0)
				{
					this.flag = false;
					this.pageCounter++;
				}
				this.infiniteScrollLoader = false;
				this.isLoading = false;
			}).catch((err)=>{
				this.flag = false;
				this.infiniteScrollLoader = false;
				console.log("ERROR ADDING EVENTS ", err);
			})
		})
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

	constructor (protected events: EventsService, private api: ApiCallService, protected gFunc: GeneralFuncService, protected route: Router) {
		this.api.getBaseEvents(String(this.pageCounter))
		.then((data)=>{
			this.events.events = data;
			this.isLoading = false;
			this.pageCounter++;
		})
		.catch((err)=>{
			console.log("ERROR ADDING EVENTS ", err);
		})
		this.route.events.subscribe((event)=>{
			if (event instanceof NavigationEnd)
			{
			  if (event.url == "/events")
				this.events.isSearchVisible = true;
			  else
				this.events.isSearchVisible = false;
			}
		  })
	}
}
