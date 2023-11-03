import { Component } from '@angular/core';
import { EventsService } from '../services/events.service';
import { ApiCallService } from '../services/api-call.service';
import { GeneralFuncService } from '../services/general-func.service';
import { Router } from '@angular/router';

interface dateTimeSearch{
	startDate:string,
	endDate: string,
	startTime: string,
	endTime: string,
}

@Component({
	selector: 'app-search-bar',
	templateUrl: './search-bar.component.html',
	styleUrls: ['./search-bar.component.css']
})
export class SearchBarComponent {
	searchInput: 		boolean = true;
	inp:				string = "title";
	isLoaded:			boolean = false;
	input:				string = "";
	isSearchVisible:	boolean = false;
	dateSearch:			dateTimeSearch = {startDate: "", endDate: "", startTime: "", endTime: ""}
	categories:			Array<string> = [];
	tags:				{"categories": Array<string>} = {"categories": []};

	//make the call to search api when in the event section user want to search by date interval
	searchByDate()
	{
		let endTimeStamp = "";
		let startTimeStamp = "";

		if (this.dateSearch.startDate != "")
			startTimeStamp = this.dateSearch.startDate + "T" + (this.dateSearch.startTime == '' ? "00:00" : this.dateSearch.startTime);
		if (this.dateSearch.endDate != "")
			endTimeStamp = this.dateSearch.endDate + "T" + (this.dateSearch.endTime == '' ? "23:59" : this.dateSearch.endTime);
		this.api.search("time", undefined, undefined, startTimeStamp != "" ? startTimeStamp : undefined, endTimeStamp != "" ? endTimeStamp : undefined, undefined)
		.then((data)=>{
			this.events.events = data;
		})
		.catch((err)=>{
			console.log("ERROR SEARCHING", err)
		})
	}

	//act as filter that read the current selected search method and call the
	//API accordngly 
	search()
	{
		const typeOfSearch = document.querySelector("#filter") as HTMLSelectElement;

		if (typeOfSearch.value == "title")
		{
			this.api.search("title", this.events.value, undefined, undefined, undefined, undefined)
			.then((data)=>{
				this.events.events = data;
			})
			.catch((err)=>{
				console.log("ERROR SEARCHING", err)
			})
		}
		if (typeOfSearch.value == "categories")
		{
			this.api.search("category", undefined, undefined, undefined, undefined, this.categories)
			.then((data)=>{
				this.events.events = data;
			})
			.catch((err)=>{
				console.log("ERROR SEARCHING", err)
			})
		}
		if (typeOfSearch.value == "place")
		{
			this.api.search("place", undefined, this.events.value, undefined, undefined, undefined)
			.then((data)=>{
				this.events.events = data;
			})
			.catch((err)=>{
				console.log("ERROR SEARCHING", err)
			})
		}
	}

	//check the current selected type of search and set variables accordingly to display 
	//the right thing in the UI
	switchInput()
	{ 
		const selectEl = document.querySelector("#filter") as HTMLSelectElement;

		this.categories = [];
		this.inp = selectEl.value;
		this.events.value = '';
		if (selectEl.value == "date")
			this.searchInput = false;
		else
			this.searchInput = true;
	}

	//store the value entered in the search bar letter by letter
	searchFunc($event: any)
	{
		this.input = $event.target.value
	}

	//manage the look of the categories whenever is pressed or released
	//adding or deleting from the array that will be passed to backend
	addTag($event: any)
	{
		if ($event.target.classList.contains("not-pressed"))
		{
			this.categories.push($event.target.innerHTML);
			$event.target.classList.add("pressed");
			$event.target.classList.remove("not-pressed");
		}
		else if ($event.target.classList.contains("pressed"))
		{
			this.categories = this.categories.filter((element: string)=>{if (element == $event.target.innerText)return (false);return (true)})
			$event.target.classList.remove("pressed");
			$event.target.classList.add("not-pressed");
		}
	}

	//simply check if tag is in the selected list if so return true
	isInList(val: string)
	{
		for (let el of this.categories)
		{
			if (val == el)
				return (true);
		}
		return (false);
	}

	constructor(protected events: EventsService, protected api: ApiCallService, protected gFunc: GeneralFuncService, protected router: Router){
		this.api.getTags()
		.then((data)=>{
			this.tags = data as unknown as {"categories": Array<string>};
			this.isLoaded = true;
		})
		.catch((err)=>{
			console.log("ERROR RETRIEVING TAGS ", err)
		})
	}
}
