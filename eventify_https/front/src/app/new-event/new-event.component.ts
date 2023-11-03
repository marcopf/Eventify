import { Component } from '@angular/core';
import { NewEventService } from '../services/new-event.service';
import { ApiCallService } from '../services/api-call.service';
import { GeneralFuncService } from '../services/general-func.service';
import { Router } from '@angular/router';

function collectAndFormatData(newEvent: NewEventService, list: Array<string>)
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
	delete data['time'];
	data['place'] = data['place'].trim()
  	data['categories'] = list;
	console.log(data)
	return (data);
}

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
  selector: 'app-new-event',
  templateUrl: './new-event.component.html',
  styleUrls: ['./new-event.component.css']
})
export class NewEventComponent {
  tags:		{"categories": Array<string>} = {"categories": []}
  isLoaded:	boolean = false;
  input:	string = "";
  
 
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

	//handle the submit button preparing all data and images to be sent to server
	submit()
	{
		let data: { [key: string]: any } = {};
		let formImage: FormData;

		if (this.newEvent.checkValue())
		{
			data = collectAndFormatData(this.newEvent, this.newEvent.newEvent.category);
			formImage = collectImageAndFormat();
			if (!this.newEvent.dataErr && !this.newEvent.imageErr)
			{
				this.api.createEvent(data, formImage).then((data)=>{
					if (data == 1)
						this.newEvent.dataErr = true;
					if (data == 2)
					this.newEvent.imageErr = true;
				})
				.catch((err)=>{
					console.log(err);
				})
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
		  this.newEvent.newEvent.category.push($event.target.innerHTML);
		  $event.target.classList.add("pressed");
		  $event.target.classList.remove("not-pressed");
	  }
	  else if ($event.target.classList.contains("pressed"))
	  {
		  this.newEvent.newEvent.category = this.newEvent.newEvent.category.filter((element: string)=>{if (element == $event.target.innerText)return (false);return (true)})
		  $event.target.classList.remove("pressed");
		  $event.target.classList.add("not-pressed");
	  }
	  console.log(this.newEvent.newEvent.category)
	}

	//store the value entered in the search bar letter by letter
  	searchFunc($event: any)
  	{
		this.input = $event.target.value
  	}
	
  	constructor(protected r: Router, protected newEvent: NewEventService, protected api: ApiCallService, protected gFunc: GeneralFuncService) {
		if (this.gFunc.getUserName() == null)
			this.api.refreshToken().then((data)=>{
				if (!data.ok)
					this.r.navigate(['/login']);
				else
					window.location.reload();
			})
		this.newEvent.emptySet(this.newEvent.newEvent)
		this.newEvent.dataErr = false;
		this.newEvent.imageErr = false;
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
