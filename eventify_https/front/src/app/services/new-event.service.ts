import { Injectable } from '@angular/core';

type newEvent = {
	title: string,
	description: string,
	place: string,
	date: string,
	category: Array<string>
	time: string
}

//actual functio that make the date comparison
function isDateInFuture(dateToCheck: string) {
	const currentDate = new Date();
	const inputDate = new Date(dateToCheck);

	return inputDate > currentDate;
}

@Injectable({
	providedIn: 'root'
})
export class NewEventService {
	dataErr:	boolean = false;
	imageErr:	boolean = false;
	newEvent:	newEvent = {
		title: "",
		description: "",
		place: "",
		date: "",
		time: "",
		category: []
	}

	emptySet(ev: newEvent)
	{
		ev.title = "";
		ev.description = "";
		ev.place = "";
		ev.date = "";
		ev.category = [];
		ev.time = "";
	}

	//function that check if the input date for new event is actually in the future
	checkValue()
	{
		const	fileEl = document.querySelector("#photos") as HTMLInputElement;
		const	dateEl = document.querySelector("#date") as HTMLInputElement;
		let		flag: Boolean = true;
		
		Object.keys(this.newEvent).forEach((key)=>{
			if (this.newEvent[key as keyof typeof this.newEvent] == "")
				flag = false;
		})
		if (fileEl.files!.length <= 2)
			flag = false;
		if (!isDateInFuture(dateEl.value))
			flag = false;
		return (flag);
	}

	//function that check if the input date for modify event is actually in the future
	checkValueModify()
	{
		const	dateEl = document.querySelector("#date") as HTMLInputElement;
		let		flag: Boolean = true;

		if (!isDateInFuture(dateEl.value))
			flag = false;
		return (flag);
	}

	constructor() { }
}
