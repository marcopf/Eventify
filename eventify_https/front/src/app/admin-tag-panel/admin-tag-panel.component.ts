import { Component, HostListener } from '@angular/core';
import { ApiCallService } from '../services/api-call.service';
import { GeneralFuncService } from '../services/general-func.service';

function enableEditTag(event: any)
{
	event.target.classList.remove("fa-pencil");
	event.target.classList.add("fa-check");
	event.target.style.backgroundColor = "var(--ok-col)"
	event.target.parentNode.firstChild.readOnly = false;
}

@Component({
	selector: 'app-admin-tag-panel',
	templateUrl: './admin-tag-panel.component.html',
	styleUrls: ['./admin-tag-panel.component.css']
})
export class AdminTagPanelComponent {
	inpLen:		number = 1;
	counter:	number = 2;
	isLoaded:	boolean = false;
	tags:		{'categories': Array<string>} = {'categories': []}

	//handle the deletion of the "add tag element" from the list 
	@HostListener ('click', ['$event'])
	onClick($event: any)
	{
		if ($event.target.classList.contains("delete") && $event.target.parentNode.firstChild.name.substring(0, 3) == "inp" && $event.target.parentNode.firstChild.name != "inp1")
		{
			$event.target.parentNode.classList.add("wrapFadeOut");
			setTimeout(() => {
				$event.target.parentNode.remove();
			}, 500);
		}
		else if ($event.target.classList.contains("pencil"))
			enableEditTag($event);
		if ($event.target.classList.contains("fa-check"))
			console.log($event.target.parentNode.firstChild.value);//api for edit tag value
	}

	//create a new "add tag element" and append it to the list so that user can add 
	//as many tag as the want all at once
	addTag()
	{
		const toAppend = document.querySelector(".createBox") as HTMLDivElement;
		const newInp = document.querySelector(".myInput")?.cloneNode() as HTMLInputElement;
		const newDel = document.querySelector(".delete")?.cloneNode() as HTMLDivElement;
		const newTagLine = document.querySelector(".inputWrap")?.cloneNode() as HTMLDivElement;
		
		this.inpLen = document.querySelectorAll(".myInput").length + 1;
		newInp.value = "";
		newInp.name = "inp" + this.counter;
		newInp.classList.add("inp" + this.counter++);
		newTagLine.appendChild(newInp);
		newTagLine.appendChild(newDel);
		toAppend.appendChild(newTagLine);
	}

	//collect all the new tags inserted pack togheted and send it all to backend
	submitNewTag()
	{
		const	inputList = document.querySelectorAll('.myInput') as NodeListOf<HTMLInputElement>;
		let		data: {'categories': Array<string>} = {'categories': []}

		for (let i = 0; i < inputList.length; i++)
			data.categories.push(inputList[i].value.trim());
		this.api.addTags(data).then((res)=>{
			if (res)
			{
				this.isLoaded = false;
				this.api.getTags()
				.then((data)=>{
					this.tags = data as unknown as {'categories': Array<string>};
					this.isLoaded = true;
				})
				.catch((err)=>{
					console.log("ERROR RETRIEVING TAGS ", err)
				})
			}
			else
				alert("Error adding tags...")
		})
	}

	//on first click make the input editable on second click submit the call to modify the selected tag
	enableOrSubmit($event: any)
	{
		if ($event.target.classList.contains("fa-check"))
		{
			this.api.modifyTag($event.target.parentNode.firstChild.id, $event.target.parentNode.firstChild.value).then((res)=>{
				if (res)
				{
					this.isLoaded = false;
					this.api.getTags().then((data)=>{
						this.tags = data as unknown as {'categories': Array<string>};
						this.isLoaded = true;
					}).catch((err)=>{
						console.log("ERROR RETRIEVING TAGS ", err)
					})
				}
				else
				{
					alert("Error Editing "+  $event.target.parentNode.firstChild.value);
				}
			})
		}
	}

	//handle the remove of single tag ask to confirm 
	removeTag($event: any)
	{
		if (confirm("Do you really want to delete this tag"))
		{
			this.api.removeTag($event.target.parentNode.firstChild.value).then((res)=>{
				if (res)
				{
					this.isLoaded = false;
					this.api.getTags()
					.then((data)=>{
						this.tags = data as unknown as {'categories': Array<string>};
						this.isLoaded = true;
					})
					.catch((err)=>{
						console.log("ERROR RETRIEVING TAGS ", err)
					})
				}
				else
					alert("Error removing " + $event.target.parentNode.firstChild.value);
			}).catch((err)=>{
				console.log("REMOVING TAG: ", err);
			})
		}
	}

	constructor(protected api: ApiCallService, protected gFunc: GeneralFuncService)
	{
		this.api.getTags()
		.then((data)=>{
			this.tags = data as unknown as {'categories': Array<string>};
			this.isLoaded = true;
		})
		.catch((err)=>{
			console.log("ERROR RETRIEVING TAGS ", err)
		})
	}
}
