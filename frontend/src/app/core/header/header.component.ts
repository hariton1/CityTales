import {ChangeDetectionStrategy, ChangeDetectorRef, Component, inject} from '@angular/core';
import {TuiButton, TuiIcon, TUI_DARK_MODE, TUI_DARK_MODE_KEY} from '@taiga-ui/core';
import {TuiSegmented, TuiSwitch} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {HistoricalPlaceEntity} from '../../dto/db_entity/HistoricalPlaceEntity';
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import { supabase } from '../../user-management/supabase.service';
import { CommonModule } from '@angular/common'; // <-- Add this import!

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule,
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
  filteredLocationList: BuildingEntity[] = [];
  loggedIn = false;

  constructor(private searchService: SearchService, private router: Router, private cdr: ChangeDetectorRef) {
    supabase.auth.getSession().then(({ data: { session } }) => {
      this.loggedIn = !!session;
      this.cdr.markForCheck();
    });
    supabase.auth.onAuthStateChange((_event, session) => {
      this.loggedIn = !!session;
      this.cdr.markForCheck();
    });
  }
  async logout() {
    await supabase.auth.signOut();
    this.loggedIn = false;
    this.cdr.markForCheck();
    this.router.navigate(['/login']);
  }


  getResults(query: string) {
    if (!query) {
        this.filteredLocationList = [];
        return;
    }
    this.searchService.searchLocation(query).subscribe({
      next: (locations: BuildingEntity[]) => {
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
