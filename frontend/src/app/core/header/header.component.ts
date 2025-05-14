import { ChangeDetectionStrategy, Component } from '@angular/core';
import {TuiButton, TuiIcon} from '@taiga-ui/core';
import {TuiSegmented} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {RouterLink, RouterLinkActive} from '@angular/router';

import {LocationDto} from '../../dto/location.dto'
import {SearchService} from '../../services/search.service'

@Component({
  selector: 'app-header',
  imports: [
    TuiButton,
    TuiSegmented,
    TuiLogoComponent,
    TuiHeaderComponent,
    TuiIcon,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  constructor(private searchService: SearchService) {}

  filteredLocationList: LocationDto[] = [];


  getResults(query: string) {
    if (!query) {
        this.filteredLocationList = [];
        return;
    }
    this.searchService.searchLocation(query).subscribe({
      next: (locations: LocationDto[]) => {
        this.filteredLocationList = locations;
        console.log(locations);
        console.log("Received locations!")
      },
      error: (error) => {
        console.error('Error fetching locations:', error);
      },
      complete: () => {
        // Optional: Handle completion if needed
      }
    });


    console.log(this.searchService.searchLocation(query));
    }
}
