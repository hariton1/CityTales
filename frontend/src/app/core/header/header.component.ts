import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {TuiButton, TuiDataListComponent, TuiDropdown, TuiIcon, TuiSizeL, TuiSizeS} from '@taiga-ui/core';
import {TuiDataListDropdownManager, TuiSegmented, TuiSwitch} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {Router, RouterLink} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import { supabase } from '../../user-management/supabase.service';
import { CommonModule } from '@angular/common';
import {TuiPlatform} from '@taiga-ui/cdk';
import {FormsModule} from '@angular/forms';
import { Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';


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
    TuiDropdown,
    TuiDataListDropdownManager,
    TuiDataListComponent,
    TuiPlatform,
    FormsModule,
    TuiSwitch
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HeaderComponent {
  filteredLocationList: BuildingEntity[] = [];
  loggedIn = false;
  protected open = false;
  protected size: TuiSizeL | TuiSizeS = 'l';
  protected isChecked = true;
  protected isAdmin = false;
  protected readonly platforms: ReadonlyArray<'android' | 'ios' | 'web'> = [
    'web'
  ];

  isBrowser: boolean;
  ngOnInit() {
    if (typeof window !== 'undefined' && localStorage) {
      const interestFiltering = localStorage.getItem('interest_filtering');
      this.isChecked = interestFiltering === 'true';
    }
  }
  constructor(@Inject(PLATFORM_ID) private platformId: Object, private searchService: SearchService, private router: Router, private cdr: ChangeDetectorRef) {
    supabase.auth.getSession().then(({ data: { session } }) => {
      this.loggedIn = !!session;
      if(session) {
        const userRole = localStorage.getItem('user_role');
        this.isAdmin = userRole === 'Admin' || userRole === 'Moderator';
      }
      this.cdr.markForCheck();
    });
    supabase.auth.onAuthStateChange((_event, session) => {
      this.loggedIn = !!session;
      if(session) {
        const userRole = localStorage.getItem('user_role');
        this.isAdmin = userRole === 'Admin' || userRole === 'Moderator';
      }
      this.cdr.markForCheck();
    });
    this.isBrowser = isPlatformBrowser(this.platformId);
  }
  async logout() {
    await supabase.auth.signOut();
    this.loggedIn = false;
    this.cdr.markForCheck();
    localStorage.removeItem('user_uuid');
    localStorage.removeItem('user_role');
    localStorage.removeItem('interest_filtering');
    this.router.navigate(['/login']);
  }

  protected getSize(first: boolean): TuiSizeS {
    return first ? 'm' : 's';
  }

  protected handleToggle() {
    let interestFiltering = localStorage.getItem('interest_filtering');
    if (interestFiltering === 'true') {
      localStorage.setItem('interest_filtering', 'false');
      this.isChecked = false;
    } else {
      localStorage.setItem('interest_filtering', 'true');
      this.isChecked = true;
    }
    window.location.reload();
  }
}
