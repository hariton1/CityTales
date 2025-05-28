import {ChangeDetectionStrategy, ChangeDetectorRef, Component, inject} from '@angular/core';
import {TuiButton, TuiIcon, TuiTextfield, TUI_DARK_MODE_KEY, TuiTitle} from '@taiga-ui/core';
import {TuiSearchResults} from '@taiga-ui/experimental';
import {TuiSegmented, TuiSwitch} from '@taiga-ui/kit';

import {TuiCell, TuiHeaderComponent, TuiInputSearch, TuiLogoComponent} from '@taiga-ui/layout';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import { supabase } from '../../user-management/supabase.service';
import { CommonModule } from '@angular/common';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, filter, map, Observable, startWith, switchMap, timer} from 'rxjs';
import {read} from 'node:fs'; // <-- Add this import!

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
    TuiTextfield,
    TuiInputSearch,
    TuiSearchResults,
    RouterLink,
    RouterLinkActive,
    ReactiveFormsModule,
    TuiCell,
    TuiTitle,
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

    //Search field

    protected readonly control = new FormControl('');

    open = false;

    lastSearches: string[] = [];

  results$ = this.control.valueChanges.pipe(
    debounceTime(300),
    filter((query: string | null): query is string =>
      typeof query === 'string' && query.length > 0),// Add debounce to prevent too many requests
    filter(query => !!query), // Filter out empty/null queries
    switchMap(query => {
      return new Observable<Record<string, any[]>>(observer => {
        const results: Record<string, any[]> = {};

        // Create an array to store our subscription
        const subscriptions = [
          this.searchService.searchLocation(query).subscribe({
            next: (buildings) => {
              results['buildings'] = buildings;
              // Check if we have both results
              if ('persons' in results) {
                observer.next(results);
              }
            },
            error: (error) => observer.error(error)
          }),

          this.searchService.searchPersons(query).subscribe({
            next: (persons) => {
              results['persons'] = persons;
              // Check if we have both results
              if ('buildings' in results) {
                observer.next(results);
              }
            },
            error: (error) => observer.error(error)
          })
        ];

        // Return cleanup function
        return () => {
          subscriptions.forEach(sub => sub.unsubscribe());
        };
      });
    })
  );





  private find(query: string): Record<string, readonly any[]>{
      var results: Record<string, readonly any[]> = {};

      var buildings = this.searchService.searchLocation(query).subscribe(buildings => {
        results["buildings"] = buildings;
      });
      var persons = this.searchService.searchPersons(query).subscribe(persons => {
        results["persons"] = persons;
      });

      console.log("Found results: " + results.toString())

      return results;
    }




}
