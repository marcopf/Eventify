import { Component, NgZone, SecurityContext, Renderer2, ElementRef, ViewChild} from '@angular/core';
import { Router } from '@angular/router'
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { EventsService } from '../services/events.service';
import { ApiCallService } from '../services/api-call.service';
import { GeneralFuncService } from '../services/general-func.service';
import {} from 'googlemaps';


@Component({
	selector: 'app-card-view',
	templateUrl: './card-view.component.html',
	styleUrls: ['./card-view.component.css']
})
export class CardViewComponent {
	@ViewChild('map') mapElement:	any;
	map!:							google.maps.Map;
	imageArray:						Array<string | null> = [];
	usersImageArray:				Array<string | null> = [];
	id:								number = 0;
	url:							SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl("");
	isLoading:						boolean = true;
	counter:						number = 1;
	userList:						boolean = false;
	event:							any = {};
	chatOpen:						boolean = false;
	isMapVisible:					boolean = false;

	modifyEvent()
	{
		this.r.navigate(['/modify-event/' + this.id])
	}
	
	replaceTCharInDate()
	{
		return (this.event.eventDate.replace(/T/gi, '  '))
	}
	
	//make the images slide from right to left and go back at the end
	nextImage()
	{
		const imgs = document.querySelector(".imageSlider") as HTMLDivElement;
		
		if (this.counter < this.event.numImages)
		{
			if (window.innerWidth < 1000)
			imgs.style.transform = "translateX(" + String(this.counter * - 100) + "svw)";
			else
				imgs.style.transform = "translateX(" + String(this.counter * -30) + "svw)";
			this.counter++;
		}
		else
		{
			imgs.style.transform = "translateX(0svw)";
			this.counter = 1;
		}
	}

	//show full name when username is too long and got trimmed
	showFullName($event:any, user: string)
	{
		if ($event.target.tagName == "H3" || $event.target.tagName == "IMG")
		{
			$event.target.parentNode.classList.toggle("toggleUserInfo");
			if ($event.target.parentNode.classList.contains("toggleUserInfo"))
				$event.target.innerText = user;
			else
				$event.target.innerText = user.substring(0, 7) + "...";
			
		}
	}

	//handle the click of delete button asking to confirm deletion and do if necessary
	deleteEvent()
	{
		if (confirm("Do you really want to delete this events?"))
		{
			console.log("evento eliminato")
		}
	}

	//init the google map maps whenever the button is pressed
	initMap()
	{
		const mapProperties = {
			center: new google.maps.LatLng(Number(this.event.mapsUrl.split(",")[0]), Number(this.event.mapsUrl.split(",")[1])),
			zoom: 8,
			mapTypeId: google.maps.MapTypeId.ROADMAP
	   };
	   if (!this.isMapVisible)
	   {
		   this.map = new google.maps.Map(this.mapElement.nativeElement, mapProperties);
			this.isMapVisible = true;
			let marker = new google.maps.Marker({
				position: new google.maps.LatLng(Number(this.event.mapsUrl.split(",")[0]), Number(this.event.mapsUrl.split(",")[1])),
				map: this.map,
				title: 'Marker Title' // You can customize the marker title
			  });
	   }
	   
	}

	constructor(private route: ActivatedRoute, protected events: EventsService, private sanitizer: DomSanitizer, private r: Router, protected api: ApiCallService, protected gFunc: GeneralFuncService, protected renderer: Renderer2, protected elementRef: ElementRef, protected ngZone: NgZone){
		if (this.gFunc.getUserName() == null)
			this.api.refreshToken().then((data)=>{
				if (!data.ok)
					this.r.navigate(['/login']);
				else
					window.location.reload();
			})
		this.route.params.subscribe(params => {
			this.id = Number(params['id']);
			this.api.getSingleEvent(String(this.id))
			.then((data)=>{
				const val = this.sanitizer.sanitize(SecurityContext.URL, data? data.mapsUrl : "");
				this.url = this.sanitizer.bypassSecurityTrustResourceUrl(val ? val : "");
				this.event = data;
				this.isLoading = false;
				for (let i = 0; i < data.numImages; i++)
				{
					this.imageArray.push(null);
					this.api.getEventImage(this.imageArray, i, data.id)
				}
				for(let i = 0; i < this.event.participants.length; i++)
				{
					this.usersImageArray.push(null);
					this.api.getUserImage(this.event.participants[i], this.usersImageArray, i)
				}
			})
			.catch((err)=>{
				console.log("ERROR RETRIEVING EVENT ", err)
			})
		})
		this.events.value = '';
	}
}
