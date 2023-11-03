import { Component } from '@angular/core';
import { SignupService } from '../services/signup.service';
import { GeneralFuncService } from '../services/general-func.service';

@Component({
  selector: 'app-email-alert',
  templateUrl: './email-alert.component.html',
  styleUrls: ['./email-alert.component.css']
})
export class EmailAlertComponent {

  constructor(protected signup: SignupService, protected gFunc: GeneralFuncService){
  }
}
