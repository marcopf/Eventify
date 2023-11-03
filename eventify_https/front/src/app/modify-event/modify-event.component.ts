import { Component, SecurityContext } from '@angular/core';
import { NewEventService } from '../services/new-event.service';
import { ApiCallService } from '../services/api-call.service';
import { ActivatedRoute, Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from "@angular/platform-browser";
import { GeneralFuncService } from '../services/general-func.service';

//collect and prepare data var including all infor to be sent to server
function collectAndFormatData(newEvent: NewEventService, event: any, tags: Array<string>)
{
	const formEl = document.querySelector("#myForm") as HTMLFormElement;
	const formData = new FormData(formEl);
	const data: { [key: string]: any } = {};
	
	newEvent.dataErr = false;
	newEvent.imageErr = false;
	formData.forEach((value, key) => {
		if (value == "")
		{
			newEvent.dataErr = true;
			newEvent.imageErr = true;
			return ;
		}
		data[key] = value;
	});
	data['eventDate'] = data['eventDate'] + "T" + data['time'];
	data['mapsUrl'] = "temp";
	delete data['time'];
	data['categories'] = tags; 
	return (data);
}

//prepare the image to be sent to server
function collectImageAndFormat()
{
	const fileEl = document.querySelector("#photos") as HTMLInputElement;
	const formImage = new FormData();

	if (fileEl.files && fileEl.files.length != undefined)
	{
		for (let i = 0; i < fileEl.files.length; i++)
				formImage.append("image", fileEl.files.item(i)!)
	}
	return (formImage);
}

@Component({
	selector: 'app-modify-event',
	templateUrl: './modify-event.component.html',
	styleUrls: ['./modify-event.component.css']
})
export class ModifyEventComponent{
	isLoading:	boolean = true;
	id:			number = 0;
	url:		SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl("");
	event:		any = {};
	tags:		{"categories": Array<string>} = {"categories": []}
	isLoaded:	boolean = false;
	input:		string = "";

	//display the file name of the images selected during the event creation
	changeText()
	{
		const labelEl = document.querySelector(".fileLabel") as HTMLLabelElement;
		const fileEl = document.querySelector("#photos") as HTMLInputElement;
		const fileList = fileEl.files

		labelEl.innerHTML = "";
		labelEl.style.fontSize = "small"
		if (fileList != null)
		{
			for (let i = 0; i < fileList.length; i++)
				labelEl.innerHTML += (i + 1) + "- " + fileList.item(i)?.name + "<br>";
		}
	}

	//get the time or date dependig from parameter passed
	//makeng a split on the T char that is a common separate for normal timestamp
	splitTime(par: string)
	{
		if (par == "time")
			return (this.event.eventDate.substring(this.event.eventDate.indexOf("T") + 1))
		else
			return (this.event.eventDate.substring(0, this.event.eventDate.indexOf("T")))
	}

	//make the event tags already selected in the modify view
	filterTags(selectedTag: string)
	{
		for (let el of this.event.categories)
		{	
			if (selectedTag == el)
				return (true)
		}
		return (false);
	}

	//handle the event modification collecting data and making the necessary check
	modifyCurrentEvent()
	{
		let data: { [key: string]: any } = {};
		let formImage: FormData;

		if (this.newEvent.checkValueModify())
		{
			data = collectAndFormatData(this.newEvent, this.event, this.event.categories);
			formImage = collectImageAndFormat();
			if (!this.newEvent.dataErr && !this.newEvent.imageErr)
			{
				console.log(data, formImage) //api to screate new event
				this.api.modifyEvent(data, formImage, this.event.id)
			}
		}
		else
		{
			this.newEvent.dataErr = true;
			this.newEvent.imageErr = true;
		}
	}

	//manage the look of the categories whenever is pressed or released
	//adding or deleting from the array that will be passed to backend
	addTag($event: any)
	{
		if ($event.target.classList.contains("not-pressed"))
		{
			this.event.categories.push($event.target.innerHTML);
			$event.target.classList.add("pressed");
			$event.target.classList.remove("not-pressed");
		}
		else if ($event.target.classList.contains("pressed"))
		{
			this.event.categories = this.event.categories.filter((element: string)=>{if (element == $event.target.innerText)return (false);return (true)})
			$event.target.classList.remove("pressed");
			$event.target.classList.add("not-pressed");
		}
	}

	//store the value entered in the search bar letter by letter
	searchFunc($event: any)
	{
	  this.input = $event.target.value
	}
	constructor(protected r: Router, protected newEvent: NewEventService, protected api: ApiCallService, private route: ActivatedRoute, private sanitizer: DomSanitizer, protected gFunc: GeneralFuncService) {
		if (this.gFunc.getUserName() == null)
			this.api.refreshToken().then((data)=>{
				if (!data.ok)
					this.r.navigate(['/login']);
				else
					window.location.reload();
			})
		this.newEvent.imageErr = false;
		this.newEvent.dataErr = false;
		this.route.params.subscribe(params => {
			this.id = Number(params['id']);
			this.api.getSingleEvent(String(this.id))
			.then((data1)=>{
				const val = this.sanitizer.sanitize(SecurityContext.URL, data1? data1.mapsUrl : "");
				this.url = this.sanitizer.bypassSecurityTrustResourceUrl(val ? val : "");
				this.event = data1;
				this.isLoading = false;
			})
			.catch((err)=>{
				console.log("ERROR RETRIEVING EVENT ", err)
			  })
			this.api.getTags()
			.then((data)=>{
				  this.tags = data as unknown as {"categories": Array<string>};
				  this.isLoaded = true;
			})
			.catch((err)=>{
				console.log("ERROR RETRIEVING TAGS ", err)
			  })
		})
	}
}
