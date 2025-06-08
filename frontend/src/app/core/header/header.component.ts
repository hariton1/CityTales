import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {TuiButton, TuiIcon, TuiTextfield} from '@taiga-ui/core';
import {TuiSegmented} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import { supabase } from '../../user-management/supabase.service';
import { CommonModule } from '@angular/common';
import {TuiSwitch} from '@taiga-ui/kit';
import {FormsModule} from '@angular/forms';
import { InterestFilterService } from '../../services/interest-filter.service';
import {Breakpoints} from '@angular/cdk/layout';

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
    TuiSwitch,
    FormsModule,
    RouterLinkActive
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  filteredLocationList: BuildingEntity[] = [];
  loggedIn = false;
  interestFiltering: boolean = true;

  constructor(private searchService: SearchService, private router: Router, private cdr: ChangeDetectorRef,private interestFilterService: InterestFilterService) {
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
  onInterestFilteringChange(value: boolean) {
    this.interestFilterService.setFiltering(value);
  }

  ngOnInit() {
    this.interestFilterService.getFiltering$().subscribe(active => {
      this.interestFiltering = active;
      console.log('interestFiltering' )
    });
  }
}
