import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {TuiButton, TuiDataListComponent, TuiDropdown, TuiIcon, TuiSizeL, TuiSizeS} from '@taiga-ui/core';
import {TuiDataListDropdownManager, TuiSegmented, TuiSwitch} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import { supabase } from '../../user-management/supabase.service';
import { CommonModule } from '@angular/common';
import {TuiPlatform} from '@taiga-ui/cdk';
import {FormsModule} from '@angular/forms';

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
  protected readonly platforms: ReadonlyArray<'android' | 'ios' | 'web'> = [
    'web'
  ];

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

  protected getSize(first: boolean): TuiSizeS {
    return first ? 'm' : 's';
  }

  protected handleToggle() {
    let interestFiltering = localStorage.getItem('interestFiltering');
    if (interestFiltering === 'true') {
      localStorage.setItem('interestFiltering', 'false');
    } else {
      localStorage.setItem('interestFiltering', 'true');
    }
  }
}
