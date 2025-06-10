import {ChangeDetectionStrategy, ChangeDetectorRef, Component, NgZone} from '@angular/core';
import {NgForOf, NgIf} from '@angular/common';
import {TuiTabBar} from '@taiga-ui/addon-mobile';
import {
  TuiButton,
  TuiDataListComponent,
  TuiDropdownDirective,
  TuiDropdownOpen,
  TuiIcon,
  TuiSizeL,
  TuiSizeS
} from '@taiga-ui/core';
import {SearchService} from '../../services/search.service';
import {Router, RouterLink} from '@angular/router';
import {supabase} from '../../user-management/supabase.service';
import {TuiDataListDropdownManager} from '@taiga-ui/kit';

@Component({
  selector: 'app-mobile-navigation',
  imports: [
    NgForOf,
    TuiTabBar,
    RouterLink,
    TuiDataListComponent,
    TuiDataListDropdownManager,
    TuiIcon,
    NgIf,
    TuiButton,
    TuiDropdownDirective,
    TuiDropdownOpen
  ],
  templateUrl: './mobile-navigation.component.html',
  styleUrl: './mobile-navigation.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MobileNavigationComponent {
  protected activeItemIndex = 0;

  loggedIn: boolean = false;
  protected open: boolean = false;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    protected ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.checkSession();
    this.listenToAuthChanges();
  }

  private checkSession(): void {
    supabase.auth.getSession().then(({ data: { session } }) => {
      this.ngZone.run(() => {
        this.loggedIn = !!session;
        this.cdr.markForCheck();
      });
    });
  }

  private listenToAuthChanges(): void {
    supabase.auth.onAuthStateChange((_event, session) => {
      this.ngZone.run(() => {
        this.loggedIn = !!session;
        this.cdr.markForCheck();
      });
    });
  }

  async logout(): Promise<void> {
    await supabase.auth.signOut();
    this.ngZone.run(() => {
      this.loggedIn = false;
      this.cdr.markForCheck();
      this.router.navigate(['/login']);
    });
  }

}
