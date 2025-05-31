import { Component } from '@angular/core';
import {FriendListComponent} from '../friend-list/friend-list.component';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-landing-page',
  imports: [FriendListComponent, CommonModule],
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {

}
