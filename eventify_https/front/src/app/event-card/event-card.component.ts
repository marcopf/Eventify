import { Component, HostListener, Input, OnInit } from '@angular/core';
import { EventsService } from '../services/events.service';
import { Router } from '@angular/router';
import { GeneralFuncService } from '../services/general-func.service';
import { ApiCallService } from '../services/api-call.service';

@Component({
	selector: 'app-event-card',
	templateUrl: './event-card.component.html',
	styleUrls: ['./event-card.component.css']
})
export class EventCardComponent implements OnInit{
	@Input() el:	any = {};
	small:			boolean = false;
	imageUrl:		Array<string | null> = [null];

	//call the router to navigate to the full card page passing index of event
	getFullInfo(i: number)
	{
		this.route.navigate(['/cardView/' + i])
	}

	//replace T in time stamp to display display date and time inside the card
	replaceTCharInDate()
	{
		return (this.el.dateTime.replace(/T/gi, '  '))
	}

	ngOnInit(): void {
		this.api.getEventImage(this.imageUrl, 0, this.el.id)
		.catch((err)=>{
			console.log("ERROR WHILE GETTING IMAGES", err);
		})
		if (document.querySelector(".sideBar") != null)
		{
			const cards = document.querySelectorAll(".card") as NodeListOf<HTMLDivElement>;
			
			this.small = true;
			for (let i = 0; i < cards.length; i++)
				cards[i].classList.add("admin");
		}
	}

	constructor(public events: EventsService, private route: Router, public gFunc: GeneralFuncService, protected api: ApiCallService){
	}
}
