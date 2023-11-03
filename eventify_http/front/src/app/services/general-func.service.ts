import { Injectable } from '@angular/core';
import jwtDecode from 'jwt-decode';
import Language from '../../assets/languageInterface'
import language from '../../assets/label'

@Injectable({
	providedIn: 'root'
})
export class GeneralFuncService {
	actualLanguage:	string = "en";
	languages:		Language = language;
	label:			any = this.languages[this.actualLanguage];

	//get the JWT cookie as a string
	getJwtString()
	{
		let jwtVal: any = "";

		for (let cookie of document.cookie.split('; '))
		{
			if (cookie.substring(0, 20) == "authorization=Bearer")
			{
				jwtVal = cookie.substring(23);
				return (jwtVal);
			}
		}
		return (jwtVal);
	}
	
	//get JWT and decode it making it readable
	getJwt()
	{
		let jwtVal: any = "";

		for (let cookie of document.cookie.split('; '))
		{
			if (cookie.substring(0, 20) == "authorization=Bearer")
			{
				jwtVal = jwtDecode(cookie.substring(23));
				return (jwtVal);
			}
		}
		return (jwtVal);
	}

	//once JWT is readable check that role match ADMIN
	//the function return true flase otherwise 
	isAdmin()
	{
		if (this.getJwt().scope == "ADMIN")
			return (true)
		return (false)
	}

	//first check if JWT is present then return the name field(sub)
	getUserName()
	{
		if (this.getJwt() != "")
			return (this.getJwt().sub);
		return (null)
	}

	//simply check if the jwt is present and verified if so return true
	isLogged()
	{
		if (this.getJwt() != "")
			return (true)
		return (false)
	}

	//function that switch language when language option element value change
	//the new leanguage come from label.ts in /assets
	switchLanguage()
	{
		const newLang = document.querySelector("#lan") as HTMLSelectElement;

		this.label = this.languages[newLang.value];
		sessionStorage.setItem("language", newLang.value)
	}
	constructor() { }
}
