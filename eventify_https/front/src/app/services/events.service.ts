import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

interface event {
	title: 			string,
	images:			Array<string>,
	participants:	Array<string>,
	description:	string,
	mapsUrl:		string,
	eventDate:		string,
	category:		Array<string>,
	place:			string,
	owner:			string,
	status:			boolean,
	imageN:			number
}

@Injectable({
	providedIn: 'root'
})
export class EventsService {
	events:				Array<event> = [];
	value:				string = "";
	adminView:			boolean = false;
	isMyEvents:			boolean = false;
	isSearchVisible:	boolean = false;

	constructor(public route: Router) { }
}
