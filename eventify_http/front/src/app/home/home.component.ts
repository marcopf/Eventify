import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { GeneralFuncService } from '../services/general-func.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {

  goEvents()
  {
    this.route.navigate(['/events'])
  }
  constructor(private route: Router, public gFunc: GeneralFuncService){
  }
}
