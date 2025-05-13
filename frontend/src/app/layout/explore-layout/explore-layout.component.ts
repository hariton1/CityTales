import { Component } from '@angular/core';
import {SidebarComponent} from '../sidebar/sidebar.component';
import {MapViewComponent} from '../map-view/map-view.component';

@Component({
  selector: 'app-explore-layout',
  imports: [
    SidebarComponent,
    MapViewComponent
  ],
  templateUrl: './explore-layout.component.html',
  styleUrl: './explore-layout.component.scss'
})
export class ExploreLayoutComponent {

}
