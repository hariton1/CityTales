import {ChangeDetectionStrategy, Component, inject} from '@angular/core';
import {TuiButton, TuiIcon, TUI_DARK_MODE, TUI_DARK_MODE_KEY} from '@taiga-ui/core';
import {TuiSegmented, TuiSwitch} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {RouterLink, RouterLinkActive} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';

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

  filteredLocationList: HistoricalPlaceEntity[] = [];


  getResults(query: string) {
    if (!query) {
        this.filteredLocationList = [];
        return;
    }
    this.searchService.searchLocation(query).subscribe({
      next: (locations: HistoricalPlaceEntity[]) => {
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
