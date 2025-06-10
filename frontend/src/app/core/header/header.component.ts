import {ChangeDetectionStrategy, ChangeDetectorRef, Component} from '@angular/core';
import {TuiButton, TuiDataListComponent, TuiDropdown, TuiIcon, TuiSizeL, TuiSizeS, TuiTextfield} from '@taiga-ui/core';
import {TuiDataListDropdownManager, TuiSegmented} from '@taiga-ui/kit';

import {TuiHeaderComponent, TuiLogoComponent} from '@taiga-ui/layout';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';

import {SearchService} from '../../services/search.service'
import {BuildingEntity} from '../../dto/db_entity/BuildingEntity';
import { supabase } from '../../user-management/supabase.service';
import { CommonModule } from '@angular/common';

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
    RouterLink,
    RouterLinkActive,
    TuiDropdown,
    TuiDataListDropdownManager,
    TuiDataListComponent
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
}
